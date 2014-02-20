package org.ifcx.groovy.sandbox

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class LogScriptTransform implements ASTTransformation {

    def loggerScriptNode = new ClassNode(LoggerScript.class)

    public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        def moduleAST = sourceUnit.AST
        String mainClassName = moduleAST.mainClassName
        ClassNode mainClass = moduleAST.classes.find { it.name == mainClassName }

        if (mainClass?.isScript()) {
            mainClass.setSuperClass(loggerScriptNode)
            new LoggingGroovyCodeVisitor(sourceUnit).visitClass(mainClass);
        }
    }
}

