package net.nemerosa.ontrack.model.support.tree;

@FunctionalInterface
public interface NodeTransformer<D> {

    Node<D> transform(Node<D> node);

}
