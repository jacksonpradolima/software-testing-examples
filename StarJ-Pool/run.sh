#!/bin/sh
[ -f mainClass ] && MAINCLASS="`cat mainClass`"
[ -f mainArgs ] && MAINARGS="`cat mainArgs`"
[ -f runtimeFlavour ] && FLAVOUR="`cat runtimeFlavour`"
CLASSPATH="${JAVA_HOME}/jre/lib/rt.jar:classes:lib/fastUtil-2.11.jar:jakarta-regexp-1.2.jar:FFT/classes"

FFT_DIR=FFT
TRACE_FILE=$FFT_DIR/AdaptJ.dat

if [ ! -f $TRACE_FILE ]; then
    echo "Error: trace file does not exist."
    echo "Please generate using 'makeTrace.sh'"
    exit 1
fi

case "$FLAVOUR" in
  ajc11*)
    CLASSPATH=$CLASSPATH:../lib/aspectjrt-1.1.1.jar
    ${JAVA_HOME}/bin/java -Xmx52m $* -cp $CLASSPATH $MAINCLASS $MAINARGS
  ;;
  ajc12*)
    CLASSPATH=$CLASSPATH:../lib/aspectjrt-1.2.0.jar
    ${JAVA_HOME}/bin/java -Xmx52m $* -cp $CLASSPATH $MAINCLASS $MAINARGS
  ;;
  "abc")
    CLASSPATH=$CLASSPATH:../lib/abc-runtime.jar
    ${JAVA_HOME}/bin/java -Xmx52m $* -cp $CLASSPATH $MAINCLASS $MAINARGS
  ;;
  *)
    echo "Error: don't know which runtime to use; please set in runtimeFlavour."
  ;;
esac
