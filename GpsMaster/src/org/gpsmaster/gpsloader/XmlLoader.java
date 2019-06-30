package org.gpsmaster.gpsloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.XMLConstants;
import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.GPXExtension;
import org.gpsmaster.gpxpanel.GPXFile;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class providing generic methods for subclasses reading / writing data in XML format
 * 
 * @author rfu
 *
 */
public abstract class XmlLoader extends GpsLoader {

	int depth = 0;
	XMLStreamWriter writer = null;
	DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC();
	protected String xsdResource = "";
	protected final Locale fileLocale = new Locale("en", "US"); // locale for "." as decimal separator
	
	public abstract void open(File file) throws Exception;
	
	public abstract GPXFile load() throws Exception;

	public abstract GPXFile load(InputStream inputStream, String format) throws Exception;
	

	/**
	 * 
	 */
	public boolean canValidate() {
		return true;
	}

	/**
	 * 
	 * @param stream
	 * @throws ValidationException
	 * @throws NotBoundException
	 */
	public void validate(InputStream stream) throws ValidationException {
		// TODO debug
		if (xsdResource.isEmpty() == false ) {
	        URL schemaFile = GpsMaster.class.getResource(xsdResource);
	        Source xmlFile = new StreamSource(stream);
	        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	        Schema schema = null;
	
	        try {
	            schema = schemaFactory.newSchema(schemaFile);
	            Validator validator = schema.newValidator();
	        	validator.validate(xmlFile);
			} catch (SAXException e) {
				throw new ValidationException(e.getMessage());
			} catch (IOException e) {
				throw new ValidationException(e.getMessage());
			}
		}
	}

	// Region XML-specific helper methods (reader)

	/**
	 *  
	 * @param element
	 * @param tag
	 * @return textual content of given tag or NULL 
	 */
	protected String getSubValue(Element element, String tag) {
		String value = null;
		// TODO handle <![CDATA[... correctly.
		// (see 110910_1610_B_Tulln_Runde.gpx track description)
		try {
			NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
			Node node = (Node) nodes.item(0);
			value = node.getTextContent();
		} catch (NullPointerException e) { 	}
		
		return value;
	 }
	 
	/**
	 * 
	 * @param parent
	 * @param tag
	 * @return subElement or NULL of not found
	 */
	 protected Element getSubElement(Element parent, String tag) {
		
		NodeList nodes = parent.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				Element element = (Element) node;
				if (element.getNodeName() == tag) {
					return element;
				}
			}
		}
		return null;
	 }

	 /**
	  * return all child elements with the given tag
	  * @param parent
	  * @param tag
	  * @return
	  */
	 protected List<Element> getSubElementsByTagName(Element parent, String tag) {
		 List<Element> out = new ArrayList<Element>();
		 NodeList nodes = parent.getElementsByTagName(tag);
		 for (int i = 0; i < nodes.getLength(); i++) {
			 Node node = nodes.item(i);
			 if (node.getNodeType() == Node.ELEMENT_NODE) {
				 Element element = (Element) node;
				 if (element.getNodeName() == tag) {
					 out.add(element);
				 }
			 }
		 }
		 return out;
	 }
	 
	 /**
	  * return all child elements
	  * @param parent
	  * @return
	  */
	 protected List<Element> getSubElements(Element parent) {
		 List<Element> out = new ArrayList<Element>();
		 NodeList nodes = parent.getChildNodes();
		 for (int i = 0; i < nodes.getLength(); i++) {
			 Node node = nodes.item(i);
			 if (node.getNodeType() == Node.ELEMENT_NODE) {
				 Element element = (Element) node;
				 out.add(element);
			 }
		 }
		 return out;
	 }

	// EndRegion

	 // Region XML-specific helper methods (writer)

	 	/**
	 	 * 
	 	 * @param name
	 	 * @param value
	 	 * @throws XMLStreamException
	 	 */
		protected void writeAttribute(String name, double value) throws XMLStreamException  {
			writer.writeAttribute(name, String.format(fileLocale, "%.8f", value));
		}

		/**
		 * 
		 * @param name
		 * @param value
		 * @throws XMLStreamException
		 */
		protected void writeSimpleElement(String name, double value) throws XMLStreamException {
			writeSimpleElement(name, String.format(fileLocale, "%.8f", value));
		}

		/**
		 * 
		 * @param name
		 * @param value
		 * @throws XMLStreamException
		 */
		protected void writeSimpleElement(String name, int value) throws XMLStreamException {
			writeSimpleElement(name, Integer.toString(value));
		}

		/**
		 * 
		 * @param name
		 * @param value
		 * @throws XMLStreamException
		 */
		protected void writeSimpleElement(String name, Date value) throws XMLStreamException {
			if (value != null) {
				writeSimpleElement(name, formatter.print(new DateTime(value)));
			}
		}

	 /**
		 * writes a single element, including start & end tags, if the value is not empty.
		 * <name>value</value>
		 * @param name name of the element
		 * @param value value of the element
	 * @throws XMLStreamException 
		 */
		protected void writeSimpleElement(String name, String value) throws XMLStreamException {
			// TODO check if writing as CDATA[ is necessary
            if ((value != null) && (value.isEmpty() == false)) {
            	writer.writeCharacters("\n");
            	writer.writeStartElement(name);
            	writer.writeCharacters(value);
            	writer.writeEndElement();            	
            }
		}
		
		/**
		 * 
		 * @param element
		 * @throws XMLStreamException
		 */
		protected void writeSubtree(GPXExtension element) throws XMLStreamException {
			if (element.getExtensions().size() > 0) {
				writeStartElement(element.getKey());
				for (GPXExtension subElement : element.getExtensions()) {
					writeSubtree(subElement);
				}
				writeEndElement();
			} else {
				writeSimpleElement(element.getKey(), element.getValue());
			}			
		}

		/**
		 * writes a start element with leading indentations
		 * @param name
		 * @throws XMLStreamException 
		 */
		protected void writeStartElement(String name) throws XMLStreamException {
			// write indents
			writer.writeCharacters("\n");
			writer.writeStartElement(name);
			depth++;
		}

		/**
		 * writes end element with newline
		 * @param name
		 * @throws XMLStreamException
		 */
		protected void writeEndElement() throws XMLStreamException {
			writer.writeCharacters("\n");
			writer.writeEndElement();
			depth--;
		}
		
		// for debugging purposes
		public void printTree(GPXExtension element, int level) {
			for (int i = 0; i < level; i++) {
				System.out.print("___");
			}
			System.out.println(element.getKey() + " = " + element.getValue());
			for (GPXExtension e : element.getExtensions()) {
				printTree(e, level + 1);
			}
		}
		
	 // EndRegion
	 
	

}
