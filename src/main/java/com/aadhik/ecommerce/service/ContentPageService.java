package com.aadhik.ecommerce.service;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author THIRUPATHI G
 */
@ApplicationScoped
public class ContentPageService implements Serializable {

    private final Map<String, ContentPage> pages = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        pages.put("shipping", new ContentPage("shipping", "Shipping Policy",
                "<h2>Shipping Policy</h2><p>Add your shipping policy content here.</p>"));
        pages.put("refund", new ContentPage("refund", "Refund Policy",
                "<h2>Refund Policy</h2><p>Add your refund policy content here.</p>"));
        pages.put("privacy", new ContentPage("privacy", "Privacy Policy",
                "<h2>Privacy Policy</h2><p>Add your privacy policy content here.</p>"));
        pages.put("contact", new ContentPage("contact", "Contact Information",
                "<h2>Contact Information</h2><p>Add your store contact information here.</p>"));
    }

    public Map<String, ContentPage> getPages() {
        return pages;
    }

    public ContentPage getPage(String key) {
        return pages.get(key);
    }

    public void savePage(String key, String htmlContent) {
        ContentPage page = pages.get(key);
        if (page != null) {
            page.setHtmlContent(htmlContent);
        }
    }

    public static class ContentPage implements Serializable {
        private final String key;
        private final String title;
        private String htmlContent;

        public ContentPage(String key, String title, String htmlContent) {
            this.key = key;
            this.title = title;
            this.htmlContent = htmlContent;
        }

        public String getKey() {
            return key;
        }

        public String getTitle() {
            return title;
        }

        public String getHtmlContent() {
            return htmlContent;
        }

        public void setHtmlContent(String htmlContent) {
            this.htmlContent = htmlContent;
        }
    }
}