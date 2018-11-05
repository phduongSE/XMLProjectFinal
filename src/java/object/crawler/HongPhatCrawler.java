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
import javax.servlet.ServletContext;
import object.model.Category;
import object.thread.BaseThread;

/**
 *
 * @author phduo
 */
public class HongPhatCrawler extends BaseCrawler implements Runnable {

    private String url;
    private String categoryName;
    private Category category;

    public HongPhatCrawler(String url, String categoryName, ServletContext context) {
        super(context);
        this.url = url;
        this.categoryName = categoryName;
    }

    @Override
    public void run() {
        category = createCategory(categoryName);
        if (category == null) {
            Logger.getLogger(HongPhatCrawler.class.getName()).log(Level.SEVERE, null, new Exception("Error: Category null !!!"));
        }
        BufferedReader reader = null;
        try {
            reader = getBufferedReaderForURL(url);
            int lastPage = 0;
            String line = "";

            while ((line = reader.readLine()) != null) {
                if (line.contains("/section")) {
                    break;
                }
                if (line.contains("class=\"paging")) {
                    if (line.contains("»")) {
                        int indexOfPageNumber = line.indexOf("/p=");
                        line = line.substring(indexOfPageNumber);
                        int indexOfChar = line.indexOf("\"");
                        line = line.replace("/p=", "");
                        String lastPageNumber = line.substring(0, indexOfChar);
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

            if (lastPage == 0) {
                lastPage = 1;
            }
            for (int i = 0; i < lastPage; i++) {
                String pageUrl = url + "/p=" + (i + 1);
                Thread pageCrawlingThread = new Thread(new HongPhatEachPageCrawler(pageUrl, category, this.getContext()));
                pageCrawlingThread.start();
                try {
                    synchronized (BaseThread.getInstance()) {
                        while (BaseThread.isSuspended()) {
                            BaseThread.getInstance().wait();
                        }
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(HongPhatCrawler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HongPhatCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HongPhatCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

        }
    }

}
