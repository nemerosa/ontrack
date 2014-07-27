package net.nemerosa.ontrack.model.support.tree;

public interface NodeTransformer<D> {

    Node<D> transform(Node<D> node);

}
