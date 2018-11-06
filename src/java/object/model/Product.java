/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package object.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author phduo
 */

//OFFSET :skip ROWS FETCH NEXT :take ROWS ONLY
@Entity
@Table(name = "Product", catalog = "XMLProjectDB", schema = "dbo")
@XmlRootElement(name = "product")
@XmlType(propOrder = {"productName", "productPrice", "imgSrc", "detailLink"})
@XmlAccessorType(XmlAccessType.NONE)
@NamedQueries({
    @NamedQuery(name = "Product.findAll", query = "SELECT p FROM Product p")
    , @NamedQuery(name = "Product.getPaging", query = "SELECT p FROM Product p ORDER BY p.id")
    , @NamedQuery(name = "Product.getCategoryPaging", query = "SELECT p FROM Product p WHERE p.categoryId = :categoryId ORDER BY p.id")
    , @NamedQuery(name = "Product.countAllProduct", query = "SELECT COUNT(p.id) FROM Product p")
    , @NamedQuery(name = "Product.countAllCategoryProduct", query = "SELECT COUNT(p.id) FROM Product p WHERE p.categoryId = :categoryId")
    , @NamedQuery(name = "Product.findById", query = "SELECT p FROM Product p WHERE p.id = :id")
    , @NamedQuery(name = "Product.findByProductName", query = "SELECT p FROM Product p WHERE p.productName = :productName")
    , @NamedQuery(name = "Product.findByProductPrice", query = "SELECT p FROM Product p WHERE p.productPrice = :productPrice")
    , @NamedQuery(name = "Product.findByImgSrc", query = "SELECT p FROM Product p WHERE p.imgSrc = :imgSrc")
    , @NamedQuery(name = "Product.findByDetailLink", query = "SELECT p FROM Product p WHERE p.detailLink = :detailLink")
    , @NamedQuery(name = "Product.findByDomain", query = "SELECT p FROM Product p WHERE p.domain = :domain")
    , @NamedQuery(name = "Product.findByNameAndCategoryId", query = "SELECT p FROM Product p WHERE lower(p.productName) LIKE lower(:productName) AND p.categoryId = :categoryId AND lower(p.domain) = lower(:domain)")})
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id", nullable = false)
    private Integer id;
    @Column(name = "ProductName", columnDefinition = "nvarchar(256)")
    private String productName;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "ProductPrice", precision = 53)
    private Double productPrice;
    @Column(name = "ImgSrc", columnDefinition = "nvarchar(256)")
    private String imgSrc;
    @Column(name = "DetailLink", columnDefinition = "nvarchar(256)")
    private String detailLink;
    @Column(name = "Domain", columnDefinition = "nvarchar(256)")
    private String domain;
    @Column(name = "CategoryId", columnDefinition = "nvarchar(256)")
    private Integer categoryId;
    @JoinColumn(name = "Category", referencedColumnName = "CategoryId")
    @ManyToOne
    private Category category;

    public Product() {
    }

    public Product(Integer id, String productName, Double productPrice, String imgSrc, String detailLink, String domain, Integer categoryId) {
        this.id = id;
        this.productName = productName;
        this.productPrice = productPrice;
        this.imgSrc = imgSrc;
        this.detailLink = detailLink;
        this.domain = domain;
        this.categoryId = categoryId;
    }

    public Product(Integer id) {
        this.id = id;
    }

    @XmlAttribute(name = "id", required = true)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    @XmlElement(required = true)
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getProductPrice() {
        return productPrice.longValue();
    }

    @XmlElement(required = true)
    public void setProductPrice(Long productPrice) {
        this.productPrice = productPrice.doubleValue();
    }

    public String getImgSrc() {
        return imgSrc;
    }

    @XmlElement(required = true)
    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getDetailLink() {
        return detailLink;
    }

    @XmlElement(required = true)
    public void setDetailLink(String detailLink) {
        this.detailLink = detailLink;
    }

    @XmlAttribute(name = "domain", required = true)
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @XmlAttribute(name = "categoryId", required = true)
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Product)) {
            return false;
        }
        Product other = (Product) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "object.model.Product[ id=" + id + " ]";
    }

}
