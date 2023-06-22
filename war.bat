rmdir /S /Q ..\medallionWeb.war
del /F medallionWeb.war
@echo off
echo start compiling war
call E:\Source\Projects\medallion\workspace\play\play.bat war . -o ..\medallionWeb.war
echo done compiling war
echo now creating the war file from the war folder
jar cvf medallionWeb.war -C ..\medallionWeb.war .
echo done  creating the war file from the war folder
rmdir /S /Q ..\medallionWeb.war
echo done removing war folder
