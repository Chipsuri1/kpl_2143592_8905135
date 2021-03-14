# kpl_2143592_8905135
IT-Sicherheit | 4.Semester | DHBW

##How to:

####Umgebungsvar setzen:
```
JAVA_HOME auf "C:\Program Files\Java\jre1.8.0_281" oder bei dir setzen  
Path bearbeiten und "%JAVA_HOME%\bin" adden
```
####downloaden vong hsqldb:

https://sourceforge.net/projects/hsqldb/files/

####in powershell
```cd in den hsql\hsql\hsql\lib```

hier halt dann nachher die richtigen namen setzen!!  
```java -cp ../lib/hsqldb.jar org.hsqldb.server.Server --database.0 file:mydb --dbname.0 xdb```