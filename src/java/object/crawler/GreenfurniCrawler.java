/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package object.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import object.model.Category;
import object.thread.BaseThread;

/**
 *
 * @author phduo
 */
public class GreenfurniCrawler extends BaseCrawler implements Runnable {

    private String url;
    private String categoryName;
    private Category category;

    public GreenfurniCrawler(String url, String categoryName, ServletContext context) {
        super(context);
        this.url = url;
        this.categoryName = categoryName;
    }

    @Override
    public void run() {
        category = createCategory(categoryName);
        if (category == null) {
            Logger.getLogger(GreenfurniCrawler.class.getName()).log(Level.SEVERE, null, new Exception("Error: Category null !!!"));
        }
        BufferedReader reader = null;
        try {
            reader = getBufferedReaderForURL(url);
            int lastPage = 0;
            String line = "";

            while ((line = reader.readLine()) != null) {
                if (line.contains("class=\"pagination")) {
                    
                    int indexOfLiTag = line.indexOf("<li");
                    int indexOfLiEndTag = line.indexOf("</li>");
                    
                    if (indexOfLiTag > 0 && indexOfLiEndTag > 0) {
                        line = line.substring(indexOfLiTag, indexOfLiEndTag);
                        String lastPageNumber = line.split("of ")[1];
                        lastPage = Integer.parseInt(lastPageNumber);
                    }
                    
                    break;
                }
            }

            try {
                synchronized (BaseThread.getInstance()) {
                    while (BaseThread.isSuspended()) {
                        BaseThread.getInstance().wait();
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(GreenfurniCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }

            for (int i = 0; i < lastPage; i++) {
                String pageUrl = url + "&page=" + (i + 1);
                Thread pageCrawlingThread = new Thread(new GreenfurniEachPageCrawler(pageUrl, category, this.getContext()));
                pageCrawlingThread.start();
                try {
                    synchronized (BaseThread.getInstance()) {
                        while (BaseThread.isSuspended()) {
                            BaseThread.getInstance().wait();
                        }
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(GreenfurniCrawler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GreenfurniCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GreenfurniCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

        }
    }

}
