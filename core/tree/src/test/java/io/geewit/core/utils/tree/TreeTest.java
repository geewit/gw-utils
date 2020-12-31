package io.geewit.core.utils.tree;

import io.geewit.utils.uuid.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Slf4j
public class TreeTest {
    private final static Logger logger = LoggerFactory.getLogger(TreeTest.class);

    private Org root;

    @BeforeEach
    public void init() {
        int maxLevel = 5;
        int maxIndex = 5;
        root = this.buildTree(maxLevel, maxIndex);
    }

    private Org buildTree(int maxLevel, int maxIndex) {
        Org root = null;
        for(int level = 0; level < maxLevel; level++) {
            for(int index = 0; index < maxIndex; index++) {
                if(root == null) {
                    root = this.buildOrg(level, index, null);
                } else {
                    root = this.buildOrg(level, index, root);
                }
            }
        }
        return root;
    }

    private Org buildOrg(int level, int index, Org parent) {
        Org org = new Org();
        long id = Double.valueOf(Math.pow(10, level)).longValue() + index;
        String name = UUIDUtils.randomUUID();
        org.setName(name);
        if(parent != null) {
            org.setParentId(parent.id);
            parent.addChild(org);
        }
        log.info("org.id = {}, org.name = {}", id, name);
        return org;
    }

    @Test
    public void test() {

    }
}
