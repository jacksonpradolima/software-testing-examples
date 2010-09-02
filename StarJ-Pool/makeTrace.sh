#!/bin/sh
[ -f mainClass ] && MAINCLASS="`cat mainClass`"
[ -f mainArgs ] && MAINARGS="`cat mainArgs`"
[ -f runtimeFlavour ] && FLAVOUR="`cat runtimeFlavour`"

FFT_DIR=FFT
TRACE_FILE=$FFT_DIR/AdaptJ.dat

CLASSPATH="$FFT_DIR/classes:classes:${JAVA_HOME}/jre/lib/rt.jar:lib/fastUtil-2.11.jar:jakarta-regexp-1.2.jar:FFT/classes"

# Compile agent
cd AdaptJAgent
make install
cd ..


cd $FFT_DIR

ant compile

LD_LIBRARY_PATH=../AdaptJAgent/ldlib ${JAVA_HOME}/bin/java -cp $CLASSPATH -Xint -XX:+EnableJVMPIInstructionStartEvent -XrunAdaptJ:specFile=AdaptJ.spec,cp=$CLASSPATH `cat mainClass` `cat mainArgs`

#case "$FLAVOUR" in
#  ajc11*)
#    CLASSPATH=$CLASSPATH:../lib/aspectjrt-1.1.1.jar
#    ${JAVA_HOME}/bin/java -Xmx52m $* -cp $CLASSPATH $MAINCLASS $MAINARGS
#  ;;
#  ajc12*)
#    CLASSPATH=$CLASSPATH:../lib/aspectjrt-1.2.0.jar
#    ${JAVA_HOME}/bin/java -Xmx52m $* -cp $CLASSPATH $MAINCLASS $MAINARGS
#  ;;
#  "abc")
#    CLASSPATH=$CLASSPATH:../lib/abc-runtime.jar
#    ${JAVA_HOME}/bin/java -Xmx52m $* -cp $CLASSPATH $MAINCLASS $MAINARGS
#  ;;
#  *)
#    echo "Error: don't know which runtime to use; please set in runtimeFlavour."
#  ;;
#esac

cd ..
