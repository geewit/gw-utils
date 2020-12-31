package io.geewit.core.utils.tree;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Org extends TreeNode<Org, Long> {
    private String name;
}
