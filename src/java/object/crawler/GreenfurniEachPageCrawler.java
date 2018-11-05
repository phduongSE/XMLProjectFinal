/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package object.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
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
import object.dao.ProductDAO;
import object.model.Category;
import object.model.Product;
import object.thread.BaseThread;

/**
 *
 * @author phduo
 */
public class GreenfurniEachPageCrawler extends BaseCrawler implements Runnable {

    private String url;
    private Category category;
//    private TblCategory category;

    public GreenfurniEachPageCrawler(String url, Category category, ServletContext context) {
        super(context);
        this.url = url;
        this.category = category;
//        this.category = category;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        try {
            reader = getBufferedReaderForURL(url);
            String line = "";
            String document = "<document>";
            boolean isFound = false;
            boolean isStart = false;

            while ((line = reader.readLine()) != null) {
                if (isFound && line.contains("class=\"pagination")) {
                    break;
                }
                if (line.contains("class=\"product_item_luoi")) {
                    isFound = true;
                }
                if (isFound && line.contains("class=\"col-lg-3")) {
                    isStart = true;
                }
                if (isStart) {
                    if (line.contains("<img")) {
                        line += "</img>";
                    }
                    document += line.trim();
                }
            }

            document += "</document>";

            document = document.replace("&", "&amp;");
            try {
                synchronized (BaseThread.getInstance()) {
                    while (BaseThread.isSuspended()) {
                        BaseThread.getInstance().wait();
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(GreenfurniEachPageCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }
            // Parser
            stAXParserForEachPage(document);
        } catch (IOException ex) {
            Logger.getLogger(GreenfurniEachPageCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(GreenfurniEachPageCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void stAXParserForEachPage(String document) throws XMLStreamException, UnsupportedEncodingException {
        document = document.trim();
        XMLEventReader eventReader = parseStringToXMLEventReader(document);
        Map<String, String> categories = new HashMap<>();
        String detailLink = "";
        String imgLink = "";
        String price = "";
        double realPrice = 0;
        String productName = "";
        boolean isStart = false;
        int num = 0;
        while (eventReader.hasNext()) {
            String tagName = "";
            XMLEvent event = (XMLEvent) eventReader.next();
            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                tagName = startElement.getName().getLocalPart();
                if ("a".equals(tagName)) {
                    num++;
                    isStart = true;
//                    startElement = event.asStartElement();
                    // Detail link 
                    Attribute attrHref = startElement.getAttributeByName(new QName("href"));
                    detailLink = attrHref == null ? "" : attrHref.getValue();
                    detailLink = AppConstant.URL_GREENFURNI + detailLink;

                    Attribute attrTitle = startElement.getAttributeByName(new QName("title"));
                    productName = attrTitle == null ? "" : attrTitle.getValue();
                    while (isStart && eventReader.hasNext()) {
                        event = (XMLEvent) eventReader.next();
                        if (event.isStartElement()) {
                            startElement = event.asStartElement();
                            tagName = startElement.getName().getLocalPart();
                            if ("img".equals(tagName)) {
                                Attribute attrSrc = startElement.getAttributeByName(new QName("src"));
                                imgLink = attrSrc == null ? "" : attrSrc.getValue();
                                imgLink = AppConstant.URL_GREENFURNI + imgLink;
                            }
                            if ("div".equals(tagName)) {
                                Attribute attrClass = startElement.getAttributeByName(new QName("class"));
                                String classValue = attrClass.getValue();
                                if ("giacu".equals(classValue)) {
                                    event = (XMLEvent) eventReader.next();
                                    price = event.asCharacters().getData();
                                    price = price.replaceAll("\\D+", "");
                                    realPrice = Double.parseDouble(price);
                                    isStart = false;
                                }
                            }
                        }
                    }
                    Product p = new Product(new Integer(1), productName, realPrice, imgLink, detailLink, AppConstant.URL_GREENFURNI, category.getCategoryId());

                    try {
                        synchronized (BaseThread.getInstance()) {
                            while (BaseThread.isSuspended()) {
                                BaseThread.getInstance().wait();
                            }
                        }
                    } catch (Exception e) {
                        Logger.getLogger(GreenfurniEachPageCrawler.class.getName()).log(Level.SEVERE, null, e);
                    }

                    ProductDAO.getInstance().saveProductWhenCrawling(p);
                }
            }
        }
    }

}
