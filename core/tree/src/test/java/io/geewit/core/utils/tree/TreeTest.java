package io.geewit.core.utils.tree;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.geewit.utils.uuid.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
public class TreeTest {
    private final static Logger logger = LoggerFactory.getLogger(TreeTest.class);

    private List<Org> roots;

    @BeforeEach
    public void init() {
        int maxLevel = 5;
        int maxIndex = 5;
        roots = this.buildTree(maxLevel, maxIndex);
    }

    private List<Org> buildTree(int maxLevel, int maxIndex) {
        List<Org> orgs = new ArrayList<>();
        for(int level = 0; level < maxLevel; level++) {
            Org parent = null;
            for(int index = 0; index < maxIndex; index++) {
                Org org;
                if(parent == null) {
                    org = this.buildOrg(level, index, null);
                } else {
                    org = this.buildOrg(level, index, parent);
                }
                if(org.parentId == null) {
                    parent = org;
                }
                orgs.add(org);
            }
        }
        roots = TreeUtils.generateTree(orgs);
        return roots;
    }

    private Org buildOrg(int level, int index, Org parent) {
        Org org = new Org();
        long id = Double.valueOf(Math.pow(10, level)).longValue() + index;
        org.setId(id);
        String name = UUIDUtils.randomUUID();
        org.setName(name);
        if(parent != null) {
            org.setParentId(parent.id);
            org.setParentIds(parent.parentIds + id + "#");
        } else {
            org.setParentIds(id + "#");
        }
//        boolean checked = new Random().nextBoolean();
//        org.setChecked(checked);
        log.info("org.id = {}, org.name = {}", id, name);
        return org;
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void test() {
        try {
            log.info("roots: " + objectMapper.writeValueAsString(roots));
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage());
        }
    }


    @Test
    public void testCheck() {
        Set<Long> checkingKeys = Stream.of(1L, 10L, 100L, 1000L, 10000L).collect(Collectors.toSet());
        TreeUtils.cascadeCheckKeys(roots, checkingKeys);
        try {
            log.info("roots: " + objectMapper.writeValueAsString(roots));
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage());
        }
    }
}
