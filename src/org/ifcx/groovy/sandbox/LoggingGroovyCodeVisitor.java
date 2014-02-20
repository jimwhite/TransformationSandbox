package org.ifcx.groovy.sandbox;

import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.control.SourceUnit;

import java.util.HashSet;
import java.util.Set;

public class LoggingGroovyCodeVisitor extends ClassCodeExpressionTransformer {

    final SourceUnit sourceUnit;

    // Keep track of which lines already have a logging call generated for them.
    Set<Integer> loggedLines = new HashSet<Integer>();

    public LoggingGroovyCodeVisitor(SourceUnit sourceUnit) {
        this.sourceUnit = sourceUnit;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return sourceUnit;
    }

    @Override
    public void visitMethod(MethodNode node) {
        if (node.isScriptBody()) super.visitMethod(node);
    }

    @Override
    public void visitAnnotations(AnnotatedNode node) {

    }

    @Override
    public void visitDeclarationExpression(DeclarationExpression expression) {
        expression.getRightExpression().visit(this);
    }

    @Override
    public Expression transform(Expression exp) {
        if (exp instanceof DeclarationExpression) {
            DeclarationExpression declarationExpression = (DeclarationExpression) exp;
            return new DeclarationExpression(declarationExpression.getLeftExpression()
                    , declarationExpression.getOperation()
                    , transform(declarationExpression.getRightExpression()));
        }

        Integer lineNumber = exp.getLineNumber();

        // Only wrap an expression with logging if we're the first
        // (highest level) expression to try and log on this line.
        return loggedLines.add(lineNumber) ? loggingExpression(lineNumber, exp): super.transform(exp);
    }

    private Expression loggingExpression(Integer lineNumber, Expression expression) {
        if (expression instanceof BooleanExpression) {
            BooleanExpression booleanExpression = (BooleanExpression) expression;
            return new BooleanExpression(new MethodCallExpression(
                    new VariableExpression("this")
                    , "_log"
                    , new ArgumentListExpression(new ConstantExpression(lineNumber), booleanExpression.getExpression())));
        } else {
            return new MethodCallExpression(
                    new VariableExpression("this")
                    , "_log"
                    , new ArgumentListExpression(new ConstantExpression(lineNumber), expression));
        }
    }

}
