package object.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import object.constant.AppConstant;
import object.dao.ProductDAO;
import object.model.Product;
import object.model.Products;
import org.w3c.dom.Document;

/**
 *
 * @author phduo
 */
public class ProductServlet extends HttpServlet {

    public int pIndex = 1;
    public int categoryId = 0;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String sPIndex = request.getParameter("pIndex");
            String sCategoryId = request.getParameter("categoryId");
            if (sPIndex != null) {
                pIndex = Integer.parseInt(sPIndex);
            }
            if (sCategoryId != null) {
                categoryId = Integer.parseInt(sCategoryId);
            }
        } catch (Exception e) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, e);
        }

        try {
            // Create Jaxb marshaller
            JAXBContext jContext = JAXBContext.newInstance(Products.class);
            Marshaller mashaller = jContext.createMarshaller();
            mashaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Get Product list
            ProductDAO dao = ProductDAO.getInstance();
            List<Product> listProduct = dao.getCategoryPaging(pIndex, AppConstant.PAGE_SIZE, categoryId);
//            List<Product> listProduct = dao.getAll("Product.findAll");
            Products products = new Products();
            products.setProducts(listProduct);

            // create document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // marshall to document
            mashaller.marshal(products, doc);

            // set to application scope
            long size = dao.countAllCategoryProduct(categoryId);
            int lastPageNumber = (int) Math.ceil(size / AppConstant.PAGE_SIZE.doubleValue());

            request.setAttribute("RESULT", doc);
            request.setAttribute("LASTPAGE", lastPageNumber);
            request.setAttribute("CATEGORYID", categoryId);
            request.setAttribute("PINDEX", pIndex);
        } catch (Exception e) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
            rd.forward(request, response);
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
