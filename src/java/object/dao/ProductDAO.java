/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package object.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import object.model.Product;
import object.utils.DBUtils;

/**
 *
 * @author phduo
 */
public class ProductDAO extends BaseDAO<Product, Integer> {

    private ProductDAO() {
    }

    private static ProductDAO instance;
    private static final Object LOCK = new Object();

    public static ProductDAO getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new ProductDAO();
            }
        }

        return instance;
    }

    public List<Product> getPaging(int pageNumber, int pageSize) {
        EntityManager em = DBUtils.getEntityManager();
        try {
            List<Product> result = em.createNamedQuery("Product.getPaging", Product.class)
                    .setFirstResult((pageNumber - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
            if (result != null && !result.isEmpty()) {
                return result;
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

    public List<Product> getCategoryPaging(int pageNumber, int pageSize, int categoryId) {
        EntityManager em = DBUtils.getEntityManager();
        try {
            List<Product> result = null;
            if (categoryId == 0) {
                result = getPaging(pageNumber, pageSize);
            } else {
                result = em.createNamedQuery("Product.getCategoryPaging", Product.class)
                        .setParameter("categoryId", categoryId)
                        .setFirstResult((pageNumber - 1) * pageSize)
                        .setMaxResults(pageSize)
                        .getResultList();
            }
            if (result != null && !result.isEmpty()) {
                return result;
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

    public Long countAllProduct() {
        EntityManager em = DBUtils.getEntityManager();
        try {
            long result = (long) em.createNamedQuery("Product.countAllProduct")
                    .getSingleResult();
            return result;
        } catch (Exception e) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return null;
    }

    public Long countAllCategoryProduct(int categoryId) {
        EntityManager em = DBUtils.getEntityManager();
        try {
            long result = 0;
            if (categoryId == 0) {
                result = countAllProduct();
            } else {
                result = (long) em.createNamedQuery("Product.countAllCategoryProduct")
                        .setParameter("categoryId", categoryId)
                        .getSingleResult();
            }
            return result;
        } catch (Exception e) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return null;
    }

    public Product getProductBy(String productName, String categoryId, String domain) {
        EntityManager em = DBUtils.getEntityManager();
        try {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            List<Product> result = em.createNamedQuery("Product.findByNameAndCategoryId", Product.class)
                    .setParameter("productName", productName)
                    .setParameter("categoryId", Integer.parseInt(categoryId))
                    .setParameter("domain", domain)
                    .getResultList();
            transaction.commit();
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

    public synchronized void saveProductWhenCrawling(Product product) {
        Product existedProduct = getProductBy(product.getProductName(),
                product.getCategoryId().toString(),
                product.getDomain().toString());
        Product result;
        if (existedProduct == null) {
            result = create(product);
        } else {
            existedProduct.setProductPrice(product.getProductPrice());
            existedProduct.setImgSrc(product.getImgSrc());
            existedProduct.setDetailLink(product.getDetailLink());
            result = update(existedProduct);
        }
    }

}
