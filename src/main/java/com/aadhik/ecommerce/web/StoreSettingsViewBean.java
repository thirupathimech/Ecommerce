package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.StoreSettings;
import com.aadhik.ecommerce.service.CatalogService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@ApplicationScoped
public class StoreSettingsViewBean implements Serializable {

    private static final String DEFAULT_STORE_NAME = "My Ecommerce Store";
    private static final String DEFAULT_LOGO = "https://images.unsplash.com/photo-1496450681664-3df85efbd29f?w=150&h=150&fit=crop";

    @Inject
    private CatalogService catalogService;

    private volatile StoreSettings storeSettings;

    public StoreSettings getStoreSettings() {
        if (storeSettings == null) {
            refresh();
        }
        return storeSettings;
    }

    public void refresh() {
        StoreSettings loaded = catalogService.getStoreSettings();
        storeSettings = loaded != null ? loaded : defaultSettings();
    }

    public String getStoreName() {
        return blankToDefault(getStoreSettings().getStoreName(), DEFAULT_STORE_NAME);
    }

    public String getStoreAddress() {
        return blankToDefault(getStoreSettings().getStoreAddress(), "");
    }

    public String getStoreLogo() {
        return normalizeAsset(getStoreSettings().getStoreLogo(), DEFAULT_LOGO);
    }

    public String getStoreFavicon() {
        return normalizeAsset(getStoreSettings().getStoreFavicon(), "");
    }

    public String getMetaTitle() {
        return blankToDefault(getStoreSettings().getMetaTitle(), getStoreName());
    }

    public String getMetaDescription() {
        return blankToDefault(getStoreSettings().getMetaDescription(), "Best offers and products from our ecommerce store.");
    }

    public String getGooglePlacesApiKey() {
        return blankToDefault(getStoreSettings().getGooglePlacesApiKey(), "");
    }

    private String normalizeAsset(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        if (value.startsWith("dbfile:")) {
            return "/resources/files/" + value.substring("dbfile:".length());
        }
        return value;
    }

    private String blankToDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private StoreSettings defaultSettings() {
        StoreSettings settings = new StoreSettings();
        settings.setStoreName(DEFAULT_STORE_NAME);
        settings.setStoreEmail("support@example.com");
        settings.setStorePhoneNumber("+91");
        settings.setMetaTitle("Buy Online | " + DEFAULT_STORE_NAME);
        settings.setMetaDescription("Best offers and products from our ecommerce store.");
        settings.setSitemapUrl("https://example.com/sitemap.xml");
        settings.setStoreLogo(DEFAULT_LOGO);
        return settings;
    }
}
