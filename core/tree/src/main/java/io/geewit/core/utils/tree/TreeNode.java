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
     * 是否选中
     */
    protected Boolean checked;
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

    /**
     * 递归设置选中值
     */
    public void cascadeCheck() {
        //region 如果子孙为空则不设置当前选中值
        if(this.children == null || this.children.isEmpty()) {
            return;
        }
        //endregion

        for(N child : this.children) {
            //region 递归设置子孙选中值
            child.cascadeCheck();
            //endregion

            if(child.checked == null) {
                child.checked = false;
            }
            //region 如果子孙选中值为false则设置当前选中值为false
            if(!child.checked) {
                this.checked = false;
                return;
            }
            //endregion
        }

        //region 如果所有子孙选中值为true则设置当前选中值为true
        this.checked = true;
        //endregion
    }
}
