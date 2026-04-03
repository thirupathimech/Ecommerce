package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.ShippingRate;
import com.aadhik.ecommerce.model.ShippingThresholdType;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class ShippingBean extends AdminBean implements Serializable {

    private static final Map<String, String> INDIA_STATES = new LinkedHashMap<>();

    static {
        INDIA_STATES.put("AN", "Andaman and Nicobar Islands");
        INDIA_STATES.put("AP", "Andhra Pradesh");
        INDIA_STATES.put("AR", "Arunachal Pradesh");
        INDIA_STATES.put("AS", "Assam");
        INDIA_STATES.put("BR", "Bihar");
        INDIA_STATES.put("CH", "Chandigarh");
        INDIA_STATES.put("CT", "Chhattisgarh");
        INDIA_STATES.put("DN", "Dadra and Nagar Haveli and Daman and Diu");
        INDIA_STATES.put("DL", "Delhi");
        INDIA_STATES.put("GA", "Goa");
        INDIA_STATES.put("GJ", "Gujarat");
        INDIA_STATES.put("HR", "Haryana");
        INDIA_STATES.put("HP", "Himachal Pradesh");
        INDIA_STATES.put("JK", "Jammu and Kashmir");
        INDIA_STATES.put("JH", "Jharkhand");
        INDIA_STATES.put("KA", "Karnataka");
        INDIA_STATES.put("KL", "Kerala");
        INDIA_STATES.put("LA", "Ladakh");
        INDIA_STATES.put("LD", "Lakshadweep");
        INDIA_STATES.put("MP", "Madhya Pradesh");
        INDIA_STATES.put("MH", "Maharashtra");
        INDIA_STATES.put("MN", "Manipur");
        INDIA_STATES.put("ML", "Meghalaya");
        INDIA_STATES.put("MZ", "Mizoram");
        INDIA_STATES.put("NL", "Nagaland");
        INDIA_STATES.put("OR", "Odisha");
        INDIA_STATES.put("PY", "Puducherry");
        INDIA_STATES.put("PB", "Punjab");
        INDIA_STATES.put("RJ", "Rajasthan");
        INDIA_STATES.put("SK", "Sikkim");
        INDIA_STATES.put("TN", "Tamil Nadu");
        INDIA_STATES.put("TS", "Telangana");
        INDIA_STATES.put("TR", "Tripura");
        INDIA_STATES.put("UP", "Uttar Pradesh");
        INDIA_STATES.put("UT", "Uttarakhand");
        INDIA_STATES.put("WB", "West Bengal");
    }

    private ShippingRate shippingForm;

    @Override
    public void resetForm() {
        shippingForm = new ShippingRate();
        shippingForm.setActive(true);
        shippingForm.setThresholdType(ShippingThresholdType.WEIGHT);
        shippingForm.setThresholdValue(BigDecimal.ZERO);
        shippingForm.setCharge(BigDecimal.ZERO);
    }

    @Override
    public void saveForm() {
        if (!validateForm()) {
            return;
        }

        shippingForm.setStateName(INDIA_STATES.get(shippingForm.getStateCode()));

        catalogService.saveShippingRate(shippingForm);
        addInfo("Shipping rate saved successfully.");
        resetForm();
    }

    @Override
    public boolean validateForm() {
        if (shippingForm.getThresholdType() == null) {
            addError("Choose rule type (weight or price).");
            return false;
        }
        if (shippingForm.getThresholdValue() == null || shippingForm.getThresholdValue().compareTo(BigDecimal.ZERO) < 0) {
            addError("Threshold value must be zero or higher.");
            return false;
        }
        if (shippingForm.getCharge() == null || shippingForm.getCharge().compareTo(BigDecimal.ZERO) < 0) {
            addError("Shipping charge must be zero or higher.");
            return false;
        }
        if (isBlank(shippingForm.getStateCode())) {
            addError("State is required. Choose any India state.");
            return false;
        }
        if (catalogService.existsShippingConditionRule(
                shippingForm.getStateCode(),
                shippingForm.getThresholdType(),
                shippingForm.getThresholdValue(),
                shippingForm.getId())) {
            addError("Same state + type + max value already exists.");
            return false;
        }

        return true;
    }

    @Override
    public void editForm(Object form) {
        if (!(form instanceof ShippingRate item)) {
            return;
        }
        ShippingRate draft = new ShippingRate();
        draft.setId(item.getId());
        draft.setStateCode(item.getStateCode());
        draft.setStateName(item.getStateName());
        draft.setThresholdType(item.getThresholdType());
        draft.setThresholdValue(item.getThresholdValue());
        draft.setCharge(item.getCharge());
        draft.setActive(item.isActive());
        shippingForm = draft;
        activeMenu = "shipping";
    }

    @Override
    public boolean deleteForm(Object form) {
        if (!(form instanceof ShippingRate item) || item.getId() == null) {
            addError("Invalid shipping rule.");
            return false;
        }
        catalogService.deleteShippingRate(item.getId());
        addInfo("Shipping rate deleted successfully.");
        return true;
    }

    public ShippingRate getShippingForm() {
        return shippingForm;
    }

    public void setShippingForm(ShippingRate shippingForm) {
        this.shippingForm = shippingForm;
    }

    public ShippingThresholdType[] getThresholdTypes() {
        return ShippingThresholdType.values();
    }

    public String thresholdTypeLabel(ShippingThresholdType thresholdType) {
        if (thresholdType == null) {
            return "";
        }
        return thresholdType == ShippingThresholdType.WEIGHT ? "Weight based" : "Price based";
    }

    public String thresholdSuffix(ShippingThresholdType thresholdType) {
        if (thresholdType == ShippingThresholdType.WEIGHT) {
            return "kg";
        }
        return "₹";
    }

    public List<SelectItem> getAvailableStateOptions() {
        List<SelectItem> items = new ArrayList<>();
        for (Map.Entry<String, String> entry : INDIA_STATES.entrySet()) {
            items.add(new SelectItem(entry.getKey(), entry.getValue()));
        }
        return items;
    }

    public List<ShippingRate> getShippingRates() {
        List<ShippingRate> rates = new ArrayList<>(catalogService.getShippingRates());
        rates.sort(Comparator
                .comparing(ShippingRate::getStateName, Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(ShippingRate::getThresholdType, Comparator.nullsLast(Enum::compareTo))
                .thenComparing(ShippingRate::getThresholdValue, Comparator.nullsLast(BigDecimal::compareTo)));
        rates.add(defaultOtherStatesRate());
        return rates;
    }

    public String stateLabel(ShippingRate rate) {
        if (rate == null || isBlank(rate.getStateCode())) {
            return "Other states";
        }
        return rate.getStateName();
    }

    public boolean isDefaultOtherStatesRow(ShippingRate rate) {
        return rate == null || isBlank(rate.getStateCode());
    }

    public String chargeLabel(ShippingRate rate) {
        if (isDefaultOtherStatesRow(rate)) {
            return "Free";
        }
        return "₹ " + rate.getCharge();
    }

    private ShippingRate defaultOtherStatesRate() {
        ShippingRate defaultRate = new ShippingRate();
        defaultRate.setStateCode(null);
        defaultRate.setStateName("Other states");
        defaultRate.setThresholdType(ShippingThresholdType.PRICE);
        defaultRate.setThresholdValue(BigDecimal.ZERO);
        defaultRate.setCharge(BigDecimal.ZERO);
        defaultRate.setActive(true);
        return defaultRate;
    }
}
