/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package object.model;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author phduo
 */
@XmlRootElement(name = "products")
//@XmlAccessorType(XmlAccessType.FIELD)
public class Products {

    private List<Product> products = null;

    public List<Product> getProducts() {
        return products;
    }

    @XmlElement(name = "product")
    public void setProducts(List<Product> products) {
        this.products = products;
    }

}
