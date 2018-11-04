/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package object.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import object.constant.AppConstant;

/**
 *
 * @author phduo
 */
public class HongPhatCategoriesCrawler extends BaseCrawler {

    public HongPhatCategoriesCrawler(ServletContext context) {
        super(context);
    }

    public Map<String, String> getCategories(String url) {
        BufferedReader reader = null;
        try {
            reader = getBufferedReaderForURL(url);
            String line = "";
            String document = "<document>";
            boolean isMainCategory = false;
            boolean isFound = false;
            while ((line = reader.readLine()) != null) {
                if (isFound && line.isEmpty()) {
                    break;
                }
                if (isFound && line.contains("<img")) {
                    isMainCategory = true;
                }
                if (isMainCategory && line.contains("href")) {
                    line += "</li>";
                    document += line.trim();
                    isMainCategory = false;
                }
                if (line.contains("id=\"news-box")) {
                    isFound = true;
                }
            }
            
            document += "</document>";
            Map<String, String> result = staxParserForCategories(document);
            return result;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HongPhatCategoriesCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HongPhatCategoriesCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(HongPhatCategoriesCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return null;
    }

    public Map<String, String> staxParserForCategories(String document) 
            throws XMLStreamException, UnsupportedEncodingException {
        
        document = document.trim();
        XMLEventReader eventReader = parseStringToXMLEventReader(document);
        Map<String, String> categories = new HashMap<>();
        
        while (eventReader.hasNext()) {            
            XMLEvent event = (XMLEvent) eventReader.nextEvent();
            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String tagName = startElement.getName().getLocalPart();
                if ("a".equals(tagName)) {
                    Attribute href = startElement.getAttributeByName(new QName("href"));
                    String link = AppConstant.URL_HongPhat + href.getValue();
                    eventReader.next();
                    eventReader.next();
                    eventReader.next();
                    event = (XMLEvent) eventReader.next();
                    Characters characters = event.asCharacters();
                    categories.put(link, characters.getData());
                }
            }
        }
        return categories;
    }
}
