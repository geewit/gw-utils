package io.geewit.shadow.transformers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jengelman.gradle.plugins.shadow.transformers.ResourceTransformer;
import com.github.jengelman.gradle.plugins.shadow.transformers.TransformerContext;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * JsonMergingTransformer
 * 将所有匹配的 JSON 中的 "groups" 和 "properties" 合并，
 * 最终分别写回各自的配置路径，避免同名文件互相覆盖。
 * 适配 Shadow 9.x:
 * - 实现 ResourceTransformer 接口
 * - 使用 TransformerContext#getInputStream()
 */
public class JsonMergingTransformer implements ResourceTransformer {

    private static final Logger log = Logging.getLogger(JsonMergingTransformer.class);

    private static final String JSON_SUFFIX = ".json";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // 从 build.gradle 注入要合并的资源路径列表
    @Input
    private final List<String> paths = new ArrayList<>();

    // 按文件路径存储合并后的 groups / properties
    private final Map<String, ArrayNode> groupsMap = new LinkedHashMap<>();
    private final Map<String, ArrayNode> propertiesMap = new LinkedHashMap<>();

    private boolean hasTransformed = false;

    public void setPaths(List<String> paths) {
        this.paths.clear();
        if (paths != null) {
            this.paths.addAll(paths);
        }
        log.debug("JsonMergingTransformer paths: {}", this.paths);
    }

    public List<String> getPaths() {
        return Collections.unmodifiableList(paths);
    }

    @Override
    public boolean canTransformResource(FileTreeElement element) {
        String path = element.getRelativePath().getPathString();
        // 优先精确匹配
        if (paths.contains(path)) {
            return true;
        }
        // 再支持正则匹配
        // 如果没有配置 paths，则默认处理所有 .json 资源
        return paths.stream().anyMatch(path::matches) || (paths.isEmpty() && path.endsWith(JSON_SUFFIX));
    }

    @Override
    public void transform(TransformerContext context) {
        hasTransformed = true;

        String path = context.getPath();
        ArrayNode mergedGroups = groupsMap.computeIfAbsent(path, p -> OBJECT_MAPPER.createArrayNode());
        ArrayNode mergedProperties = propertiesMap.computeIfAbsent(path, p -> OBJECT_MAPPER.createArrayNode());

        // Shadow 9.x: 使用 context.getInputStream()
        try (InputStream is = context.getInputStream()) {
            JsonNode root = OBJECT_MAPPER.readTree(is);

            JsonNode groups = root.get("groups");
            if (groups != null && groups.isArray()) {
                groups.forEach(mergedGroups::add);
                log.debug("Merged {} group entries from {}", groups.size(), path);
            }

            JsonNode props = root.get("properties");
            if (props != null && props.isArray()) {
                props.forEach(mergedProperties::add);
                log.debug("Merged {} property entries from {}", props.size(), path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to merge JSON resource: " + path, e);
        }
    }

    @Override
    public boolean hasTransformedResource() {
        return hasTransformed;
    }

    @Override
    public void modifyOutputStream(@NonNull ZipOutputStream zos, boolean preserveFileTimestamps) {
        if (!hasTransformed) {
            return;
        }

        // 只对真正出现过的路径写回结果，保持行为与之前一致（不会新增多余文件）
        for (String path : paths.isEmpty() ? unionKeys() : paths) {
            ArrayNode groups = groupsMap.get(path);
            ArrayNode properties = propertiesMap.get(path);

            if (groups == null && properties == null) {
                continue;
            }

            ObjectNode mergedRoot = OBJECT_MAPPER.createObjectNode();
            mergedRoot.set("groups",
                    groups != null ? groups : OBJECT_MAPPER.createArrayNode());
            mergedRoot.set("properties",
                    properties != null ? properties : OBJECT_MAPPER.createArrayNode());

            byte[] data;
            try {
                data = OBJECT_MAPPER
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsBytes(mergedRoot);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize merged JSON for " + path, e);
            }

            log.debug("Writing merged metadata to {}", path);
            ZipEntry entry = new ZipEntry(path);
            try {
                zos.putNextEntry(entry);
                zos.write(data);
                zos.flush();
                zos.closeEntry();
            } catch (IOException e) {
                throw new RuntimeException("Failed to write merged JSON resource: " + path, e);
            }
        }
    }

    /**
     * 当 paths 为空时，按实际收集到的 key 写回，保持原有行为。
     */
    private Set<String> unionKeys() {
        Set<String> keys = new LinkedHashSet<>();
        keys.addAll(groupsMap.keySet());
        keys.addAll(propertiesMap.keySet());
        return keys;
    }

    @Override
    public String getName() {
        return "JsonMergingTransformer";
    }
}
