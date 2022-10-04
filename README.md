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


XML example for: stocks_new.xml
<?xml version="1.0" encoding="UTF-8"?>
<stocks>
    <stock>
        <product_id>1</product_id>
        <quantity>2000</quantity>
    </stock>
    <stock>
        <product_id>2</product_id>
        <quantity>999</quantity>
    </stock>
    <stock>
        <product_id>4</product_id>
        <quantity>-1000</quantity>
    </stock>
</stocks>

JSON example to put on the ORDERS RabbitMQ queue:

{"client_name":"John","items":[{"product_id":1,"quantity":3},{"product_id":2,"quantity":6}]}
