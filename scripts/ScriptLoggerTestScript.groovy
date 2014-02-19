import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer
import org.ifcx.groovy.sandbox.LogScriptStatementsTransform

def conf = new CompilerConfiguration()
conf.addCompilationCustomizers(new ASTTransformationCustomizer(new LogScriptStatementsTransform()))
new GroovyShell(conf).evaluate('''

def x = 1 * 2 + 3

y = 4

println "one statement"

println "another statement"

z = x * y

println z
''')
