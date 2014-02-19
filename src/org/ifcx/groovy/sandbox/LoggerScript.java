package org.ifcx.groovy.sandbox;

import groovy.lang.Script;

public abstract class LoggerScript extends Script {
    public Object _log(int lineNum, Object value) {
        System.err.println("line " + lineNum + " : " + value);
        return value;
    }
}
