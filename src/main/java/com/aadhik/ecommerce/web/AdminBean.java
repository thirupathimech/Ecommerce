package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.HomeSlider;
import com.aadhik.ecommerce.model.HomepageSection;
import com.aadhik.ecommerce.model.ProductCollection;
import com.aadhik.ecommerce.model.SectionType;
import com.aadhik.ecommerce.service.CatalogService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Named
@ViewScoped
public class AdminBean implements Serializable {

    @Inject
    private CatalogService catalogService;

    private HomeSlider sliderForm;
    private HomepageSection sectionForm;
    private ProductCollection collectionForm;

    @PostConstruct
    public void init() {
        sliderForm = new HomeSlider();
        sectionForm = new HomepageSection();
        sectionForm.setCollection(new ProductCollection());
        collectionForm = new ProductCollection();
    }

    public void saveSlider() {
        catalogService.saveSlider(sliderForm);
        sliderForm = new HomeSlider();
        addInfo("Slider saved successfully");
    }

    public void saveSection() {
        catalogService.saveSection(sectionForm);
        sectionForm = new HomepageSection();
        sectionForm.setCollection(new ProductCollection());
        addInfo("Homepage section saved successfully");
    }

    public void saveCollection() {
        catalogService.saveCollection(collectionForm);
        collectionForm = new ProductCollection();
        addInfo("Collection saved successfully");
    }

    public List<HomeSlider> getSliders() {
        return catalogService.getHomeSliders();
    }

    public List<CatalogService.HomepageSectionView> getSectionViews() {
        return catalogService.getHomepageSectionsWithProducts();
    }

    public List<ProductCollection> getCollections() {
        return catalogService.getActiveCollections();
    }

    public List<SectionType> getSectionTypes() {
        return Arrays.asList(SectionType.values());
    }

    private void addInfo(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
    }

    public HomeSlider getSliderForm() {
        return sliderForm;
    }

    public void setSliderForm(HomeSlider sliderForm) {
        this.sliderForm = sliderForm;
    }

    public HomepageSection getSectionForm() {
        return sectionForm;
    }

    public void setSectionForm(HomepageSection sectionForm) {
        this.sectionForm = sectionForm;
    }

    public ProductCollection getCollectionForm() {
        return collectionForm;
    }

    public void setCollectionForm(ProductCollection collectionForm) {
        this.collectionForm = collectionForm;
    }
}
