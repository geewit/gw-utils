package io.geewit.core.utils.tree;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.geewit.utils.uuid.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
public class TreeTest {
    private final static Logger logger = LoggerFactory.getLogger(TreeTest.class);

    private Set<Org> roots;

    private List<Org> tree;

    @BeforeEach
    public void init() {
        int maxLevel = 5;
        int maxIndex = 5;
        roots = this.buildTree(maxLevel, maxIndex);
    }

    private Set<Org> buildTree(int maxLevel, int maxIndex) {
        List<Org> orgList = new ArrayList<>();
        Set<Org> roots = new HashSet<>();
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
                    roots.add(org);
                }
                orgList.add(org);
            }
        }
        tree = TreeUtils.generateTree(orgList);
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
//            org.setParent(parent);
            org.setParentIds(parent.parentIds + id + "#");
//            parent.addChild(org);
        } else {
            org.setParentIds(id + "#");
        }
        log.info("org.id = {}, org.name = {}", id, name);
        return org;
    }

    @Test
    public void test() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            log.info("roots: " + objectMapper.writeValueAsString(roots));
            log.info("tree: " + objectMapper.writeValueAsString(tree));
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage());
        }
    }
}
