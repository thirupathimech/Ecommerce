package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.MediaFile;
import com.aadhik.ecommerce.model.Product;
import com.aadhik.ecommerce.model.ProductCollection;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.primefaces.model.DualListModel;

/**
 * @author THIRUPATHI G
 */
@Named
@ViewScoped
public class ProductBean extends AdminBean {

    private Product productForm;
    private boolean productEditorVisible;
    private DualListModel<String> productCollectionPickList;
    private List<ProductVariantInput> variantInputs;

    @Override
    public void resetForm() {
        productForm = new Product();
        productForm.setCollections(new ArrayList<>());
        productForm.setActive(true);
        productForm.setHasVariants(false);
        variantInputs = new ArrayList<>();
        variantInputs.add(new ProductVariantInput());
        productCollectionPickList = null;
        productEditorVisible = true;
    }

    @Override
    public void saveForm() {
        if (!validateForm()) {
            return;
        }
        if (productForm.isHasVariants()) {
            productForm.setVariantData(serializeVariants(variantInputs));
        } else {
            productForm.setVariantData(null);
        }
        try {
            productForm.setCollections(resolveCollectionsFromPickList(productCollectionPickList));
            catalogService.saveProduct(productForm);
            resetForm();
            productEditorVisible = false;
            addInfo("Product saved successfully");
        } catch (Exception ex) {
            addError("Product update failed.");
        }
    }

    @Override
    public boolean validateForm() {
        if (isBlank(productForm.getName())) {
            addError("Product title is required.");
            return false;
        }

        if (isBlank(productForm.getDescription())) {
            addError("Product description is required.");
            return false;
        }

        if (isBlank(productForm.getSku())) {
            addError("SKU is required.");
            return false;
        }

        if (catalogService.isExistSKU(productForm.getSku(), productForm.getId())) {
            addError("Duplicate SKU cannot be allowed");
            return false;
        }

        if (isBlank(productForm.getImageUrl())) {
            addError("Primary product image selection is required.");
            return false;
        }

        if (productForm.getPrice() == null || productForm.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            addError("Product price must be greater than 0.");
            return false;
        }

        if (productForm.getComparePrice() != null
                && productForm.getComparePrice().compareTo(productForm.getPrice()) < 0) {
            addError("Compare price must be greater than or equal to product price.");
            return false;
        }

        if (!productForm.isHasVariants()) {
            return true;
        }

        if (variantInputs.isEmpty()) {
            addError("At least one variant is required.");
            return false;
        }

        for (ProductVariantInput variant : variantInputs) {
            if (isBlank(variant.getColor())) {
                addError("Variant color is required.");
                return false;
            }
            if (isBlank(variant.getImageUrl())) {
                addError("Variant image selection is required.");
                return false;
            }
            if (variant.getPrice() == null || variant.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                addError("Variant price must be greater than 0.");
                return false;
            }
        }
        return true;
    }

    @Override
    public void editForm(Object form) {
        if (form instanceof Product product) {
            Product draft = new Product();
            draft.setId(product.getId());
            draft.setName(product.getName());
            draft.setDescription(product.getDescription());
            draft.setSku(product.getSku());
            draft.setHsn(product.getHsn());
            draft.setImageUrl(product.getImageUrl());
            draft.setGalleryImages(product.getGalleryImages());
            draft.setPrice(product.getPrice());
            draft.setComparePrice(product.getComparePrice());
            draft.setWeight(product.getWeight());
            draft.setHasVariants(product.isHasVariants());
            draft.setVariantData(product.getVariantData());
            draft.setFeatured(product.isFeatured());
            draft.setActive(product.isActive());
            draft.setCollections(toCollectionReferences(product.getCollections()));
            productForm = draft;
            loadProductCollectionPickList(product.getCollections());

            variantInputs = deserializeVariants(product.getVariantData());
            if (variantInputs.isEmpty()) {
                variantInputs.add(new ProductVariantInput());
            }
            activeMenu = "products";
            productEditorVisible = true;
        }
    }

    @Override
    public boolean deleteForm(Object form) {
        return false;
    }

    public List<ProductVariantInput> getVariantInputs() {
        return variantInputs;
    }

    public boolean isProductEditorVisible() {
        return productEditorVisible;
    }

    public DualListModel<String> getProductCollectionPickList() {
        if (productCollectionPickList == null) {
            loadProductCollectionPickList(productForm == null ? null : productForm.getCollections());
        }
        return productCollectionPickList;
    }

    public void setProductCollectionPickList(DualListModel<String> productCollectionPickList) {
        this.productCollectionPickList = productCollectionPickList;
    }

    public Product getProductForm() {
        return productForm;
    }

    public void setProductForm(Product productForm) {
        this.productForm = productForm;
    }

    public void openNewProductForm() {
        activeMenu = "products";
        productEditorVisible = true;
        resetForm();
    }

