/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package object.listener;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import object.thread.GreenfurniThread;
import object.thread.HongPhatThread;
import object.thread.KhanghomeThread;
import object.utils.Utils;
import org.w3c.dom.Document;

/**
 *
 * @author phduo
 */
public class ContextServletListener implements ServletContextListener {

    private static String realPath = "";
    private static String XSL_FILE = "WEB-INF\\product.xsl";
    private static String XSL_FILE_A = "WEB-INF\\a.xsl";
    private static GreenfurniThread greenfurniThread;
    private static HongPhatThread hongPhatThread;
    private static KhanghomeThread khanghomeThread;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Listening");
        
        final ServletContext context = sce.getServletContext();
        realPath = sce.getServletContext().getRealPath("/");

        try {
            String xslFilePath = realPath + XSL_FILE;
            Document xsl = Utils.parseFileToDom(xslFilePath);
            context.setAttribute("XSLDOC", xsl);
        } catch (Exception e) {
            Logger.getLogger(ContextServletListener.class.getName()).log(Level.SEVERE, null, e);
        }

        greenfurniThread = new GreenfurniThread(context);
        greenfurniThread.start();

        hongPhatThread = new HongPhatThread(context);
        hongPhatThread.start();
        
        khanghomeThread = new KhanghomeThread(context);
        khanghomeThread.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Destroy");
    }

}
