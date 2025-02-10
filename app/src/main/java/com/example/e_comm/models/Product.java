package com.example.e_comm.models;

import java.util.ArrayList;

public class Product {
    private String id_product;
    private String id_seller;
    private String id_cat;
    private String description;
    private String nom;
    private ArrayList img;
    private String img_product;
    private Double price;
    private String base64Image; // Added field for base64Image

    public Product() {}

    // Constructor with base64Image
    public Product(String id_product, String id_seller, String id_cat, String nom, String description, ArrayList img, Double price, String base64Image) {
        this.id_product = id_product;
        this.id_seller = id_seller;
        this.id_cat = id_cat;
        this.nom = nom;
        this.description = description;
        this.img = img;
        this.price = price;
        this.base64Image = base64Image; // Initialize base64Image field
    }

    // Constructor without base64Image for backward compatibility
    public Product(String id_product, String id_seller, String id_cat, String nom, String description, String img_product, Double price) {
        this.id_product = id_product;
        this.id_seller = id_seller;
        this.id_cat = id_cat;
        this.nom = nom;
        this.description = description;
        this.img_product = img_product;
        this.price = price;
    }

    // Constructor without base64Image for backward compatibility
    public Product(String id_product, String id_seller, String id_cat, String img_product, String nom, Double price) {
        this.id_product = id_product;
        this.id_seller = id_seller;
        this.id_cat = id_cat;
        this.img_product = img_product;
        this.nom = nom;
        this.price = price;
    }

    // Getter and setter for base64Image
    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    // Getters and setters for other fields
    public String getId_product() {
        return id_product;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList getImg() {
        return img;
    }

    public Double getPrice() {
        return price;
    }

    public String getImg_product() {
        return img_product;
    }

    public String getId_seller() {
        return id_seller;
    }

    public String getId_cat() {
        return id_cat;
    }

    public void setId_product(String id_product) {
        this.id_product = id_product;
    }

    public void setId_seller(String id_seller) {
        this.id_seller = id_seller;
    }

    public void setId_cat(String id_cat) {
        this.id_cat = id_cat;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setImg(ArrayList img) {
        this.img = img;
    }

    public void setImg_product(String img_product) {
        this.img_product = img_product;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setImgProduct(String imgProduct) {
    }
}
