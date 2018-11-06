/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package object.utils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

/**
 *
 * @author phduo
 */
public class DBUtils implements Serializable {
    
    private DBUtils() {
    }
    
    private static EntityManagerFactory emf;
    private static final Object LOCK = new Object();
    
    public static EntityManager getEntityManager(){
        synchronized(LOCK){
            if (emf == null) {
                try {
                    emf = Persistence.createEntityManagerFactory("XMLProjectFinalPU");
                } catch (Exception e) {
                    Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
        
        return emf.createEntityManager();
    }
}
