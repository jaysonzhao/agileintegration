package org.mycompany;

import org.apache.camel.Exchange;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class MyBeanProcessor {

	public MyBeanProcessor() {
		// should do nothing
	}

	public void processExchange(Exchange exchange) {
		// Get your XML from exchange (maybe, your need to convert them to DOM Document
		// before processing)
		String xmlStr = exchange.getIn().getBody(String.class);
		System.out.println(xmlStr);
		Document doc = convertStringToXMLDocument(xmlStr);
	
	}

	private static Document convertStringToXMLDocument(String xmlString) {
		// Parser that produces DOM object trees from XML content
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// API to obtain DOM Document instance
		DocumentBuilder builder = null;
		try {
			// Create DocumentBuilder with default configuration
			builder = factory.newDocumentBuilder();

			// Parse the content to Document object
			Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
			System.out.println(doc);
			return doc;
		} catch (Exception e) {
			System.out.print(e);
			e.printStackTrace();
		}
		return null;
	}

}
