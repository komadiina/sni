package org.unibl.etf.sni.paypal.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ProductCatalog {
    private String id;
    private String name;
    private String description;
    private LocalDateTime createTime;
    private List<ProductCatalogLinks> links;

    public ProductCatalog() {}

    public ProductCatalog(String id, String name, String description, LocalDateTime createTime, List<ProductCatalogLinks> links) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createTime = createTime;
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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public List<ProductCatalogLinks> getLinks() {
        return links;
    }

    public void setLinks(List<ProductCatalogLinks> links) {
        this.links = links;
    }
}
