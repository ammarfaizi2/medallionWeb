call ..\libraries\apache-ant-1.9.1\bin\ant.bat
call ..\libraries\apache-ant-1.9.1\bin\ant.bat -buildfile ..\medallionService\build.xml jar-with-build
call ..\libraries\apache-ant-1.9.1\bin\ant.bat -buildfile ..\medallionModel\build.xml jar-with-build
call ..\libraries\apache-ant-1.9.1\bin\ant.bat -buildfile ..\medallionPluginService\build.xml jar-with-build