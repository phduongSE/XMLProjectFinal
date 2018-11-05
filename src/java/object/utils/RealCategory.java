/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package object.utils;

/**
 *
 * @author phduo
 */
public class RealCategory {

    public static String getRealCategoryName(String categoryName) {
        categoryName = categoryName.toLowerCase();
        if (categoryName.contains("bàn ăn")) {
            return "Nội thất nhà bếp";
        }
        if (categoryName.contains("phòng ngủ") || categoryName.contains("tủ gỗ")) {
            return "Nội thất phòng ngủ";
        }
        if (categoryName.contains("kệ") || categoryName.contains("salon") || categoryName.contains("sofa")) {
            return "Nội thất phòng khách";
        }
        return "Đồ nội thất khác";
    }

}
