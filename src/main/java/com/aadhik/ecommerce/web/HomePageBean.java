package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.HomeSlider;
import com.aadhik.ecommerce.service.CatalogService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.List;

@Named
@RequestScoped
public class HomePageBean {

    @Inject
    private CatalogService catalogService;

    public List<HomeSlider> getSliders() {
        return catalogService.getHomeSliders();
    }

    public List<CatalogService.HomepageSectionView> getSectionViews() {
        return catalogService.getHomepageSectionsWithProducts();
    }
}
