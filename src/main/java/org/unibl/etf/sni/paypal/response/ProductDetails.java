package org.unibl.etf.sni.paypal.response;

public class ProductDetails {
    /*
    example:
    "id": "PROD-32L8127185610912R",
    "name": "Life insurance policy",
    "description": "Life insurance policy",
    "type": "SERVICE",
    "category": "INSURANCE_AUTO_AND_HOME",
    "image_url": "https://example.com/streaming.jpg",
    "home_url": "https://example.com/home",
    "create_time": "2025-01-15T21:09:08Z",
    "update_time": "2025-01-15T21:09:08Z",
    "links": [
        {
            "href": "https://api.sandbox.paypal.com/v1/catalogs/products/PROD-32L8127185610912R",
            "rel": "self",
            "method": "GET"
        },
        {
            "href": "https://api.sandbox.paypal.com/v1/catalogs/products/PROD-32L8127185610912R",
            "rel": "edit",
            "method": "PATCH"
        }
    ]
    */

    private String id;
    private String name;
    private String description;
    private String type;
    private String category;
    private String image_url;
    private String home_url;
    private String create_time;
    private String update_time;
    private ProductCatalogLinks[] links;


    public ProductDetails() {}

    public ProductDetails(String id, String name, String description, String type, String category, String image_url, String home_url, String create_time, String update_time, ProductCatalogLinks[] links) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.category = category;
        this.image_url = image_url;
        this.home_url = home_url;
        this.create_time = create_time;
        this.update_time = update_time;
        this.links = links;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getHome_url() {
        return home_url;
    }

    public void setHome_url(String home_url) {
        this.home_url = home_url;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public ProductCatalogLinks[] getLinks() {
        return links;
    }

    public void setLinks(ProductCatalogLinks[] links) {
        this.links = links;
    }
}
