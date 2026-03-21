package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.Product;
import com.aadhik.ecommerce.model.ProductCollection;
import com.aadhik.ecommerce.model.StoreMenuItem;
import com.aadhik.ecommerce.service.CatalogService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@RequestScoped
public class StorefrontBean extends BaseBean implements Serializable {

    @Inject
    private CatalogService catalogService;

    public List<StoreMenuItem> getMenuItems() {
        return catalogService.getActiveStoreMenuItems();
    }

    public String menuLink(StoreMenuItem item) {
        if (item == null || item.getTargetType() == null) {
            return "#";
        }
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        return switch (item.getTargetType()) {
            case HOME ->
                contextPath + "/index.xhtml";
            case ALL_PRODUCTS ->
                contextPath + "/products.xhtml";
            case COLLECTION ->
                contextPath + "/collection.xhtml?slug=" + safe(item.getTargetRef());
            case PRODUCT ->
                contextPath + "/product.xhtml?id=" + safe(item.getTargetRef());
            case PAGE ->
                contextPath + "/content/page.xhtml?slug=" + safe(item.getTargetRef());
        };
    }

    public List<Product> getAllActiveProducts() {
        return catalogService.getActiveProducts();
    }

    public ProductCollection getSelectedCollection() {
        String slug = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("slug");
        return isBlank(slug) ? null : catalogService.findCollectionBySlug(slug);
    }

    public List<Product> getSelectedCollectionProducts() {
        ProductCollection collection = getSelectedCollection();
        return collection == null ? List.of() : catalogService.getProductsByCollection(collection.getId());
    }

    public Product getSelectedProduct() {
        String idText = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if (isBlank(idText)) {
            return null;
        }
        try {
            return catalogService.findProductById(Long.valueOf(idText));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public String collectionDescription() {
        ProductCollection collection = getSelectedCollection();
        return collection == null || isBlank(collection.getDescription())
                ? "Browse all products in this collection."
                : collection.getDescription();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
