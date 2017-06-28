package xmlTransformacije;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXValidator extends DefaultHandler {
	private static SAXParserFactory parserFactory;

	private static SchemaFactory schemaFactory;
	
	private static JAXBContext jaxbContext;


	static {
		parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);

		// Omogućuje validaciju u odnosu na XML šemu
		schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	}

	public boolean parse(Object object, String schemaName) {
		try {
			// Validacija u odnosu na XML šemu
			File file = new File(schemaName + ".xml");
			Schema schema = schemaFactory.newSchema(new File("src/main/resources/" + schemaName + ".xsd"));
			jaxbContext = JAXBContext.newInstance(object.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(object, file);
			parserFactory.setSchema(schema);

			SAXParser saxParser = parserFactory.newSAXParser();
			saxParser.parse(new File(schemaName + ".xml"), this);
			

		} catch (SAXParseException e) {
			System.out.println("[ERROR] Parsing error, line: " + e.getLineNumber() + ", uri: " + e.getSystemId());
			System.out.println("[ERROR] " + e.getMessage());
			System.out.print("[ERROR] Embedded exception: ");

			Exception embeddedException = e;
			if (e.getException() != null)
				embeddedException = e.getException();

			// Print stack trace...
			embeddedException.printStackTrace();
			return false;

		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
