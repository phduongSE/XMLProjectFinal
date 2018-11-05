/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package object.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import object.thread.GreenfurniThread;
import object.thread.HongPhatThread;

/**
 *
 * @author phduo
 */
public class ContextServletListener implements ServletContextListener{

    private static String realPath = "";
    private static GreenfurniThread greenfurniThread;
    private static HongPhatThread hongPhatThread;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Listening");
        realPath = sce.getServletContext().getRealPath("/");

        final ServletContext context = sce.getServletContext();
        greenfurniThread = new GreenfurniThread(context);
        greenfurniThread.start();
        
        hongPhatThread = new HongPhatThread(context);
        hongPhatThread.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Listening");
    }

    public static String getRealPath() {
        return realPath;
    }

}
