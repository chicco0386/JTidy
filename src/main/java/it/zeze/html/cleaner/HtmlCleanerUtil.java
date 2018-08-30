package it.zeze.html.cleaner;

import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HtmlCleanerUtil {

	private static HtmlCleaner cleaner = new HtmlCleaner();

	@SuppressWarnings("unchecked")
	public static List<TagNode> getListOfElementsByAttributeFromFile(String filePath, String attributeName, String attributeValue) throws IOException, XPatherException {
		List<TagNode> listOfElementsToReturn = new ArrayList<TagNode>();
		TagNode node = cleaner.clean(new File(filePath));
		if (StringUtils.isBlank(attributeValue)){
			listOfElementsToReturn = node.getElementListHavingAttribute(attributeName, true);
		} else {
			listOfElementsToReturn = node.getElementListByAttValue(attributeName, attributeValue, true, true);
		}
		return listOfElementsToReturn;
	}

	@SuppressWarnings("unchecked")
	public static List<TagNode> getListOfElementsByAttributeFromElement(TagNode nodeToTraverse, String attributeName, String attributeValue) throws IOException, XPatherException {
		List<TagNode> listOfElementsToReturn = new ArrayList<TagNode>();
		listOfElementsToReturn = nodeToTraverse.getElementListByAttValue(attributeName, attributeValue, true, true);
		return listOfElementsToReturn;
	}

	public static List<TagNode> getListOfElementsByXPathFromFile(String filePath, String xPath) throws IOException, XPatherException {
		List<TagNode> listOfElementsToReturn = new ArrayList<TagNode>();
		TagNode node = cleaner.clean(new File(filePath));
		Object[] arrayObject = node.evaluateXPath(xPath);
		for (int i = 0; i < arrayObject.length; i++) {
			listOfElementsToReturn.add((TagNode) arrayObject[i]);
		}
		return listOfElementsToReturn;
	}

	public static List<TagNode> getListOfElementsByXPathFromElement(TagNode nodeToTraverse, String xPath) throws IOException, XPatherException {
		List<TagNode> listOfElementsToReturn = new ArrayList<TagNode>();
		Object[] arrayObject = nodeToTraverse.evaluateXPath(xPath);
		for (int i = 0; i < arrayObject.length; i++) {
			listOfElementsToReturn.add((TagNode) arrayObject[i]);
		}
		return listOfElementsToReturn;
	}

	/**
	 * Utilizzato per XPATH che contengono funzioni non supportate da
	 * HTMLCleaner (es. contains)
	 * 
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public static List<TagNode> getListOfElementsByXPathSpecialFromElement(TagNode nodeToTraverse, String xPathExpression) throws IOException, XPatherException, XPathExpressionException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		List<TagNode> listOfElementsToReturn = new ArrayList<TagNode>();
		Document doc = new DomSerializer(new CleanerProperties()).createDOM(nodeToTraverse);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodes = (NodeList) xpath.evaluate(xPathExpression, doc, XPathConstants.NODESET);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		Node currenteNode = null;
		TagNode currentTagNode = null;
		StringWriter writer = new StringWriter();
		try {
			for (int i = 0; i < nodes.getLength(); i++) {
				writer = new StringWriter();
				currenteNode = nodes.item(i);
				transformer.transform(new DOMSource(currenteNode), new StreamResult(writer));
				String xml = writer.toString();
				currentTagNode = new HtmlCleaner().clean(xml);
				listOfElementsToReturn.add(currentTagNode);
			}
		} finally {
			writer.close();
		}
		return listOfElementsToReturn;
	}

	public static List<TagNode> getListOfElementsByXPathSpecialFromFile(String filePath, String xPathExpression) throws IOException, XPatherException, XPathExpressionException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		List<TagNode> listOfElementsToReturn = new ArrayList<TagNode>();
		TagNode nodeToTraverse = cleaner.clean(new File(filePath));
		listOfElementsToReturn = getListOfElementsByXPathSpecialFromElement(nodeToTraverse, xPathExpression);
		return listOfElementsToReturn;
	}

	public static boolean nodeContainsAttribute(TagNode node, String attrbuteName, String attributeValue) {
		boolean contains = false;
		Map<String, String> mapAttributes = node.getAttributes();
		String value = mapAttributes.get(attrbuteName);
		if (value != null && (value.equals(attributeValue) || value.contains(attributeValue))) {
			contains = true;
		}
		return contains;
	}

	public static String getAttributeValueFromFile(String filePath, String attributeName, String attributeValue, String attributeToRead) throws IOException, XPatherException {
		String toReturn = null;
		List<TagNode> listOfElementsToReturn = getListOfElementsByAttributeFromFile(filePath, attributeName, attributeValue);
		if (listOfElementsToReturn != null && !listOfElementsToReturn.isEmpty()) {
			if (attributeToRead != null) {
				toReturn = listOfElementsToReturn.get(0).getAttributeByName(attributeToRead);
			} else {
				// Leggo il valore del tag
				toReturn = listOfElementsToReturn.get(0).getText().toString();
			}
		}
		return toReturn;
	}

	public static void printTagNode(TagNode toPrint, Writer writer) throws Exception {
		Document doc = new DomSerializer(new CleanerProperties()).createDOM(toPrint);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
	}

	/**
	 * @param args
	 * @throws TransformerException
	 * @throws TransformerFactoryConfigurationError
	 * @throws ParserConfigurationException
	 * @throws XPatherException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public static void main(String[] args) throws XPathExpressionException, IOException, XPatherException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		
	}

}
