package org.ifcx.groovy.sandbox

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer
import org.ifcx.groovy.sandbox.LogScriptStatementsTransform

def conf = new CompilerConfiguration()
conf.addCompilationCustomizers(new ASTTransformationCustomizer(new LogScriptStatementsTransform()))
new GroovyShell(conf).evaluate('''

def x = 1 * 2 + 3

y = 4

while (y--) {
  if (y & 1) {
        y
        println "one statement"
  } else {
        y
        println "another statement"
  }
}

z = x * y

println z
''')
