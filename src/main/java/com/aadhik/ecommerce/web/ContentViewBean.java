package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.ContentPage;
import com.aadhik.ecommerce.service.ContentPageService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Named
@RequestScoped
public class ContentViewBean extends BaseBean implements Serializable {

    @Inject
    private ContentPageService contentPageService;

    private ContentPage currentPage;

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap();
        String slug = params.get("slug");
        currentPage = contentPageService.getPageBySlug(slug);
        if (currentPage == null) {
            currentPage = contentPageService.getPage("shipping");
        }
    }

    public ContentPage getCurrentPage() {
        return currentPage;
    }

    public List<ContentPage> getPages() {
        return contentPageService.getPages();
    }

    public boolean isSelected(String slug) {
        return currentPage != null && currentPage.getSlug() != null && currentPage.getSlug().equals(slug);
    }
}