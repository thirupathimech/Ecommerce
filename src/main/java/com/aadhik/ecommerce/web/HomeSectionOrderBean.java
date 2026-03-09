package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.HomeSectionOrderItem;
import com.aadhik.ecommerce.model.HomeSectionType;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.DualListModel;

/**
 * @author THIRUPATHI G
 */
@Named
@ViewScoped
public class HomeSectionOrderBean extends AdminBean {

    @Override
    public void resetForm() {
    }

    @Override
    public void saveForm() {
        if (homeSectionOrderPickList == null) {
            loadHomeSectionOrderPickList();
            return;
        }

        List<HomeSectionOrderItem> items = new ArrayList<>();
        int sortOrder = 1;
        for (String key : homeSectionOrderPickList.getTarget()) {
            String[] values = key.split(":");
            if (values.length != 2) {
                continue;
            }
            try {
                HomeSectionType type = HomeSectionType.valueOf(values[0]);
                Long recordId = Long.valueOf(values[1]);
                HomeSectionOrderItem item = new HomeSectionOrderItem();
                item.setSectionType(type);
                item.setRecordId(recordId);
                item.setSortOrder(sortOrder++);
                items.add(item);
            } catch (IllegalArgumentException ignored) {
            }
        }

        catalogService.saveHomeSectionOrderItems(items);
        addInfo("Home section order saved successfully");
    }

    @Override
    public boolean validateForm() {
        return false;
    }

    @Override
    public void editForm(Object form) {
    }

    @Override
    public boolean deleteForm(Object form) {
        return false;
    }


    public String resolveHomeSectionOrderLabel(String key) {
        if (homeSectionOrderOptionLabelMap == null || homeSectionOrderOptionLabelMap.isEmpty()) {
            loadHomeSectionOrderPickList();
        }
        return homeSectionOrderOptionLabelMap.getOrDefault(key, key);
    }

    public DualListModel<String> getHomeSectionOrderPickList() {
        if (homeSectionOrderPickList == null) {
            loadHomeSectionOrderPickList();
        }
        return homeSectionOrderPickList;
    }

    public void setHomeSectionOrderPickList(DualListModel<String> homeSectionOrderPickList) {
        this.homeSectionOrderPickList = homeSectionOrderPickList;
    }

}
