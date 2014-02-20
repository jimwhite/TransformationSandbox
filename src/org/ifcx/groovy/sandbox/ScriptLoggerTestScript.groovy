package org.ifcx.groovy.sandbox

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer
import org.ifcx.groovy.sandbox.LogScriptStatementsTransform

def conf = new CompilerConfiguration()
conf.addCompilationCustomizers(new ASTTransformationCustomizer(new LogScriptTransform()))
new GroovyShell(conf).evaluate(new File('InstrumentedScript.groovy'))
