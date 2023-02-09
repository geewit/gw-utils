package io.geewit.core.utils.tree;

import java.io.Serializable;

/**
 * parentNode向下childNode传递sign的处理逻辑
 * @author geewit
 */
@FunctionalInterface
public interface TransmissionChildConsumer<N extends SignedTreeNode<N, Key>, Key extends Serializable> {
    void accept(N parentNode, N childNode);
}