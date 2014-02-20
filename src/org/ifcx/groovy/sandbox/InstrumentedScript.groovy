package org.ifcx.groovy.sandbox

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
