package io.geewit.core.utils.tree;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 简单树节点
 * @author geewit
 */
@SuperBuilder
@FieldNameConstants
@Setter
@Getter
@ToString
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
    /**
     * 路径枚举
     */
    protected String parentIds;
    /**
     * 父级节点
     */
    protected N parent;

    @Builder.Default
    protected List<N> children = new ArrayList<>();

    public void addChild(N child) {
        if(children == null) {
            children = Stream.of(child).collect(Collectors.toList());
        } else {
            if (children.stream().filter(Objects::nonNull).map(TreeNode::getId).filter(Objects::nonNull).noneMatch(id -> id.equals(child.getId()))) {
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
        TreeNode<N, Key> that = (TreeNode<N, Key>) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
