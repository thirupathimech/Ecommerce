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
    private static final String DEFAULT_MENU_DRAWER_BG = "#F4D332";
    private static final String DEFAULT_HEADER_TEXT_COLOR = "#FFFFFF";

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
        themeForm.setMenuDrawerBackground(existing.getMenuDrawerBackground());
        themeForm.setHeaderTextColor(existing.getHeaderTextColor());
        themeForm.setUpdatedAt(existing.getUpdatedAt());
    }

    @Override
    public void saveForm() {
        if (!validateForm()) {
            return;
        }
        normalizeTheme(themeForm);
        applyDerivedTheme(themeForm);
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
        return validateColor(themeForm.getPrimaryColor(), "Application theme color")
                && validateColor(themeForm.getMenuDrawerBackground(), "Menu panel background color")
                && validateColor(themeForm.getHeaderTextColor(), "Header text color");
    }

    @Override
    public void editForm(Object form) {
        if (form instanceof ThemeConfig config) {
            themeForm = config;
            normalizeTheme(themeForm);
            applyDerivedTheme(themeForm);
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
        theme.setMenuDrawerBackground(DEFAULT_MENU_DRAWER_BG);
        theme.setHeaderTextColor(DEFAULT_HEADER_TEXT_COLOR);
        theme.setUpdatedAt(LocalDateTime.now());
        return theme;
    }

    private void normalizeTheme(ThemeConfig theme) {
        theme.setPrimaryColor(normalizeColor(theme.getPrimaryColor(), DEFAULT_PRIMARY_COLOR));
        theme.setMenuDrawerBackground(normalizeColor(theme.getMenuDrawerBackground(), DEFAULT_MENU_DRAWER_BG));
        theme.setHeaderTextColor(normalizeColor(theme.getHeaderTextColor(), DEFAULT_HEADER_TEXT_COLOR));
    }

    private void applyDerivedTheme(ThemeConfig theme) {
        String primaryColor = normalizeColor(theme.getPrimaryColor(), DEFAULT_PRIMARY_COLOR);
        String menuDrawerBackground = normalizeColor(theme.getMenuDrawerBackground(), DEFAULT_MENU_DRAWER_BG);
        String headerTextColor = normalizeColor(theme.getHeaderTextColor(), DEFAULT_HEADER_TEXT_COLOR);
        theme.setPrimaryColor(primaryColor);
        theme.setPrimaryBackground(mix(primaryColor, "#FFFFFF", 0.92));
        theme.setBuyNowBackground(primaryColor);
        theme.setBuyNowTextColor(textColor(primaryColor));
        theme.setAddCartBackground(mix(primaryColor, "#FFFFFF", 0.18));
        theme.setAddCartTextColor(textColor(theme.getAddCartBackground()));
        theme.setMenuDrawerBackground(menuDrawerBackground);
        theme.setHeaderTextColor(headerTextColor);
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
        return resolved.matches("[0-9a-fA-F]{6}") ? "#" + resolved.toUpperCase() : fallback;
    }

    private String textColor(String background) {
        int[] rgb = rgb(background);
        double luminance = (0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]) / 255.0;
        return luminance > 0.64 ? "#1D2B54" : "#F7F9FF";
    }

    private String mix(String first, String second, double secondWeight) {
        int[] a = rgb(first);
        int[] b = rgb(second);
        double firstWeight = 1.0 - secondWeight;
        return String.format("#%02X%02X%02X",
                clamp((int) Math.round(a[0] * firstWeight + b[0] * secondWeight)),
                clamp((int) Math.round(a[1] * firstWeight + b[1] * secondWeight)),
                clamp((int) Math.round(a[2] * firstWeight + b[2] * secondWeight)));
    }

    private int[] rgb(String hex) {
        String normalized = normalizeColor(hex, "#000000").substring(1);
        return new int[]{
            Integer.parseInt(normalized.substring(0, 2), 16),
            Integer.parseInt(normalized.substring(2, 4), 16),
            Integer.parseInt(normalized.substring(4, 6), 16)
        };
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
