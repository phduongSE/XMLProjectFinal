/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package object.thread;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import object.constant.AppConstant;
import object.crawler.GreenfurniCategoriesCrawler;
import object.crawler.GreenfurniCrawler;

/**
 *
 * @author phduo
 */
public class GreenfurniThread extends BaseThread implements Runnable{
    private ServletContext context;

    public GreenfurniThread(ServletContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        while (true) {            
            try {
                GreenfurniCategoriesCrawler categoriesCrawler = new GreenfurniCategoriesCrawler(context);
                Map<String, String> categories = categoriesCrawler.getCategories(AppConstant.URL_GREENFURNI);
                for (Map.Entry<String, String> entry : categories.entrySet()) {
                    Thread crawlingThread = new Thread(new GreenfurniCrawler(entry.getKey(), entry.getValue(), context));
                    crawlingThread.start();
                    
                    synchronized(BaseThread.getInstance()){
                        while (BaseThread.isSuspended()) {                            
                            BaseThread.getInstance().wait();
                        }
                    }
                }
                this.sleep(60*60*60*60);
                synchronized (BaseThread.getInstance()){
                    while (BaseThread.isSuspended()) {                        
                        BaseThread.getInstance().wait();
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(GreenfurniThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
}
