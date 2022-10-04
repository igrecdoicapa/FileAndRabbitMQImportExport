package XMLReader;

import Entities.Product;
import MySQLConnection.SQLConnection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class ReadXML {
    private String stockNewPath;
    private String stockProcessedPath;
    static SQLConnection sqlConnection;

    static {
        try {
            sqlConnection = new SQLConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readStock() throws ParserConfigurationException, IOException, SAXException, SQLException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream("src/main/resources/application.properties");
        Properties properties = new Properties();
        properties.load(fis);
        stockNewPath = properties.getProperty("stockNewPath");
        stockProcessedPath = properties.getProperty("stockProcessedPath");
        File xmlDoc = new File(stockNewPath + "\\stocks_new.xml");
        if(xmlDoc.exists()){
            DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuild = dbFact.newDocumentBuilder();
            Document doc = dBuild.parse(xmlDoc);

            NodeList nList = doc.getElementsByTagName("stock");
            for(int i = 0; i< nList.getLength(); i++){ //parsing through each <stock> field in the XML
                Node nNode = nList.item(i);
                if(nNode.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) nNode;
                    int productFromFile = Integer.parseInt(element.getElementsByTagName("product_id").item(0).getTextContent());
                    int quantityFromFile = Integer.parseInt(element.getElementsByTagName("quantity").item(0).getTextContent());

                    //checkStock() verifies if the product already exists in the db.
                    Product jsonProduct = sqlConnection.checkStock(productFromFile, quantityFromFile);

                    if(jsonProduct.getProduct_id() == -1 & quantityFromFile >= 0){
                        //if the product doesn't exist and the quantity in the file is greater than 0, it will be inserted
                        jsonProduct.setProduct_id(productFromFile);
                        jsonProduct.setStock(quantityFromFile);
                        sqlConnection.insertStock(jsonProduct);
                    } else if (jsonProduct.getStock() < 0){
                        /*If the product exists (jsonProduct has id different from -1)
                         but the current quantity minus the quantity in the file is less than 0, skip it*/
                        System.out.println("Stock can't be negative");
                    } else {
                        //If the product exists and the new quantity is greater than 0, update the product
                        sqlConnection.updateStock(jsonProduct);
                    }
                }
            }
            //Renaming the file in order to not go in an infinite loop
            File processedFile = new File(stockProcessedPath + "\\stocks_processed.xml");
            xmlDoc.renameTo(processedFile);
        } else {
            System.out.println("File does not exist.");
        }

    }
}
