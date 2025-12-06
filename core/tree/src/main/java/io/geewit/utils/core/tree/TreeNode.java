package io.geewit.utils.core.tree;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 简单树节点
 * @param <N> 树节点类型
 * @param <Key> 主键类型
 * @author geewit
 */
@FieldNameConstants
@Setter
@Getter
@ToString
public abstract class TreeNode<N extends TreeNode<N, Key>, Key extends Serializable> {
    /**
     * 主键
     */
    protected Key id;

    /**
     * 父级id
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

    /**
     * 子级节点
     */
    protected List<N> children = new ArrayList<>();

    /**
     * 添加子节点到当前节点的子节点列表中
     * 如果子节点列表为空，则创建新的列表并添加该子节点
     * 如果子节点列表不为空，则检查是否已存在相同ID的子节点，如果不存在则添加
     *
     * @param child 要添加的子节点，不能为null
     */
    public void addChild(N child) {
        // 如果子节点列表为空，创建新的列表并添加子节点
        if(children == null) {
            children = Stream.of(child).collect(Collectors.toList());
        } else {
            // 检查子节点列表中是否已存在相同ID的节点，避免重复添加
            if (children.stream().filter(Objects::nonNull).map(TreeNode::getId).filter(Objects::nonNull).noneMatch(id -> id.equals(child.getId()))) {
                children.add(child);
            }
        }
    }

    /**
     * 清空对象的所有属性值
     * <p>
     * 将当前对象的所有属性设置为null，包括id、父级信息和子级信息等属性
     * </p>
     */
    public void clear() {
        this.id = null;
        this.parentId = null;
        this.parentIds = null;
        this.parent = null;
        this.children = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TreeNode)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        TreeNode<N, Key> that = (TreeNode<N, Key>) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
