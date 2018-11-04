/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package object.dao;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import object.model.Category;
import object.utils.DBUtils;

/**
 *
 * @author phduo
 */
public class CategoryDAO extends BaseDAO<Category, Integer> {

    public CategoryDAO() {
    }

    private static CategoryDAO instance;
    private static final Object LOCK = new Object();

    public static CategoryDAO getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new CategoryDAO();
            }
        }

        return instance;
    }

    public synchronized Category getCategoryByName(String categoryName) {
        EntityManager em = DBUtils.getEntityManager();
        try {
            List<Category> result = em.createNamedQuery("Category.findByCategoryname", Category.class)
                    .setParameter("categoryName", categoryName)
                    .getResultList();

            if (result != null && !result.isEmpty()) {
                return result.get(0);
            }
        } catch (Exception e) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return null;
    }

}
