package net.nemerosa.ontrack.model.support.tree;

public interface NodeVisitor<D> {

    void start(Node<D> node);

    void end(Node<D> node);

}
