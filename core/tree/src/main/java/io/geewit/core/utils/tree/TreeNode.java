package io.geewit.core.utils.tree;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 树节点
 * @author geewit
 */
@Setter
@Getter
public abstract class TreeNode<N extends TreeNode<N, Key>, Key extends Serializable> {
    /**
     * 主键
     * @return
     */
    protected Key id;

    /**
     * 父级id
     * @return
     */
    protected Key parentId;

    protected String parentIds;

    /**
     * 父级节点
     * @return
     */
    protected N parent;


    protected Set<N> children;

    public void addChild(N treeNode) {
        if(this.children == null) {
            this.children = Stream.of(treeNode).collect(Collectors.toSet());
        } else {
            this.children.add(treeNode);
        }
    }
}
