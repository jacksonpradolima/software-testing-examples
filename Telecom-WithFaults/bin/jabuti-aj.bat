set TOOL=D:\users\oall\Ferramentas\JaBUTi-AJ
set JAVA_HOME=C:\j2sdk\bin\
set AJ_HOME=D:\users\oall\Ferramentas\AspectJ
set JUNIT_HOME=D:\users\oall\Ferramentas\JUnit

java -cp  "%AJ_HOME%\aspectjrt.jar;.;%TOOL%;%TOOL%\lib\bcel.jar;%JUNIT_HOME%\junit.jar;%TOOL%\lib\jviewsall.jar;%TOOL%\lib\dom.jar;%TOOL%\lib\crimson.jar;%TOOL%\lib\jaxp-api.jar;javax.jar;%TOOL%\src;%TOOL%\lib\jabuti.jar" gui.JabutiGUI
pause
