# FileAndRabbitMQImportExport

Prerequisites:

-install RabbitMQ

-install MySQL Workbench

-run db.sql in MySql Workbench

-in src\main\resources\application.properties, please update the following properties:

*dbUserName with the username of the SQL server

*dbPassword with the password for the SQL server

*stockNewPath with the folder path you want to read the XML file from (example: D:\\processingFile)

*stockProcessedPath with the folder you want the processed XML file to be placed (example D:\\processingFile\\logs)



This project aims to:

-read every 10 seconds an XML file (stocks_new.xml) and insert Products into the database;

-after the insert, the XML file will be renamed stocks_processed.xml

-read every 10 seconds JSON messages from a RabbitMQ queue and insert Orders into the database;

-update the stocks if the order is valid

-put a JSON message to a RabbitMQ queue after an Order was received through the queue.



XML and JSON examples can be found in the root folder.
