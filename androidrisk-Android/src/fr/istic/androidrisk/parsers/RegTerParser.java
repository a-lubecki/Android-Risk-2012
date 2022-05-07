package fr.istic.androidrisk.parsers;



import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import fr.istic.androidrisk.dto.MondeDTO;
import fr.istic.androidrisk.moteur.*;


public class RegTerParser {

    public static String fichier = "frontiers/ZDJ.xml";

    /*
    * Méthode de parsage du fichier xml contenant régions et limitrophes
    * @param ct l'activité de l'application dans le but d'accéder aux assets
    * @return un tableau de HashMap contenant une Hashmap de région et une Hashmap de territoires
    */
	public static MondeDTO parse(Activity ct) {

		HashMap<String, Territory> territoires = new HashMap<String, Territory>();
		HashMap<Integer, Region> regions = new HashMap<Integer, Region>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		AssetManager am = ct.getAssets();

		try {
			InputStream fs = am.open(fichier);
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(fs);
			Element root = doc.getDocumentElement();
			NodeList reg = root.getElementsByTagName("territoire");
			for(int j = 0; j<reg.getLength();j++){
				Node n = reg.item(j);
				Territory t = new Territory();
				boolean id = false;
				for (int i = 0; i < n.getChildNodes().getLength(); i++) {
					Node temp=n.getChildNodes().item(i);
					if(temp.getNodeName().equalsIgnoreCase("name_territoire")){
						t.setNom(temp.getTextContent());
					}else if(temp.getNodeName().equalsIgnoreCase("id")){
						t.setIdentifiant(temp.getTextContent());
						id = true;
					}else if(temp.getNodeName().equalsIgnoreCase("limitrophes")){
						for (int k=0; k < temp.getChildNodes().getLength();k++){
							Node limi = temp.getChildNodes().item(k);
							if(limi.getNodeName().equalsIgnoreCase("limitrophe")){
								t.addTerritoireLimitrophe(limi.getTextContent());
							}
						}
					}
				}

				if(id!=false){
					territoires.put(t.getIdentifiant(),t);
				}
			}
			reg = root.getElementsByTagName("region");
			for(int j = 0;j<reg.getLength();j++){
				Node n = reg.item(j);
				Region r = new Region();
				for( int i =0;i<n.getChildNodes().getLength();i++){
					Node temp=n.getChildNodes().item(i);
					if(temp.getNodeName().equalsIgnoreCase("name_region")){
						r.setNom(temp.getTextContent());
					}else if(temp.getNodeName().equalsIgnoreCase("id_region")){
						r.setId(Integer.parseInt(temp.getTextContent()));
					}else if(temp.getNodeName().equalsIgnoreCase("contain")){
						for (int k=0; k < temp.getChildNodes().getLength();k++){
							Node territ = temp.getChildNodes().item(k);
							if(territ.getNodeName().equalsIgnoreCase("territoire")){
                                                            Territory territoire = territoires.get(territ.getTextContent());
								r.getTerritoires().put(territoire.getIdentifiant(), territoire);
							}
						}
					}

				}
				regions.put(r.getId(),r);
			}
			MondeDTO mondeDTO = new MondeDTO(territoires, regions);
			return mondeDTO;

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

    /*
    * Méthode d'affichage d'objets région avec leur contenu. Cette méthode a été utilisée a des fins de tests
    * @param regions une Hashmap de régions identifi�es par un Integer
    */
	public static void TestRegion(HashMap<Integer, Region> regions){
		Set<Integer>ke = regions.keySet();
		Iterator<Integer> it = ke.iterator();
		while(it.hasNext()){
			int s = it.next();
		}
	}

    /*
    * Méthode d'affichage d'objets territoire avec leur contenu. Cette méthode a été utilisée a des fins de tests
    * @param territoires une Hashmap de territoires identifiées par un String
    */
	public static void TestTerritoire(HashMap<String, Territory> territoires){
		Set<String>ke = territoires.keySet();
		Iterator<String> it = ke.iterator();
		while(it.hasNext()){
			String s = it.next();
		}
	}

}
