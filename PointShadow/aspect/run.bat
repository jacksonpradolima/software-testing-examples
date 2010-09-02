SET TOOL=..\myjabuti

..\jdk1.3.1_08\bin\java -cp "%TOOL%\lib\aspectjrt.jar;%TOOL%;.;%TOOL%\lib\bcel.jar;%TOOL%\lib\crimson.jar" probe.ProberLoader -P myPS.jbt PointShadow.Point