    public void cancelProductEditor() {
        resetForm();
        productEditorVisible = false;
    }

    public void addVariantRow() {
        variantInputs.add(new ProductVariantInput());
    }

    public void removeVariantRow(int index) {
        if (index >= 0 && index < variantInputs.size()) {
            variantInputs.remove(index);
        }
        if (variantInputs.isEmpty()) {
            variantInputs.add(new ProductVariantInput());
        }
    }

    private String serializeVariants(List<ProductVariantInput> variants) {
        StringBuilder builder = new StringBuilder();
        for (ProductVariantInput variant : variants) {
            if (builder.length() > 0) {
                builder.append("\n");
            }
            builder.append(escape(variant.getColor())).append("|")
                    .append(escape(variant.getImageUrl())).append("|")
                    .append(variant.getPrice() == null ? "" : variant.getPrice()).append("|")
                    .append(variant.getComparePrice() == null ? "" : variant.getComparePrice()).append("|")
                    .append(variant.getWeight() == null ? "" : variant.getWeight());
        }
        return builder.toString();
    }

    private List<ProductVariantInput> deserializeVariants(String variantData) {
        List<ProductVariantInput> variants = new ArrayList<>();
        if (isBlank(variantData)) {
            return variants;
        }

        String[] lines = variantData.split("\\n");
        for (String line : lines) {
            String[] parts = line.split("\\|", -1);
            ProductVariantInput variant = new ProductVariantInput();
            variant.setColor(unescape(getSafe(parts, 0)));
            variant.setImageUrl(unescape(getSafe(parts, 1)));
            variant.setPrice(toDecimal(getSafe(parts, 2)));
            variant.setComparePrice(toDecimal(getSafe(parts, 3)));
            variant.setWeight(toDecimal(getSafe(parts, 4)));
            variants.add(variant);
        }

        return variants;
    }

    private void loadProductCollectionPickList(List<ProductCollection> selectedCollections) {
        List<String> source = getCollections().stream()
                .filter(ProductCollection::isActive)
                .map(collection -> String.valueOf(collection.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
        List<String> target = new ArrayList<>();
        if (selectedCollections != null) {
            for (ProductCollection collection : selectedCollections) {
                if (collection == null || collection.getId() == null) {
                    continue;
                }
                String idText = String.valueOf(collection.getId());
                if (source.contains(idText) && !target.contains(idText)) {
                    target.add(idText);
                }
            }
        }
        source.removeAll(target);
        productCollectionPickList = new DualListModel<>(source, target);
    }

    private List<ProductCollection> resolveCollectionsFromPickList(DualListModel<String> pickList) {
        List<ProductCollection> selectedCollections = new ArrayList<>();
        if (pickList == null) {
            return selectedCollections;
        }
        for (String idText : pickList.getTarget()) {
            if (isBlank(idText)) {
                continue;
            }
            try {
                Long id = Long.valueOf(idText);
                ProductCollection collection = new ProductCollection();
                collection.setId(id);
                selectedCollections.add(collection);
            } catch (NumberFormatException ignored) {
            }
        }
        return selectedCollections;
    }

    private String getSafe(String[] values, int index) {
        if (index < values.length) {
            return values[index];
        }
        return "";
    }

    private BigDecimal toDecimal(String text) {
        if (isBlank(text)) {
            return null;
        }
        return new BigDecimal(text);
    }

    private String escape(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("|", "%7C").replace("\n", "%0A");
    }

    private String unescape(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("%7C", "|").replace("%0A", "\n");
    }

    public void selectFile(MediaFile file) {
        String ref = toDbFileRef(file.getId());
        if ("primary".equals(fileSelectionTarget)) {
            productForm.setImageUrl(ref);
        } else if ("variant".equals(fileSelectionTarget)
                && fileSelectionVariantIndex >= 0
                && fileSelectionVariantIndex < variantInputs.size()) {
            variantInputs.get(fileSelectionVariantIndex).setImageUrl(ref);
        } else if ("gallery".equals(fileSelectionTarget)) {
            if (isBlank(productForm.getGalleryImages())) {
                productForm.setGalleryImages(ref);
            } else if (!productForm.getGalleryImages().contains(ref)) {
                productForm.setGalleryImages(productForm.getGalleryImages() + "," + ref);
            }
        }
        addInfo("File selected");
    }

    public static class ProductVariantInput implements Serializable {

        private String color;
        private String imageUrl;
        private BigDecimal price;
        private BigDecimal comparePrice;
        private BigDecimal weight;

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public BigDecimal getComparePrice() {
            return comparePrice;
        }

        public void setComparePrice(BigDecimal comparePrice) {
            this.comparePrice = comparePrice;
        }

        public BigDecimal getWeight() {
            return weight;
        }

        public void setWeight(BigDecimal weight) {
            this.weight = weight;
        }
    }

}
