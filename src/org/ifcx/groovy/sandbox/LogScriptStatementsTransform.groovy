package org.ifcx.groovy.sandbox

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class LogScriptStatementsTransform implements ASTTransformation {

    def loggerScriptNode = new ClassNode(LoggerScript.class)

    public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        def moduleAST = sourceUnit.AST
        String mainClassName = moduleAST.mainClassName
        ClassNode mainClass = moduleAST.classes.find { it.name == mainClassName }

        if (mainClass?.isScript()) {
            mainClass.setSuperClass(loggerScriptNode)
            mainClass.methods?.each { MethodNode method ->
                if (method.isScriptBody()) {
                    BlockStatement topCode = method.code
                    List<Statement> existing = topCode.statements
                    List<Statement> transformed = existing.collectMany { statementWrapper(it, it.lineNumber) }
                    method.setCode(new BlockStatement(transformed, topCode.variableScope))
                }
            }
        }
    }

    List<Statement> statementWrapper(Statement statement, lineNum) {
        switch (statement.class) {
            case ExpressionStatement :
                Expression expression = statement.expression
                if (expression instanceof DeclarationExpression) {
                    [new ExpressionStatement(
                        new DeclarationExpression(expression.leftExpression
                            , expression.operation
                            , loggingExpression(lineNum, statement.expression.rightExpression)
                    ))]
                } else {
                    [new ExpressionStatement(loggingExpression(lineNum, statement.expression))]
                }
                break
            default :
                [statement]
        }
    }

    private MethodCallExpression loggingExpression(lineNum, Expression expression) {
        new MethodCallExpression(
                new VariableExpression("this")
                , "_log"
                , new ArgumentListExpression([new ConstantExpression(lineNum), expression])
        )
    }

}

