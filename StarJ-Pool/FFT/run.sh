#!/bin/sh
java $* -cp ${JAVA_HOME}/jre/lib/rt.jar:classes:lib/fastUtil-2.11.jar:jakarta-regexp-1.2.jar:../lib/aspectjrt.jar:FFT/classes `cat mainClass` `cat mainArgs`
