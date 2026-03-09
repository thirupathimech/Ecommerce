package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.HomepageSection;
import com.aadhik.ecommerce.model.ProductCollection;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * @author THIRUPATHI G
 */
@Named
@ViewScoped
public class HomepageSectionBean extends AdminBean {

    private HomepageSection sectionForm;

    @Override
    public void resetForm() {
        sectionForm = new HomepageSection();
        sectionForm.setCollection(new ProductCollection());
        sectionForm.setActive(true);
        sectionForm.setMaxItems(4);
        sectionForm.setSortOrder(1);
    }

    @Override
    public void saveForm() {
        if (!validateForm()) {
            return;
        }
        catalogService.saveSection(sectionForm);
        resetForm();
        addInfo("Homepage section saved successfully");
    }

    @Override
    public boolean validateForm() {
        if (isBlank(sectionForm.getSectionTitle())) {
            addError("Section title is required.");
            return false;
        }
        if (sectionForm.getSectionType() == null) {
            addError("Section type is required.");
            return false;
        }
        if (sectionForm.getMaxItems() <= 0) {
            addError("Max items must be greater than 0.");
            return false;
        }
        if (sectionForm.getSortOrder() <= 0) {
            addError("Section sort order must be greater than 0.");
            return false;
        }
        return true;
    }

    @Override
    public void editForm(Object form) {
        if (form instanceof HomepageSection section) {
            HomepageSection draft = new HomepageSection();
            draft.setId(section.getId());
            draft.setSectionTitle(section.getSectionTitle());
            draft.setSectionType(section.getSectionType());
            draft.setMaxItems(section.getMaxItems());
            draft.setSortOrder(section.getSortOrder());
            draft.setActive(section.isActive());
            draft.setCollection(toCollectionReference(section.getCollection()));
            sectionForm = draft;
        }
    }

    @Override
    public boolean deleteForm(Object form) {
        return false;
    }

    public HomepageSection getSectionForm() {
        return sectionForm;
    }

    public void setSectionForm(HomepageSection sectionForm) {
        this.sectionForm = sectionForm;
    }

}
