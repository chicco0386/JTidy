package it.zeze.html.cleaner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HtmlCleanerUtil {

	private static HtmlCleaner cleaner = new HtmlCleaner();

	@SuppressWarnings("unchecked")
	public static List<TagNode> getListOfElementsByAttributeFromFile(String filePath, String attributeName, String attributeValue) throws IOException, XPatherException {
		List<TagNode> listOfElementsToReturn = new ArrayList<TagNode>();
		TagNode node = cleaner.clean(new File(filePath));
		listOfElementsToReturn = node.getElementListByAttValue(attributeName, attributeValue, true, true);
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
	 */
	public static void main(String[] args) {
		// leggiNuovaGazzetta();
		unmarshallAndSaveSingleHtmlFileNEW(new File("/home/enrico/Scrivania/ZeZe/fantaFormazione/new_html/probFormazioniFG33.html"));
	}

	private static void unmarshallAndSaveSingleHtmlFileNEW(File fileToElaborate) {
		System.out.println("unmarshallAndSaveSingleHtmlFileNEW, entrato per elaborare il file [" + fileToElaborate.getAbsolutePath() + "]");
		try {
			List<TagNode> listRootTagSquadre = HtmlCleanerUtil.getListOfElementsByAttributeFromFile(fileToElaborate.getAbsolutePath(), "id", "sqtab");
			if (listRootTagSquadre != null && !listRootTagSquadre.isEmpty()) {
				TagNode rootTag = listRootTagSquadre.get(0);
				List<TagNode> listPartite = getListOfElementsByXPathSpecialFromElement(rootTag, "//div[contains(@class,'tab-pane')]");
				// TagNode currentPartita = null;
				for (int i = 0; i < listPartite.size(); i++) {
					TagNode currentPartita = null;
					currentPartita = listPartite.get(i);
					System.out.println("##################################");
					unmarshallAndSaveSingleHtmlFileNEWPartita(currentPartita);
				}
			} else {
				System.out.println("Nessun rootTag contenente le partite!");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPatherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("unmarshallAndSaveSingleHtmlFileNEW, uscito");
	}

	private static void unmarshallAndSaveSingleHtmlFileNEWPartita(TagNode nodePartita) throws TransformerFactoryConfigurationError, Exception {
		try {
			if (nodePartita != null) {
				List<TagNode> listSquadreHome = HtmlCleanerUtil.getListOfElementsByAttributeFromElement(nodePartita, "itemprop", "homeTeam");
				String nomeSquadra = listSquadreHome.get(0).getElementsByName("h3", true)[0].getText().toString();
				System.out.println("Squadra CASA [" + nomeSquadra + "]");
				// Recupero lista giocatori
				List<TagNode> listaGiocatori = getListOfElementsByXPathSpecialFromElement(nodePartita, "//div[contains(@class,'probbar')]");
				TagNode titolariCasa = listaGiocatori.get(0);
				printTagNode(titolariCasa, new FileWriter(new File("/home/enrico/Scrivania/Casa_"+nomeSquadra+".html")));
				List<TagNode> listTitolariCasa = unmarshallAndSaveGiocatoriCasaNEW(titolariCasa);
				System.out.println("Giocatori TITOLARI CASA [" + listTitolariCasa.size() + "]");
				String nome = null;
				for (TagNode current : listTitolariCasa) {
					nome = getNomeGiocatore(current);
					System.out.println(nome + " - " + getRuoloGiocatore(current));
				}
				TagNode panchinaCasa = listaGiocatori.get(2);
				List<TagNode> listPanchinaCasa = unmarshallAndSaveGiocatoriCasaNEW(panchinaCasa);
				System.out.println("Giocatori PANCHINA CASA [" + listPanchinaCasa.size() + "]");
				for (TagNode current : listPanchinaCasa) {
					nome = getNomeGiocatore(current);
					System.out.println(nome + " - " + getRuoloGiocatore(current));
				}

				List<TagNode> listSquadreFuori = HtmlCleanerUtil.getListOfElementsByAttributeFromElement(nodePartita, "itemprop", "awayTeam");
				nomeSquadra = listSquadreFuori.get(0).getElementsByName("h3", true)[0].getText().toString();
				System.out.println("Squadra FUORI [" + nomeSquadra + "]");
				TagNode titolariFuori = listaGiocatori.get(1);
				List<TagNode> listTitolariFuori = unmarshallAndSaveGiocatoriFuoriNEW(titolariFuori);
				System.out.println("Giocatori TITOLARI FUORI [" + listTitolariFuori.size() + "]");
				for (TagNode current : listTitolariFuori) {
					nome = getNomeGiocatore(current);
					System.out.println(nome + " - " + getRuoloGiocatore(current));
				}
				TagNode panchinaFuori = listaGiocatori.get(3);
				List<TagNode> listPanchinaFuori = unmarshallAndSaveGiocatoriFuoriNEW(panchinaFuori);
				System.out.println("Giocatori PANCHINA FUORI [" + listPanchinaFuori.size() + "]");
				for (TagNode current : listPanchinaFuori) {
					nome = getNomeGiocatore(current);
					System.out.println(nome + " - " + getRuoloGiocatore(current));
				}

			} else {
				System.out.println("Nessun nodePartita!");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPatherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static List<TagNode> unmarshallAndSaveGiocatoriCasaNEW(TagNode nodeGiocatoriCasa) throws IOException, XPatherException {
		List<TagNode> listGiocatoriCasa = getListOfElementsByAttributeFromElement(nodeGiocatoriCasa, "class", "pgroup lf");
		return listGiocatoriCasa;
	}

	private static List<TagNode> unmarshallAndSaveGiocatoriFuoriNEW(TagNode nodeGiocatoriCasa) throws IOException, XPatherException {
		List<TagNode> listGiocatoriFuori = getListOfElementsByAttributeFromElement(nodeGiocatoriCasa, "class", "pgroup rt");
		return listGiocatoriFuori;
	}

	private static String getRuoloGiocatore(TagNode nodeGiocatore) throws XPathExpressionException, IOException, XPatherException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		String toReturn = null;
		List<TagNode> list = getListOfElementsByXPathSpecialFromElement(nodeGiocatore, "//span[contains(@class,'role')]");
		if (list != null && !list.isEmpty()) {
			TagNode node = list.get(0);
			toReturn = node.getText().toString().trim();
		}
		return toReturn;
	}

	private static String getNomeGiocatore(TagNode nodeGiocatore) throws XPathExpressionException, IOException, XPatherException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		String toReturn = null;
		List<TagNode> list = getListOfElementsByXPathSpecialFromElement(nodeGiocatore, "//div[contains(@class,'pname2')]/a");
		if (list != null && !list.isEmpty()) {
			TagNode node = list.get(0);
			toReturn = node.getText().toString().trim();
		}
		return toReturn;
	}

	private static void leggiNuovaGazzetta() {
		try {
			String filePath = "/home/enrico/Scrivania/ZeZe/fantaFormazione/new_html/probFormazioniGS2.html";

			List<TagNode> listPlayersInNode = HtmlCleanerUtil.getListOfElementsByXPathFromFile(filePath, "//div[@class='MXXX-central-articles-main-column']");
			System.out.println(listPlayersInNode.size());

			// Partite
			List<TagNode> listPlayersNameInNodeToClean = HtmlCleanerUtil.getListOfElementsByXPathFromElement(listPlayersInNode.get(0), "//div[@class='probabiliFormazioni']/div");
			List<TagNode> listPlayersNameInNode = new ArrayList<TagNode>();
			for (TagNode currentNode : listPlayersNameInNodeToClean) {
				if (nodeContainsAttribute(currentNode, "class", "matchFieldContainer")) {
					listPlayersNameInNode.add(currentNode);
				}
			}
			System.out.println(listPlayersNameInNode.size());
			System.out.println("----------------------------------------------------");

			// Nomi squadre
			TagNode currentPartitaNode;
			List<TagNode> currentPartita;
			TagNode particaCasaNode;
			TagNode particaFuoriCasaNode;
			for (int i = 0; i < listPlayersNameInNode.size(); i++) {
				currentPartitaNode = listPlayersNameInNode.get(i);
				currentPartita = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentPartitaNode, "//span[@class='teamName']/a");
				particaCasaNode = currentPartita.get(0);
				particaFuoriCasaNode = currentPartita.get(1);
				System.out.println("> Partita [" + (i + 1) + "]");
				System.out.println("-> Casa [" + particaCasaNode.getText() + "]");
				// Giocatori Titolari
				System.out.println("--> Titolari");
				List<TagNode> listTitolari = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentPartitaNode, "//li[@class='team-players-inner']");
				List<TagNode> listPlayersCasaTit = HtmlCleanerUtil.getListOfElementsByXPathFromElement(listTitolari.get(0), "//li/span[@class='team-player']");
				for (TagNode currentTag : listPlayersCasaTit) {
					if (currentTag.getParent().getAttributeByName("class") == null) {
						System.out.println(currentTag.getText());
					}
				}
				// Giocatori Panchina
				System.out.println("--> Panchina");
				List<TagNode> listPanchina = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentPartitaNode, "//div[@class='matchDetails']/div[@class='homeDetails']/p[1]");
				String panchinaToSplit = listPanchina.get(0).getText().toString();
				List<String> listGiocatoriPanchina = getGiocatoriPanchinaNew(panchinaToSplit);
				for (String currentGiocPanchina : listGiocatoriPanchina) {
					System.out.println(currentGiocPanchina);
				}

				System.out.println("-> Fuori casa [" + particaFuoriCasaNode.getText() + "]");
				// Giocatori Titolari
				System.out.println("--> Titolari");
				listPlayersCasaTit = HtmlCleanerUtil.getListOfElementsByXPathFromElement(listTitolari.get(1), "//li/span[@class='team-player']");
				for (TagNode currentTag : listPlayersCasaTit) {
					if (currentTag.getParent().getAttributeByName("class") == null) {
						System.out.println(currentTag.getText());
					}
				}
				// Giocatori Panchina
				System.out.println("--> Panchina");
				listPanchina = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentPartitaNode, "//div[@class='matchDetails']/div[@class='awayDetails']/p[1]");
				panchinaToSplit = listPanchina.get(0).getText().toString();
				listGiocatoriPanchina = getGiocatoriPanchinaNew(panchinaToSplit);
				for (String currentGiocPanchina : listGiocatoriPanchina) {
					System.out.println(currentGiocPanchina);
				}
			}

		} catch (XPatherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static List<String> getGiocatoriPanchinaNew(String stringPanchinaHTMLToSplit) {
		List<String> toReturn = new ArrayList<String>();
		System.out.println(stringPanchinaHTMLToSplit);
		// Rimuovo Panchina:
		stringPanchinaHTMLToSplit = stringPanchinaHTMLToSplit.replace("Panchina: ", "");
		String[] arrayPanchina = stringPanchinaHTMLToSplit.split(",");
		for (String currentPanchina : arrayPanchina) {
			currentPanchina = currentPanchina.trim().replaceAll("\\A(\\d*){3}\\s*", "");
			currentPanchina = currentPanchina.replace("-", "");
			currentPanchina = currentPanchina.replace("?", "'");
			currentPanchina = currentPanchina.trim().replaceAll("\\A(.){1}", "");
			toReturn.add(currentPanchina.trim());
		}
		return toReturn;

	}

}
