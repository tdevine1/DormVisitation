README FILE

Residential Hall Monitoring System for Fairmont State University Housing, Version 1.0

@author Hanif Mirza and Brandon Ballard
Date: 05/12/2016

CONTENTS
I.	MINIMUM SYSTEM REQUIREMENTS
II.	SOFTWARE/PLUG-IN DOWNLOADS
III.	EXTERNAL JAR FILES USED
IV.	LOCAL DATABASE SERVER CONFIGURATION
V.	JAVA SOURCE FILES
VI.	TECHNICAL SUPPORT


I. MINIMUM SYSTEM REQUIREMENTS

Operating Systems
¥ Windows 7 or Higher, Linux or Macintosh 9.2 or higher
¥ Minimum 2 GB of RAM
¥ Screen resolution 800x600 or higher



II. SOFTWARE/PLUG-IN DOWNLOADS

Java SE SE Development Kit 7 or later (to update and compile the source codes)
http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

Java SE Runtime Environment 7 or later
http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html

MySQL Workbench Local Database Server
http://www.mysql.com/downloads/


III.	EXTERNAL JAR FILES USED

1. mysql-connector-java-5.1.36-bin.jar (to establish database server connection, JDBC connector)

2. itextpdf-5.1.0.jar (print pdf from a table)

3. jfreechart.jar (for pie chart)

4. For importing excel file the following jar files have been used:

   I) poi-3.14-20160307.jar
  II) poi-ooxml-3.14-20160307.jar
 III) poi-ooxml-schemas-3.14-20160307.jar
  IV) xmlbeans-2.6.0.jar


IV.	LOCAL DATABASE SERVER CONFIGURATION 

1. After installing local database server, the server needs to be configured 

2. Set up a new connections with the following details
	
	Hostname: 127.0.01 Port: 3306
	Username: root 
	Password: root
	
3. Create a new schema with the name falcon16_dorm

4. Then execute Dorm_Visitation.sql file to create all the database tables



V. JAVA SOURCE FILES

   There are 21 java source files are included. The LoginDialog.java has the main function, so the program starts from this class.
   2 PNG image files are also included.  


VI. TECHNICAL SUPPORT 
If you need technical assistance, you may contact us in the following ways:

Email: hanif0088@gmail.com or bcrwar1@aol.com  




Copyright © 2016 Fairmont State University Housing
