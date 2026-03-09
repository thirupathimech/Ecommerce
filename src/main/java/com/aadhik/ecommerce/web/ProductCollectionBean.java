package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.MediaFile;
import com.aadhik.ecommerce.model.Product;
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
public class ProductCollectionBean extends AdminBean {

    private ProductCollection collectionForm;
    private ProductCollection selectedCollectionForProducts;
    private DualListModel<String> collectionProductsPickList;

    @Override
    public void resetForm() {
        collectionForm = new ProductCollection();
        collectionForm.setActive(true);
    }

    @Override
    public void saveForm() {
        if (!validateForm()) {
            return;
        }
        catalogService.saveCollection(collectionForm);
        resetForm();
        addInfo("Collection saved successfully");
    }

    @Override
    public boolean validateForm() {
        if (isBlank(collectionForm.getName())) {
            addError("Collection name is required.");
            return false;
        }
        if (isBlank(collectionForm.getSlug())) {
            addError("Collection slug is required.");
            return false;
        }
        return true;
    }

    @Override
    public void editForm(Object form) {
        if (form instanceof ProductCollection collection) {
            ProductCollection draft = new ProductCollection();
            draft.setId(collection.getId());
            draft.setName(collection.getName());
            draft.setSlug(collection.getSlug());
            draft.setBannerImage(collection.getBannerImage());
            draft.setDescription(collection.getDescription());
            draft.setActive(collection.isActive());
            collectionForm = draft;
        }
    }

    @Override
    public boolean deleteForm(Object form) {
        return false;
    }

    public ProductCollection getCollectionForm() {
        return collectionForm;
    }

    public void setCollectionForm(ProductCollection collectionForm) {
        this.collectionForm = collectionForm;
    }

    public void selectFile(MediaFile file) {
        String ref = toDbFileRef(file.getId());
        if ("collection-banner".equals(fileSelectionTarget)) {
            collectionForm.setBannerImage(ref);
        }
        addInfo("File selected");
    }

    public void openCollectionProductsManager(ProductCollection collection) {
        if (collection == null || collection.getId() == null) {
            addError("Invalid collection.");
            return;
        }
        selectedCollectionForProducts = collection;
        loadCollectionProductsPickList(collection);
    }

    private void loadCollectionProductsPickList(ProductCollection collection) {
        List<String> source = getProducts().stream()
                .map(product -> String.valueOf(product.getId()))
                .collect(Collectors.toCollection(ArrayList::new));

        List<String> target = new ArrayList<>();
        for (Product product : getProducts()) {
            boolean inCollection = product.getCollections() != null
                    && product.getCollections().stream().anyMatch(c -> c != null
                    && c.getId() != null
                    && c.getId().equals(collection.getId()));
            if (inCollection) {
                target.add(String.valueOf(product.getId()));
            }
        }
        source.removeAll(target);
        collectionProductsPickList = new DualListModel<>(source, target);
    }

    public void saveCollectionProducts() {
        if (selectedCollectionForProducts == null || selectedCollectionForProducts.getId() == null) {
            addError("Select a collection first.");
            return;
        }
        List<String> selectedProductIds = collectionProductsPickList == null ? List.of() : collectionProductsPickList.getTarget();

        for (Product product : getProducts()) {
            List<ProductCollection> existingCollections = new ArrayList<>();
            if (product.getCollections() != null) {
                for (ProductCollection existing : product.getCollections()) {
                    if (existing != null && existing.getId() != null
                            && !existing.getId().equals(selectedCollectionForProducts.getId())) {
                        existingCollections.add(toCollectionReference(existing));
                    }
                }
            }

            if (selectedProductIds.contains(String.valueOf(product.getId()))) {
                existingCollections.add(toCollectionReference(selectedCollectionForProducts));
            }
            product.setCollections(existingCollections);
            catalogService.saveProduct(product);
        }

        loadCollectionProductsPickList(selectedCollectionForProducts);
        addInfo("Collection products updated successfully");
    }

    public ProductCollection getSelectedCollectionForProducts() {
        return selectedCollectionForProducts;
    }

    public void setSelectedCollectionForProducts(ProductCollection selectedCollectionForProducts) {
        this.selectedCollectionForProducts = selectedCollectionForProducts;
    }

    public DualListModel<String> getCollectionProductsPickList() {
        return collectionProductsPickList;
    }

    public void setCollectionProductsPickList(DualListModel<String> collectionProductsPickList) {
        this.collectionProductsPickList = collectionProductsPickList;
    }

}
