package io.geewit.core.utils.tree;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
     * 是否选中
     */
    protected Boolean checked;
    /**
     * 父级节点
     * @return
     */
    protected N parent;

    protected List<N> children = new ArrayList<>();

    public void addChild(N child) {
        if(children == null) {
            children = Stream.of(child).collect(Collectors.toList());
        } else {
            if (children.stream().filter(Objects::nonNull).map(N::getId).filter(Objects::nonNull).noneMatch(id -> id.equals(child.getId()))) {
                children.add(child);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TreeNode)) {
            return false;
        }
        TreeNode<N, Key> treeNode = (TreeNode<N, Key>) o;
        return getId().equals(treeNode.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
