package io.geewit.core.utils.tree;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TreeTest {
    private final static Logger logger = LoggerFactory.getLogger(TreeTest.class);

    @Setter
    @Getter
    public static class Org extends TreeNode<Org, Long> {
        private String name;
    }
}
