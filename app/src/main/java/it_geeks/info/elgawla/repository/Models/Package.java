package it_geeks.info.elgawla.repository.Models;

import java.io.Serializable;

public class Package implements Serializable {

    private int package_id, package_salons_no;
    private String package_name, package_category_name, package_cost, package_description, package_color, package_discount, package_discount_date_from, package_discount_date_to;
    private boolean package_is_discount;

    public Package(int package_id, String package_name, String package_category_name, String package_description, String package_color, String package_cost
            , boolean package_is_discount, int package_salons_no, String package_discount, String package_discount_date_from, String package_discount_date_to) {
        this.package_id = package_id;
        this.package_name = package_name;
        this.package_category_name = package_category_name;
        this.package_description = package_description;
        this.package_color = package_color;
        this.package_cost = package_cost;
        this.package_is_discount = package_is_discount;
        this.package_salons_no = package_salons_no;
        this.package_discount = package_discount;
        this.package_discount_date_from = package_discount_date_from;
        this.package_discount_date_to = package_discount_date_to;
    }

    public int getPackage_id() {
        return package_id;
    }

    public void setPackage_id(int package_id) {
        this.package_id = package_id;
    }

    public int getPackage_salons_no() {
        return package_salons_no;
    }

    public void setPackage_salons_no(int package_salons_no) {
        this.package_salons_no = package_salons_no;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getPackage_category_name() {
        return package_category_name;
    }

    public void setPackage_category_name(String package_category_name) {
        this.package_category_name = package_category_name;
    }

    public String getPackage_cost() {
        return package_cost;
    }

    public void setPackage_cost(String package_cost) {
        this.package_cost = package_cost;
    }

    public String getPackage_description() {
        return package_description;
    }

    public void setPackage_description(String package_description) {
        this.package_description = package_description;
    }

    public String getPackage_color() {
        return package_color;
    }

    public void setPackage_color(String package_color) {
        this.package_color = package_color;
    }

    public String getPackage_discount() {
        return package_discount;
    }

    public void setPackage_discount(String package_discount) {
        this.package_discount = package_discount;
    }

    public String getPackage_discount_date_from() {
        return package_discount_date_from;
    }

    public void setPackage_discount_date_from(String package_discount_date_from) {
        this.package_discount_date_from = package_discount_date_from;
    }

    public String getPackage_discount_date_to() {
        return package_discount_date_to;
    }

    public void setPackage_discount_date_to(String package_discount_date_to) {
        this.package_discount_date_to = package_discount_date_to;
    }

    public boolean isPackage_is_discount() {
        return package_is_discount;
    }

    public void setPackage_is_discount(boolean package_is_discount) {
        this.package_is_discount = package_is_discount;
    }
}
