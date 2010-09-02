set TOOL=D:\users\oall\Ferramentas\JaBUTi
set JAVA_HOME=C:\j2sdk\bin\
set AJ_HOME=D:\users\oall\Ferramentas\AspectJ
set JUNIT_HOME=D:\users\oall\Ferramentas\JUnit

java -cp  "%AJ_HOME%\aspectjrt.jar;.;%TOOL%;%JUNIT_HOME%\junit.jar;%TOOL%\br\jabuti" br.jabuti.gui.JabutiGUI
pause
