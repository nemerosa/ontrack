package net.nemerosa.ontrack.model.support.tree;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DefaultNode<D> implements Node<D> {

    private final NodeFactory<D> factory;
    private final D data;
    private final List<Node<D>> children;

    public DefaultNode(NodeFactory<D> factory, D data) {
        this(factory, data, Collections.<Node<D>>emptyList());
    }

    public DefaultNode(NodeFactory<D> factory, D data, Collection<Node<D>> children) {
        this.factory = factory;
        this.data = data;
        this.children = new ArrayList<>(children);
    }

    @Override
    public Iterable<Node<D>> getChildren() {
        return children;
    }

    @Override
    public Node<D> append(Node<D> child) {
        children.add(child);
        return this;
    }

    @Override
    public void visit(NodeVisitor<D> nodeVisitor) {
        nodeVisitor.start(this);
        for (Node<D> child : children) {
            child.visit(nodeVisitor);
        }
        nodeVisitor.end(this);
    }

    @Override
    public D getData() {
        return data;
    }

    @Override
    @JsonIgnore
    public NodeFactory<D> getFactory() {
        return factory;
    }

    @Override
    @JsonIgnore
    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public Node<D> transform(NodeTransformer<D> transformer) {
        return transform(transformer, factory);
    }

    @Override
    public Node<D> transform(NodeTransformer<D> transformer, NodeFactory<D> factory) {
        Node<D> t;
        if (isLeaf()) {
            t = factory.leaf(data);
        } else {
            List<Node<D>> newKids = new ArrayList<>();
            for (Node<D> child : children) {
                Node<D> newKid = child.transform(transformer, factory);
                if (newKid.getData() == null) {
                    // If the returned transformed node does not contain
                    // any data, only its oyn children are added
                    for (Node<D> grandChild : newKid.getChildren()) {
                        newKids.add(grandChild);
                    }
                } else {
                    newKids.add(newKid);
                }
            }
            t = factory.node(data, newKids);
        }
        return transformer.transform(t);
    }
}
