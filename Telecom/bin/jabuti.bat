set TOOL=D:\users\oall\Backups C\eclipse\workspace\JaBUTi-AJ
set JAVA_HOME=C:\j2sdk\bin\

java -cp  "%TOOL%\jabuti-bin.zip;%TOOL%\lib\aspectjtools.jar;%TOOL%\lib\aspectjrt.jar;.;%TOOL%;%TOOL%\lib\bcel.jar;%TOOL%\lib\junit.jar;%TOOL%\lib\jviewsall.jar;%TOOL%\lib\dom.jar;%TOOL%\lib\crimson.jar;%TOOL%\lib\jaxp-api.jar;javax.jar;%TOOL%\src;%TOOL%\lib\jabuti.jar" gui.JabutiGUI
pause
