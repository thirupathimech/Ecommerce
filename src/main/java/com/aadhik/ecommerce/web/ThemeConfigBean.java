package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.ThemeConfig;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.time.LocalDateTime;

@Named
@ViewScoped
public class ThemeConfigBean extends AdminBean {

    private static final String DEFAULT_PRIMARY_BG = "#FFF8E8";
    private static final String DEFAULT_PRIMARY_COLOR = "#173676";
    private static final String DEFAULT_BUY_NOW_BG = "#173676";
    private static final String DEFAULT_BUY_NOW_TEXT = "#FFFFFF";
    private static final String DEFAULT_ADD_CART_BG = "#F4D332";
    private static final String DEFAULT_ADD_CART_TEXT = "#173676";

    private ThemeConfig themeForm;

    @Override
    public void resetForm() {
        ThemeConfig existing = catalogService.getThemeConfig();
        if (existing == null) {
            themeForm = createDefaultTheme();
            return;
        }
        themeForm = new ThemeConfig();
        themeForm.setId(existing.getId());
        themeForm.setPrimaryBackground(existing.getPrimaryBackground());
        themeForm.setPrimaryColor(existing.getPrimaryColor());
        themeForm.setBuyNowBackground(existing.getBuyNowBackground());
        themeForm.setBuyNowTextColor(existing.getBuyNowTextColor());
        themeForm.setAddCartBackground(existing.getAddCartBackground());
        themeForm.setAddCartTextColor(existing.getAddCartTextColor());
        themeForm.setUpdatedAt(existing.getUpdatedAt());
    }

    @Override
    public void saveForm() {
        if (!validateForm()) {
            return;
        }
        normalizeTheme(themeForm);
        themeForm.setUpdatedAt(LocalDateTime.now());
        themeForm = catalogService.saveThemeConfig(themeForm);
        addInfo("Theme saved successfully.");
    }

    @Override
    public boolean validateForm() {
        if (themeForm == null) {
            addError("Theme form is not available.");
            return false;
        }
        return validateColor(themeForm.getPrimaryBackground(), "Primary background")
                && validateColor(themeForm.getPrimaryColor(), "Primary color")
                && validateColor(themeForm.getBuyNowBackground(), "Buy Now background")
                && validateColor(themeForm.getBuyNowTextColor(), "Buy Now text color")
                && validateColor(themeForm.getAddCartBackground(), "Add to Cart background")
                && validateColor(themeForm.getAddCartTextColor(), "Add to Cart text color");
    }

    @Override
    public void editForm(Object form) {
        if (form instanceof ThemeConfig config) {
            themeForm = config;
            normalizeTheme(themeForm);
        }
    }

    @Override
    public boolean deleteForm(Object form) {
        return false;
    }

    public void restoreDefaults() {
        ThemeConfig defaults = createDefaultTheme();
        if (themeForm != null && themeForm.getId() != null) {
            defaults.setId(themeForm.getId());
        }
        themeForm = defaults;
        addInfo("Theme reset to default colors. Save to apply it in storefront.");
    }

    public ThemeConfig getThemeForm() {
        if (themeForm == null) {
            resetForm();
        }
        return themeForm;
    }

    public void setThemeForm(ThemeConfig themeForm) {
        this.themeForm = themeForm;
    }

    private ThemeConfig createDefaultTheme() {
        ThemeConfig theme = new ThemeConfig();
        theme.setPrimaryBackground(DEFAULT_PRIMARY_BG);
        theme.setPrimaryColor(DEFAULT_PRIMARY_COLOR);
        theme.setBuyNowBackground(DEFAULT_BUY_NOW_BG);
        theme.setBuyNowTextColor(DEFAULT_BUY_NOW_TEXT);
        theme.setAddCartBackground(DEFAULT_ADD_CART_BG);
        theme.setAddCartTextColor(DEFAULT_ADD_CART_TEXT);
        theme.setUpdatedAt(LocalDateTime.now());
        return theme;
    }

    private void normalizeTheme(ThemeConfig theme) {
        theme.setPrimaryBackground(normalizeColor(theme.getPrimaryBackground(), DEFAULT_PRIMARY_BG));
        theme.setPrimaryColor(normalizeColor(theme.getPrimaryColor(), DEFAULT_PRIMARY_COLOR));
        theme.setBuyNowBackground(normalizeColor(theme.getBuyNowBackground(), DEFAULT_BUY_NOW_BG));
        theme.setBuyNowTextColor(normalizeColor(theme.getBuyNowTextColor(), DEFAULT_BUY_NOW_TEXT));
        theme.setAddCartBackground(normalizeColor(theme.getAddCartBackground(), DEFAULT_ADD_CART_BG));
        theme.setAddCartTextColor(normalizeColor(theme.getAddCartTextColor(), DEFAULT_ADD_CART_TEXT));
    }

    private boolean validateColor(String value, String label) {
        String normalized = normalizeColor(value, null);
        if (normalized == null || !normalized.matches("#[0-9A-F]{6}")) {
            addError(label + " must be a valid hex color.");
            return false;
        }
        return true;
    }

    private String normalizeColor(String value, String fallback) {
        String resolved = isBlank(value) ? fallback : value.trim();
        if (isBlank(resolved)) {
            return fallback;
        }
        resolved = resolved.startsWith("#") ? resolved.substring(1) : resolved;
        if (resolved.length() == 3) {
            resolved = "" + resolved.charAt(0) + resolved.charAt(0)
                    + resolved.charAt(1) + resolved.charAt(1)
                    + resolved.charAt(2) + resolved.charAt(2);
        }
        return "#" + resolved.toUpperCase();
    }
}