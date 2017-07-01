package net.gangelov.x.evaluator;

import net.gangelov.x.ast.*;

public class TypeDetector extends AbstractVisitor<String, Void> {
    @Override
    public String visit(NumberLiteralNode node, Void context) {
        return "Integer";
    }

    @Override
    public String visit(StringLiteralNode node, Void context) {
        return "String";
    }

    @Override
    public String visit(NameNode node, Void context) {
        return null;
    }

    @Override
    public String visit(MethodCallNode node, Void context) {
        return null;
    }

    @Override
    public String visit(BranchNode node, Void context) {
        return null;
    }

    @Override
    public String visit(BlockNode node, Void context) {
        return null;
    }

    @Override
    public String visit(WhileNode node, Void context) {
        return null;
    }
}
