package it.zeze.html.cleaner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

public class HtmlCleanerUtil {

	private static HtmlCleaner cleaner = new HtmlCleaner();

	public static List<TagNode> getListOfElementsByAttributeFromFile(String filePath, String attributeName, String attributeValue) throws IOException, XPatherException {
		List<TagNode> listOfElementsToReturn = new ArrayList<TagNode>();
		TagNode node = cleaner.clean(new File(filePath));
		listOfElementsToReturn = node.getElementListByAttValue(attributeName, attributeValue, true, true);
		return listOfElementsToReturn;
	}

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

	public static boolean nodeContainsAttribute(TagNode node, String attrbuteName, String attributeValue) {
		boolean contains = false;
		Map<String, String> mapAttributes = node.getAttributes();
		String value = mapAttributes.get(attrbuteName);
		if (value != null && (value.equals(attributeValue) || value.contains(attributeValue))) {
			contains = true;
		}
		return contains;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		leggiNuovaGazzetta();
	}

	public static void leggiNuovaGazzetta() {
		try {
			String filePath = "/home/enrico/Scrivania/ZeZe/fantaFormazione/new_html/probFormazioniGS2.html";

			List<TagNode> listPlayersInNode = HtmlCleanerUtil.getListOfElementsByXPathFromFile(filePath, "//div[@class='MXXX-central-articles-main-column']");
			System.out.println(listPlayersInNode.size());

			// Partite
			List<TagNode> listPlayersNameInNodeToClean = HtmlCleanerUtil.getListOfElementsByXPathFromElement(listPlayersInNode.get(0), "//div[@class='probabiliFormazioni']/div");
			List<TagNode> listPlayersNameInNode = new ArrayList<TagNode>();
			for (TagNode currentNode : listPlayersNameInNodeToClean){
				if (nodeContainsAttribute(currentNode, "class", "matchFieldContainer")){
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
				for (String currentGiocPanchina : listGiocatoriPanchina){
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
				for (String currentGiocPanchina : listGiocatoriPanchina){
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
	
	private static List<String> getGiocatoriPanchinaNew(String stringPanchinaHTMLToSplit){
		List<String> toReturn = new ArrayList<String>();
		System.out.println(stringPanchinaHTMLToSplit);
		// Rimuovo Panchina: 
		stringPanchinaHTMLToSplit = stringPanchinaHTMLToSplit.replace("Panchina: ", "");
		String[] arrayPanchina = stringPanchinaHTMLToSplit.split(",");
		for (String currentPanchina : arrayPanchina){
			currentPanchina = currentPanchina.trim().replaceAll("\\A(\\d*){3}\\s*", "");
			currentPanchina = currentPanchina.replace("-", "");
			currentPanchina = currentPanchina.replace("?", "'");
			currentPanchina = currentPanchina.trim().replaceAll("\\A(.){1}", "");
			toReturn.add(currentPanchina.trim());
		}
		return toReturn;
		
	}

}
