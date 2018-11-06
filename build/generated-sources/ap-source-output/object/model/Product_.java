package object.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import object.model.Category;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-11-06T16:52:50")
@StaticMetamodel(Product.class)
public class Product_ { 

    public static volatile SingularAttribute<Product, String> domain;
    public static volatile SingularAttribute<Product, Integer> id;
    public static volatile SingularAttribute<Product, String> detailLink;
    public static volatile SingularAttribute<Product, Category> category;
    public static volatile SingularAttribute<Product, String> imgSrc;
    public static volatile SingularAttribute<Product, String> productName;
    public static volatile SingularAttribute<Product, Double> productPrice;
    public static volatile SingularAttribute<Product, Integer> categoryId;

}