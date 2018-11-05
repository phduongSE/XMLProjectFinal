/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package object.thread;

import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import object.constant.AppConstant;
import object.crawler.GreenfurniCategoriesCrawler;
import object.crawler.GreenfurniCrawler;
import object.crawler.HongPhatCategoriesCrawler;
import object.crawler.HongPhatCrawler;

/**
 *
 * @author phduo
 */
public class HongPhatThread extends BaseThread implements Runnable{
    private ServletContext context;

    public HongPhatThread(ServletContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        while (true) {            
            try {
                HongPhatCategoriesCrawler categoriesCrawler = new HongPhatCategoriesCrawler(context);
                Map<String, String> categories = categoriesCrawler.getCategories(AppConstant.URL_HongPhat);
                for (Map.Entry<String, String> entry : categories.entrySet()) {
                    Thread crawlingThread = new Thread(new HongPhatCrawler(entry.getKey(), entry.getValue(), context));
                    crawlingThread.start();
                    
                    synchronized(BaseThread.getInstance()){
                        while (BaseThread.isSuspended()) {                            
                            BaseThread.getInstance().wait();
                        }
                    }
                }
                // 1000ms = 1s, total = 1 hour
                this.sleep(1000*60*60);
                synchronized (BaseThread.getInstance()){
                    while (BaseThread.isSuspended()) {                        
                        BaseThread.getInstance().wait();
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(HongPhatThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
}
