package com.aadhik.ecommerce.service;

import com.aadhik.ecommerce.model.ContentPage;
import com.aadhik.ecommerce.repository.ContentPageRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * @author THIRUPATHI G
 */
@ApplicationScoped
public class ContentPageService implements Serializable {

    @Inject
    private ContentPageRepository contentPageRepository;

    public List<ContentPage> getPages() {
        return contentPageRepository.findAll();
    }

    public ContentPage getPage(String key) {
        return key == null ? null : contentPageRepository.findByKey(key);
    }

    public ContentPage getPageBySlug(String slug) {
        return slug == null ? null : contentPageRepository.findBySlug(slug);
    }

    public void savePage(String key, String htmlContent) {
        ContentPage page = getPage(key);
        if (page == null) {
            return;
        }
        page.setHtmlContent(htmlContent == null ? "" : htmlContent);
        contentPageRepository.save(page);
    }
}
