package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.ContentPage;
import com.aadhik.ecommerce.model.MenuItemTargetType;
import com.aadhik.ecommerce.model.Product;
import com.aadhik.ecommerce.model.ProductCollection;
import com.aadhik.ecommerce.model.StoreMenuItem;
import com.aadhik.ecommerce.service.ContentPageService;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class MenuBean extends AdminBean implements Serializable {

    @Inject
    private ContentPageService contentPageService;

    private StoreMenuItem menuForm;

    @Override
    public void resetForm() {
        menuForm = new StoreMenuItem();
        menuForm.setActive(true);
        menuForm.setTargetType(MenuItemTargetType.HOME);
        menuForm.setSortOrder(nextSortOrder());
    }

    @Override
    public void saveForm() {
        if (!validateForm()) {
            return;
        }
        catalogService.saveStoreMenuItem(menuForm);
        addInfo("Menu item saved successfully.");
        resetForm();
    }

    @Override
    public boolean validateForm() {
        if (isBlank(menuForm.getLabel())) {
            addError("Menu name is required.");
            return false;
        }
        if (menuForm.getTargetType() == null) {
            addError("Select menu mapping type.");
            return false;
        }
        if (requiresReference(menuForm.getTargetType()) && isBlank(menuForm.getTargetRef())) {
            addError("Select menu mapping reference.");
            return false;
        }
        return true;
    }

    @Override
    public void editForm(Object form) {
        if (form instanceof StoreMenuItem item) {
            StoreMenuItem draft = new StoreMenuItem();
            draft.setId(item.getId());
            draft.setLabel(item.getLabel());
            draft.setTargetType(item.getTargetType());
            draft.setTargetRef(item.getTargetRef());
            draft.setSortOrder(item.getSortOrder());
            draft.setActive(item.isActive());
            menuForm = draft;
            activeMenu = "menu";
        }
    }

    @Override
    public boolean deleteForm(Object form) {
        if (!(form instanceof StoreMenuItem item) || item.getId() == null) {
            addError("Invalid menu item.");
            return false;
        }
        catalogService.deleteStoreMenuItem(item.getId());
        addInfo("Menu item deleted successfully.");
        return true;
    }

    public StoreMenuItem getMenuForm() { return menuForm; }
    public void setMenuForm(StoreMenuItem menuForm) { this.menuForm = menuForm; }
    public List<StoreMenuItem> getMenuItems() { return catalogService.getStoreMenuItems(); }
    public MenuItemTargetType[] getTargetTypes() { return MenuItemTargetType.values(); }

    public boolean requiresReference(MenuItemTargetType targetType) {
        return targetType == MenuItemTargetType.COLLECTION
                || targetType == MenuItemTargetType.PRODUCT
                || targetType == MenuItemTargetType.PAGE;
    }

    public String targetTypeLabel(MenuItemTargetType targetType) {
        if (targetType == null) return "";
        return switch (targetType) {
            case HOME -> "Home page";
            case ALL_PRODUCTS -> "All products";
            case COLLECTION -> "Collection";
            case PRODUCT -> "Product";
            case PAGE -> "Page";
        };
    }

    public List<SelectItem> getReferenceOptions() {
        List<SelectItem> options = new ArrayList<>();
        if (menuForm == null || menuForm.getTargetType() == null) {
            return options;
        }
        switch (menuForm.getTargetType()) {
            case COLLECTION -> {
                for (ProductCollection collection : getCollections()) {
                    options.add(new SelectItem(collection.getSlug(), collection.getName()));
                }
            }
            case PRODUCT -> {
                for (Product product : getProducts()) {
                    if (product.getId() != null) {
                        options.add(new SelectItem(String.valueOf(product.getId()), product.getName()));
                    }
                }
            }
            case PAGE -> {
                for (ContentPage page : contentPageService.getPages()) {
                    options.add(new SelectItem(page.getSlug(), page.getTitle()));
                }
            }
            default -> {
            }
        }
        return options;
    }

    public String describeMenuTarget(StoreMenuItem item) {
        if (item == null || item.getTargetType() == null) {
            return "";
        }
        return switch (item.getTargetType()) {
            case HOME -> "Home page";
            case ALL_PRODUCTS -> "All products page";
            case COLLECTION -> "Collection: " + resolveCollectionName(item.getTargetRef());
            case PRODUCT -> "Product: " + resolveProductName(item.getTargetRef());
            case PAGE -> "Page: " + resolvePageName(item.getTargetRef());
        };
    }

    private String resolveCollectionName(String slug) {
        ProductCollection collection = catalogService.findCollectionBySlug(slug);
        return collection == null ? slug : collection.getName();
    }

    private String resolveProductName(String value) {
        try {
            Product product = catalogService.findProductById(Long.valueOf(value));
            return product == null ? value : product.getName();
        } catch (NumberFormatException ex) {
            return value;
        }
    }

    private String resolvePageName(String slug) {
        ContentPage page = contentPageService.getPageBySlug(slug);
        return page == null ? slug : page.getTitle();
    }

    private int nextSortOrder() {
        return getMenuItems().stream()
                .map(StoreMenuItem::getSortOrder)
                .filter(value -> value != null)
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0) + 1;
    }
}