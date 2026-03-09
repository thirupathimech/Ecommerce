package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.HomeSlider;
import com.aadhik.ecommerce.model.MediaFile;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * @author THIRUPATHI G
 */
@Named
@ViewScoped
public class HomeSliderBean extends AdminBean {

    private HomeSlider sliderForm;

    @Override
    public void resetForm() {
        sliderForm = new HomeSlider();
        sliderForm.setActive(true);
        sliderForm.setSortOrder(1);
    }

    @Override
    public void saveForm() {
        if (!validateForm()) {
            return;
        }
        catalogService.saveSlider(sliderForm);
        resetForm();
        addInfo("Slider saved successfully");
    }

    @Override
    public boolean validateForm() {
        if (isBlank(sliderForm.getTitle())) {
            addError("Slider title is required.");
            return false;
        }

        if (isBlank(sliderForm.getImageUrl())) {
            addError("Slider image URL is required.");
            return false;
        }

        if (sliderForm.getSortOrder() <= 0) {
            addError("Slider image selection is required.");
            return false;
        }
        return true;
    }

    @Override
    public void editForm(Object form) {
        if (form instanceof HomeSlider slider) {
            HomeSlider draft = new HomeSlider();
            draft.setId(slider.getId());
            draft.setTitle(slider.getTitle());
            draft.setSubtitle(slider.getSubtitle());
            draft.setImageUrl(slider.getImageUrl());
            draft.setSortOrder(slider.getSortOrder());
            draft.setActive(slider.isActive());
            sliderForm = draft;
        }
    }

    @Override
    public boolean deleteForm(Object form) {
        return false;
    }

    public void selectFile(MediaFile file) {
        String ref = toDbFileRef(file.getId());
        if ("slider".equals(fileSelectionTarget)) {
            sliderForm.setImageUrl(ref);
        }
        addInfo("File selected");
    }

    public HomeSlider getSliderForm() {
        return sliderForm;
    }

    public void setSliderForm(HomeSlider sliderForm) {
        this.sliderForm = sliderForm;
    }

}
