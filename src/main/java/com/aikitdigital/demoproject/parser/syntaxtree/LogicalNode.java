package com.aikitdigital.demoproject.parser.syntaxtree;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.unmodifiableList;

@Data
public abstract class LogicalNode extends AbstractNode implements Iterable<Node> {

    private final List<Node> children;
    private final LogicalOperator operator;

    protected LogicalNode(LogicalOperator operator, List<? extends Node> children) {
        Assert.notNull(operator, "operator must not be null");
        Assert.notNull(children,"children must not be null");
        this.operator = operator;
        this.children = List.copyOf(children);
    }

    @Override
    public Iterator<Node> iterator() {
        return children.iterator();
    }

    public List<Node> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public String toString() {
        return "(" + StringUtils.join(children, operator.toString()) + ")";
    }
}
