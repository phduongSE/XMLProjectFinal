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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class KhanghomeCrawler extends BaseCrawler implements Runnable {

    private String url;
    private String categoryName;
    private Category category;

    public KhanghomeCrawler(String url, String categoryName, ServletContext context) {
        super(context);
        this.url = url;
        this.categoryName = categoryName;
    }

    @Override
    public void run() {
        System.out.println(categoryName);
        category = createCategory(categoryName);
        if (category == null) {
            Logger.getLogger(KhanghomeCrawler.class.getName()).log(Level.SEVERE, null, new Exception("Error: Category null !!!"));
        }
        BufferedReader reader = null;
        try {
            reader = getBufferedReaderForURL(url);
            String document = "<document>";
            String line = "";
            boolean isFound = false;
            boolean isStart = false;
            while ((line = reader.readLine()) != null) {
                if (isFound && !isStart && line.isEmpty()) {
                    break;
                }
                if (line.contains("class=\"products-grid")) {
                    isFound = true;
                }
                if (isFound) {
                    if (line.contains("<img")) {
                        line += "</img>";
                    }
                    if (line.contains("<a ")) {
                        line = line + reader.readLine() + reader.readLine();
                    }
                    document += line.trim();
                }
                if (line.contains("class=\"item col-lg-3 col-md-4")) {
                    isStart = true;
                }
                if (isStart && line.contains("\t\t")) {
                    isStart = false;
                }
            }

            document += "</div></document>";

            document = document.replace("&", "&amp;");
            try {
                synchronized (BaseThread.getInstance()) {
                    while (BaseThread.isSuspended()) {
                        BaseThread.getInstance().wait();
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(KhanghomeCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }
            // Parser
            stAXParserForEachPage(document);
        } catch (IOException ex) {
            Logger.getLogger(KhanghomeCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(KhanghomeCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchElementException ex) {
            Logger.getLogger(KhanghomeCrawler.class.getName()).log(Level.SEVERE, null, ex);
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
                if ("img".equals(tagName)) {
                    isStart = true;
                    // Img Src 
                    Attribute attrSrc = startElement.getAttributeByName(new QName("src"));
                    imgLink = attrSrc == null ? "" : attrSrc.getValue();
                    imgLink = AppConstant.URL_KHANGHOME + imgLink;

                    while (isStart && eventReader.hasNext()) {
                        event = (XMLEvent) eventReader.next();
                        if (event.isStartElement()) {
                            startElement = event.asStartElement();
                            tagName = startElement.getName().getLocalPart();
                            if ("a".equals(tagName)) {
                                //Detail link
                                Attribute attrLink = startElement.getAttributeByName(new QName("href"));
                                detailLink = attrLink == null ? "" : attrLink.getValue();
                                detailLink = AppConstant.URL_KHANGHOME + detailLink;

                                // Product Name
                                Attribute attrTitle = startElement.getAttributeByName(new QName("title"));
                                productName = attrTitle == null ? "" : attrTitle.getValue();
                            }
                            if ("span".equals(tagName)) {
                                Attribute attrClass = startElement.getAttributeByName(new QName("class"));
                                String classValue = attrClass.getValue();
                                if ("price1".equals(classValue)) {
                                    event = (XMLEvent) eventReader.next();
                                    price = event.asCharacters().getData();
                                    price = price.replaceAll("\\D+", "");
                                    if (!price.isEmpty()) {
                                        realPrice = Double.parseDouble(price);
                                    }
                                    isStart = false;
                                }
                            }
                        }
                    }
                    Product p = new Product(1, productName, realPrice, imgLink, detailLink, AppConstant.URL_KHANGHOME, category.getCategoryId());

                    try {
                        synchronized (BaseThread.getInstance()) {
                            while (BaseThread.isSuspended()) {
                                BaseThread.getInstance().wait();
                            }
                        }
                    } catch (InterruptedException e) {
                        Logger.getLogger(KhanghomeCrawler.class.getName()).log(Level.SEVERE, null, e);
                    }

                    ProductDAO.getInstance().saveProductWhenCrawling(p);
                }
            }
        }
    }

}
