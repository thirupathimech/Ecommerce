package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.ThemeConfig;
import com.aadhik.ecommerce.service.CatalogService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@RequestScoped
public class ThemeViewBean extends BaseBean {

    @Inject
    private CatalogService catalogService;

    public ThemeConfig getTheme() {
        ThemeConfig theme = catalogService.getThemeConfig();
        if (theme == null) {
            theme = new ThemeConfig();
            theme.setPrimaryBackground("#FFF8E8");
            theme.setPrimaryColor("#173676");
            theme.setBuyNowBackground("#173676");
            theme.setBuyNowTextColor("#FFFFFF");
            theme.setAddCartBackground("#F4D332");
            theme.setAddCartTextColor("#173676");
        }
        return theme;
    }

    public String getCssVariables() {
        ThemeConfig theme = getTheme();
        String primaryBg = normalize(theme.getPrimaryBackground(), "#FFF8E8");
        String primaryColor = normalize(theme.getPrimaryColor(), "#173676");
        String buyNowBg = normalize(theme.getBuyNowBackground(), "#173676");
        String buyNowText = normalize(theme.getBuyNowTextColor(), "#FFFFFF");
        String addCartBg = normalize(theme.getAddCartBackground(), "#F4D332");
        String addCartText = normalize(theme.getAddCartTextColor(), "#173676");
        return new StringBuilder()
                .append("--primary-bg:").append(primaryBg).append(';')
                .append("--primary-surface:").append(mix(primaryBg, "#FFFFFF", 0.82)).append(';')
                .append("--primary-color:").append(primaryColor).append(';')
                .append("--primary-color-soft:").append(mix(primaryColor, "#FFFFFF", 0.88)).append(';')
                .append("--text-main:").append(textColor(primaryBg)).append(';')
                .append("--text-muted:").append(mix(textColor(primaryBg), "#FFFFFF", 0.28)).append(';')
                .append("--buy-now-bg:").append(buyNowBg).append(';')
                .append("--buy-now-bg-hover:").append(shade(buyNowBg, -0.18)).append(';')
                .append("--buy-now-color:").append(buyNowText).append(';')
                .append("--add-cart-bg:").append(addCartBg).append(';')
                .append("--add-cart-bg-hover:").append(shade(addCartBg, -0.12)).append(';')
                .append("--add-cart-color:").append(addCartText).append(';')
                .append("--support-surface:").append(mix(primaryColor, "#FFFFFF", 0.93)).append(';')
                .append("--support-border:").append(mix(primaryColor, "#FFFFFF", 0.82)).append(';')
                .append("--footer-bg:linear-gradient(135deg, ").append(shade(primaryColor, -0.28)).append(" 0%, ").append(primaryColor).append(" 100%);")
                .append("--footer-link:").append(addCartBg).append(';')
                .append("--footer-link-hover:").append(buyNowText).append(';')
                .append("--brand-blue:").append(primaryColor).append(';')
                .append("--surface:").append(primaryBg).append(';')
                .append("--card-border:").append(mix(primaryBg, primaryColor, 0.86)).append(';')
                .append("--hero-overlay-bg:").append(alpha(primaryColor, 0.78)).append(';')
                .append("--support-highlight:").append(mix(addCartBg, "#FFFFFF", 0.72)).append(';')
                .append("--accent-yellow:").append(addCartBg).append(';')
                .append("--accent-yellow-dark:").append(shade(addCartBg, -0.18)).append(';')
                .toString();
    }

    private String normalize(String value, String fallback) {
        if (isBlank(value)) {
            return fallback;
        }
        String resolved = value.trim();
        resolved = resolved.startsWith("#") ? resolved.substring(1) : resolved;
        if (resolved.length() == 3) {
            resolved = "" + resolved.charAt(0) + resolved.charAt(0)
                    + resolved.charAt(1) + resolved.charAt(1)
                    + resolved.charAt(2) + resolved.charAt(2);
        }
        if (!resolved.matches("[0-9a-fA-F]{6}")) {
            return fallback;
        }
        return "#" + resolved.toUpperCase();
    }

    private String textColor(String background) {
        int[] rgb = rgb(background);
        double luminance = (0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]) / 255.0;
        return luminance > 0.64 ? "#1D2B54" : "#F7F9FF";
    }

    private String shade(String hex, double percent) {
        int[] rgb = rgb(hex);
        for (int i = 0; i < 3; i++) {
            rgb[i] = clamp((int) Math.round(rgb[i] * (1.0 + percent)));
        }
        return hex(rgb[0], rgb[1], rgb[2]);
    }

    private String mix(String first, String second, double secondWeight) {
        int[] a = rgb(first);
        int[] b = rgb(second);
        double firstWeight = 1.0 - secondWeight;
        return hex(
                clamp((int) Math.round(a[0] * firstWeight + b[0] * secondWeight)),
                clamp((int) Math.round(a[1] * firstWeight + b[1] * secondWeight)),
                clamp((int) Math.round(a[2] * firstWeight + b[2] * secondWeight))
        );
    }

    private String alpha(String hex, double opacity) {
        int[] rgb = rgb(hex);
        return "rgba(" + rgb[0] + ',' + rgb[1] + ',' + rgb[2] + ',' + String.format(java.util.Locale.US, "%.2f", opacity) + ')';
    }

    private int[] rgb(String hex) {
        String normalized = normalize(hex, "#000000").substring(1);
        return new int[]{
            Integer.parseInt(normalized.substring(0, 2), 16),
            Integer.parseInt(normalized.substring(2, 4), 16),
            Integer.parseInt(normalized.substring(4, 6), 16)
        };
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private String hex(int red, int green, int blue) {
        return String.format("#%02X%02X%02X", red, green, blue);
    }
}