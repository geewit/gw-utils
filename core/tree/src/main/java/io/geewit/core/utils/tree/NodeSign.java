package io.geewit.core.utils.tree;


import java.io.Serializable;

/**
 * 树节点标记
 * @author geewit
 */
public interface NodeSign<Key extends Serializable> {

    /**
     * 节点id
     */
    Key getId();

    /**
     * 标记
     */
    Integer getSign();

    /**
     * 是否向下传递标记
     */
    Boolean getTransmission();
}
