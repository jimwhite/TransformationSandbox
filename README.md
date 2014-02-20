* A start on value tracing/logging for Groovy scripts prompted by Magnus Rundberget's
call for help on how to get values reported for his Groovy Light Table plugin.

A global AST transform (LogScriptTransform.groovy) wraps the topmost expression 
on each line of a Script with a method call (LoggerScript._log).  See ScriptLoggerTestScript.groovy
on how it is run.  Note that the current code gen is in LoggingGroovyCodeVisitor and the 
transform is LogScriptTransform which uses an ExpressionTransformer (LogScriptStatementsTransform
is the first version which started walking the code itself).  

It doesn't currently do anything for classes defined in the script file because a different scheme 
for the logging method call is needed.  Right now LoggerScript is set as the base script in
LogScriptTransform to define _log, but actually all classes could have the method injected.
Perhaps better would be some static method and a ThreadLocal to hold the log.
