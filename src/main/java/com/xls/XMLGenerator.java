package com.xls;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 *
 */
public class XMLGenerator {

    public static void main(String args[]) {
        XMLGenerator xmlGenerator = new XMLGenerator();
        xmlGenerator.convertFile("C:\\Users\\insys_2\\Desktop\\CSV_Sample1.csv","C:\\Users\\insys_2\\Desktop\\ajay.xml",
                "firstName,lastName,email,age",",");
    }

    // Protected Properties
    protected DocumentBuilderFactory domFactory = null;
    protected DocumentBuilder domBuilder = null;

    public XMLGenerator() {
        try {
            domFactory = DocumentBuilderFactory.newInstance();
            domBuilder = domFactory.newDocumentBuilder();
        } catch (FactoryConfigurationError exp) {
            System.err.println(exp.toString());
        } catch (ParserConfigurationException exp) {
            System.err.println(exp.toString());
        } catch (Exception exp) {
            System.err.println(exp.toString());
        }
    }

    /**
     *
     * @param csvFileName is the path of csv, which we want to change in the xml format.
     * @param xmlFileName is the path of xml file where we want to create the xml file.
     * @param headerInfo is the first line which we passing for creating the tag.
     * @param delimiter is the separation character in the csv file.
     * @return
     */
    public int convertFile(String csvFileName, String xmlFileName, String headerInfo,
                           String delimiter) {

        int rowsCount = -1;
        try {
            Document newDoc = domBuilder.newDocument();
            // Root element
            Element rootElement = newDoc.createElement("funds");
            newDoc.appendChild(rootElement);
            // Read csv file
            BufferedReader csvReader;
            csvReader = new BufferedReader(new FileReader(csvFileName));
            int fieldCount = 0;
            String[] csvFields = null;
            StringTokenizer stringTokenizer = null;

            // The column names are used to name the elements in the XML file,
            // avoid the use of Space or other characters not suitable for XML element
            // naming

            String curLine = headerInfo;
            if (curLine != null) {
                // how about other form of csv files?
                stringTokenizer = new StringTokenizer(curLine, delimiter);
                fieldCount = stringTokenizer.countTokens();
                if (fieldCount > 0) {
                    csvFields = new String[fieldCount];
                    int i = 0;
                    while (stringTokenizer.hasMoreElements())
                        csvFields[i++] = String.valueOf(stringTokenizer.nextElement());
                }
            }

            // At this point the coulmns are known, now read data by lines
            while ((curLine = csvReader.readLine()) != null) {
                stringTokenizer = new StringTokenizer(curLine, delimiter);
                fieldCount = stringTokenizer.countTokens();
                if (fieldCount > 0) {
                    Element rowElement = newDoc.createElement("fund");
                    int i = 0;
                    while (stringTokenizer.hasMoreElements()) {
                        try {
                            String curValue = String.valueOf(stringTokenizer.nextElement());
                            Element curElement = newDoc.createElement(csvFields[i++]);
                            curElement.appendChild(newDoc.createTextNode(curValue));
                            rowElement.appendChild(curElement);
                        } catch (Exception exp) {
                        }
                    }
                    rootElement.appendChild(rowElement);
                    rowsCount++;
                }
            }
            csvReader.close();

            // Save the document to the disk file
            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();
            aTransformer .setOutputProperty(OutputKeys.INDENT, "yes");
            aTransformer .setOutputProperty(OutputKeys.METHOD, "xml");
            aTransformer .setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            Source src = new DOMSource(newDoc);
            Result result = new StreamResult(new File(xmlFileName));
            aTransformer.transform(src, result);
            rowsCount++;

            // Output to console for testing
            // Resultt result = new StreamResult(System.out);

        } catch (IOException exp) {
            System.err.println(exp.toString());
        } catch (Exception exp) {
            System.err.println(exp.toString());
        }
        return rowsCount;
        // "XLM Document has been created" + rowsCount;
    }
}
