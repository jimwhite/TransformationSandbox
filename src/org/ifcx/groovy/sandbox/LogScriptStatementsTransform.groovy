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

        if (mainClass.isScript()) {
            mainClass.setSuperClass(loggerScriptNode)
            mainClass.methods?.each { MethodNode method ->
                if (method.isScriptBody()) {
                    BlockStatement topCode = method.code
                    List<Statement> existing = topCode.statements
                    List<Statement> transformed = existing.collect { statementWrapper(it, it.lineNumber) }
                    method.setCode(new BlockStatement(transformed, topCode.variableScope))
                }
            }
        }
    }

    def EXCLUDED_EXPRESSION_TYPES = [DeclarationExpression]

    Statement statementWrapper(Statement statement, lineNum) {
        switch (statement.class) {
            case ExpressionStatement :
                Expression expression = statement.expression
                if (expression.class in EXCLUDED_EXPRESSION_TYPES) {
                    statement
                } else {
                    new ExpressionStatement(
                            new MethodCallExpression(
                                    new VariableExpression("this")
                                    , "_log"
                                    , new ArgumentListExpression([new ConstantExpression(lineNum), statement.expression])
                            )
                    )
                }
                break
            default :
                statement
        }
    }

}

