package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.HomeCollectionGroup;
import com.aadhik.ecommerce.model.ProductCollection;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.primefaces.model.DualListModel;

/**
 * @author THIRUPATHI G
 */
@Named
@ViewScoped
public class HomeCollectionGroupBean extends AdminBean {

    private HomeCollectionGroup collectionGroupForm;
    private DualListModel<String> collectionGroupPickList;

    @Override
    public void resetForm() {
        collectionGroupForm = new HomeCollectionGroup();
        collectionGroupForm.setActive(true);
        collectionGroupForm.setSortOrder(1);
        collectionGroupPickList = null;
    }

    @Override
    public void saveForm() {
        if (isBlank(collectionGroupForm.getTitle())) {
            addError("Collection group title is required.");
            return;
        }
        List<String> selected = collectionGroupPickList == null ? List.of() : collectionGroupPickList.getTarget();
        if (selected.isEmpty()) {
            addError("Choose at least one collection.");
            return;
        }
        collectionGroupForm.setCollectionIds(String.join("\n", selected));
        catalogService.saveHomeCollectionGroup(collectionGroupForm);
        resetForm();
        loadHomeSectionOrderPickList();
        addInfo("Collections group saved successfully");
    }

    @Override
    public boolean validateForm() {
        return false;
    }

    @Override
    public void editForm(Object form) {
        if (form instanceof HomeCollectionGroup group) {
            HomeCollectionGroup draft = new HomeCollectionGroup();
            draft.setId(group.getId());
            draft.setTitle(group.getTitle());
            draft.setCollectionIds(group.getCollectionIds());
            draft.setSortOrder(group.getSortOrder());
            draft.setActive(group.isActive());
            collectionGroupForm = draft;
            loadCollectionGroupPickList(group.getCollectionIds());
        }
    }

    @Override
    public boolean deleteForm(Object form) {
        if (form instanceof HomeCollectionGroup group) {
            if (group == null || group.getId() == null) {
                addError("Invalid collections group.");
                return false;
            }
            catalogService.deleteHomeCollectionGroup(group.getId());
            resetForm();
            loadHomeSectionOrderPickList();
            addInfo("Collections group deleted successfully");
        }
        return false;
    }

    private void loadCollectionGroupPickList(String selectedIdsRaw) {
        List<String> source = getCollections().stream()
                .filter(ProductCollection::isActive)
                .map(collection -> String.valueOf(collection.getId()))
                .collect(Collectors.toCollection(ArrayList::new));

        List<String> target = new ArrayList<>();
        if (!isBlank(selectedIdsRaw)) {
            for (String row : selectedIdsRaw.split("\n")) {
                String id = row == null ? "" : row.trim();
                if (!id.isEmpty() && source.contains(id)) {
                    target.add(id);
                }
            }
        }
        source.removeAll(target);
        collectionGroupPickList = new DualListModel<>(source, target);
    }

    public List<String> getCollectionGroupCollectionIds(HomeCollectionGroup group) {
        if (group == null || isBlank(group.getCollectionIds())) {
            return List.of();
        }
        return group.getCollectionIds().lines()
                .map(String::trim)
                .filter(text -> !text.isBlank())
                .collect(Collectors.toList());
    }

    public DualListModel<String> getCollectionGroupPickList() {
        if (collectionGroupPickList == null) {
            loadCollectionGroupPickList(collectionGroupForm == null ? null : collectionGroupForm.getCollectionIds());
        }
        return collectionGroupPickList;
    }

    public void setCollectionGroupPickList(DualListModel<String> collectionGroupPickList) {
        this.collectionGroupPickList = collectionGroupPickList;
    }

    public HomeCollectionGroup getCollectionGroupForm() {
        return collectionGroupForm;
    }

    public void setCollectionGroupForm(HomeCollectionGroup collectionGroupForm) {
        this.collectionGroupForm = collectionGroupForm;
    }

}
