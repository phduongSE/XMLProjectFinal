/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package object.crawler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.servlet.ServletContext;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import object.dao.CategoryDAO;
import object.model.Category;
import object.utils.RealCategory;

/**
 *
 * @author phduo
 */
public class BaseCrawler {
    private ServletContext context;
    public static final Object LOCK = new Object();

    public BaseCrawler(ServletContext context) {
        this.context = context;
    }

    public ServletContext getContext() {
        return context;
    }

    protected BufferedReader getBufferedReaderForURL(String urlString) 
            throws MalformedURLException, IOException{
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        InputStream is = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        return reader;
    }
    
    protected XMLEventReader parseStringToXMLEventReader(String xmlSection)
            throws UnsupportedEncodingException, XMLStreamException{
        byte[] byteArray = xmlSection.getBytes("UTF-8");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader reader = factory.createXMLEventReader(inputStream);
        return reader;
    }
    
    protected Category createCategory(String categoryName){
        synchronized(LOCK){
            Category category = null;
            String realCategory = RealCategory.getRealCategoryName(categoryName);
            if (realCategory != null) {
                CategoryDAO dao = CategoryDAO.getInstance();
                category = dao.getCategoryByName(categoryName);
                if (category == null) {
                    category = new Category(new Integer(1), realCategory);
                    dao.create(category);
                }
            }
            return category;
        }
    }
}
