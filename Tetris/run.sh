#!/bin/sh
[ -f mainClass ] && MAINCLASS="`cat mainClass`"
[ -f mainArgs ] && MAINARGS="`cat mainArgs`"
[ -f runtimeFlavour ] && FLAVOUR="`cat runtimeFlavour`"
case "$FLAVOUR" in
  ajc11*)
    ${JAVA_HOME}/bin/java $* -cp classes:../lib/aspectjrt-1.1.1.jar \
    $MAINCLASS $MAINARGS
  ;;
  ajc12*)
    ${JAVA_HOME}/bin/java $* -cp classes:../lib/aspectjrt-1.2.0.jar \
    $MAINCLASS $MAINARGS
  ;;
  "abc")
    ${JAVA_HOME}/bin/java $* -cp classes:../lib/abc-runtime.jar \
    $MAINCLASS $MAINARGS 
  ;;
  *)
    echo "Error: don't know which runtime to use; please set in runtimeFlavour."
esac
