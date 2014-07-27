package net.nemerosa.ontrack.model.support.tree;

import java.util.Collection;

public class DefaultNodeFactory<D> implements NodeFactory<D> {
    @Override
    public Node<D> leaf(D data) {
        return new DefaultNode<>(this, data);
    }

    @Override
    public Node<D> node(D data, Collection<Node<D>> children) {
        return new DefaultNode<>(this, data, children);
    }
}
