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
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
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
public class HongPhatEachPageCrawler extends BaseCrawler implements Runnable {

    private String url;
    private Category category;

    public HongPhatEachPageCrawler(String url, Category category, ServletContext context) {
        super(context);
        this.url = url;
        this.category = category;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        try {
            reader = getBufferedReaderForURL(url);
            String document = "<document>";
            String line = "";
            boolean isFound = false;
            boolean isStart = false;
            while ((line = reader.readLine()) != null) {
                if (isFound && line.contains("/section")) {
                    break;
                }
                if (line.contains("class=\"main_noidung_info")) {
                    document += "<div>";
                    isFound = true;
                }
                if (isFound && line.contains("class=\"sp_info col-md-3")) {
                    isStart = true;
                }
                if (isStart) {
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
                Logger.getLogger(HongPhatEachPageCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }
            // Parser
            if (isStart) {
                stAXParserForEachPage(document);
            }
        } catch (IOException ex) {
            Logger.getLogger(HongPhatEachPageCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(HongPhatEachPageCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchElementException ex) {
            Logger.getLogger(HongPhatEachPageCrawler.class.getName()).log(Level.SEVERE, null, ex);
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

        while (eventReader.hasNext()) {
            String tagName = "";
            XMLEvent event = (XMLEvent) eventReader.next();
            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                tagName = startElement.getName().getLocalPart();
                if ("a".equals(tagName)) {
                    isStart = true;
                    // Detail link 
                    Attribute attrHref = startElement.getAttributeByName(new QName("href"));
                    detailLink = attrHref == null ? "" : attrHref.getValue();
                    // stop at paging
                    if (detailLink.contains("http://dogohongphat.com")) {
                        break;
                    }
                    detailLink = AppConstant.URL_HongPhat + detailLink;

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
                                imgLink = AppConstant.URL_HongPhat + imgLink;
                            }
                            if ("p".equals(tagName)) {
                                Attribute attrClass = startElement.getAttributeByName(new QName("class"));
                                String classValue = attrClass.getValue();
                                if ("gia_sp".equals(classValue)) {
                                    event = (XMLEvent) eventReader.next();
                                    price = event.asCharacters().getData();
                                    price = price.replaceAll("\\D+", "");
                                    if (!price.isEmpty()) {
                                        realPrice = Double.parseDouble(price);
                                    }
                                    // skip add to cart button
                                    eventReader.nextTag();
                                    eventReader.nextTag();
                                    eventReader.nextTag();
                                    eventReader.nextTag();
                                    eventReader.next();
                                    eventReader.next();
                                    eventReader.nextTag();
                                    eventReader.nextTag();
                                    isStart = false;
                                }
                            }
                        }
                    }
                    Product p = new Product(1, productName, realPrice, imgLink, detailLink, AppConstant.URL_HongPhat, category.getCategoryId());

                    try {
                        synchronized (BaseThread.getInstance()) {
                            while (BaseThread.isSuspended()) {
                                BaseThread.getInstance().wait();
                            }
                        }
                    } catch (InterruptedException e) {
                        Logger.getLogger(HongPhatEachPageCrawler.class.getName()).log(Level.SEVERE, null, e);
                    }

                    ProductDAO.getInstance().saveProductWhenCrawling(p);
                }
            }
        }
    }

}
