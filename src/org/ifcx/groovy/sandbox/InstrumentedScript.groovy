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

class Foo {
    def a = 11
    def b = 22
    def c() { a * b }
}

new Foo().a * new Foo().b
new Foo().c() / 2

z = x * y

println z
