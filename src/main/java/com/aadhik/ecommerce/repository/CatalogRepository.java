package com.aadhik.ecommerce.repository;

import com.aadhik.ecommerce.model.HomeDivSection;
import com.aadhik.ecommerce.model.HomeSlider;
import com.aadhik.ecommerce.model.HomepageSection;
import com.aadhik.ecommerce.model.MarqueeConfig;
import com.aadhik.ecommerce.model.MediaFile;
import com.aadhik.ecommerce.model.Product;
import com.aadhik.ecommerce.model.ProductCollection;
import com.aadhik.ecommerce.model.VideoCarouselItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class CatalogRepository {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager entityManager;

    public List<HomeSlider> findHomeSliders(boolean activeOnly) {
        StringBuilder query = new StringBuilder(" select s from HomeSlider s ");
        if (activeOnly) {
            query.append(" where s.active = true ");
        }
        query.append(" order by s.sortOrder asc, s.id asc ");
        return entityManager.createQuery(query.toString(), HomeSlider.class).getResultList();
    }

    public List<HomeDivSection> findHomeDivSections(boolean activeOnly) {
        StringBuilder query = new StringBuilder(" select d from HomeDivSection d ");
        if (activeOnly) {
            query.append(" where d.active = true ");
        }
        query.append(" order by d.sortOrder asc, d.id asc ");
        return entityManager.createQuery(query.toString(), HomeDivSection.class).getResultList();
    }

    public List<VideoCarouselItem> findVideoCarouselItems(boolean activeOnly) {
        StringBuilder query = new StringBuilder(" select v from VideoCarouselItem v ");
        if (activeOnly) {
            query.append(" where v.active = true ");
        }
        query.append(" order by v.sortOrder asc, v.id asc ");
        return entityManager.createQuery(query.toString(), VideoCarouselItem.class).getResultList();
    }

    public List<HomepageSection> findActiveSections() {
        return entityManager.createQuery("""
                        select hs from HomepageSection hs
                        left join fetch hs.collection c
                        where hs.active = true
                        order by hs.sortOrder asc, hs.id asc
                        """, HomepageSection.class)
                .getResultList();
    }

    public List<Product> findFeaturedProducts(int limit) {
        return entityManager.createQuery("""
                        select p from Product p
                        where p.active = true and p.featured = true
                        order by p.id desc
                        """, Product.class)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Product> findProductsByCollection(Long collectionId, int limit) {
        return entityManager.createQuery("""
                        select p from Product p
                        where p.active = true and p.collection.id = :collectionId
                        order by p.id desc
                        """, Product.class)
                .setParameter("collectionId", collectionId)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<ProductCollection> findCollections(boolean activeCollection) {
        StringBuilder queryBuilder = new StringBuilder(" select c from ProductCollection c ");
        if (activeCollection) {
            queryBuilder.append(" where c.active = true ");
        }
        queryBuilder.append(" order by c.id desc ");
        return entityManager.createQuery(queryBuilder.toString(), ProductCollection.class).getResultList();
    }

    public List<Product> findProducts() {
        return entityManager.createQuery("""
                        select p from Product p
                        left join fetch p.collection c
                        order by p.id desc
                        """, Product.class)
                .getResultList();
    }

    public List<MarqueeConfig> findMarqueeConfigs() {
        return entityManager.createQuery("""
                        select m from MarqueeConfig m
                        order by m.id desc
                        """, MarqueeConfig.class)
                .getResultList();
    }

    public MarqueeConfig findActiveMarqueeConfig() {
        List<MarqueeConfig> configs = entityManager.createQuery("""
                        select m from MarqueeConfig m
                        where m.active = true
                        order by m.id desc
                        """, MarqueeConfig.class)
                .setMaxResults(1)
                .getResultList();
        return configs.isEmpty() ? null : configs.get(0);
    }

    public List<MediaFile> findMediaFiles() {
        return entityManager.createQuery("""
                        select m from MediaFile m
                        order by m.id desc
                        """, MediaFile.class)
                .getResultList();
    }

    public MediaFile findMediaFileById(Long id) {
        return entityManager.find(MediaFile.class, id);
    }

    @Transactional
    public void deleteMediaFileById(Long id) {
        MediaFile mediaFile = entityManager.find(MediaFile.class, id);
        if (mediaFile != null) {
            entityManager.remove(mediaFile);
        }
    }

    public long countFileUsage(Long fileId) {
        String ref = "dbfile:" + fileId;
        Long productUsage = entityManager.createQuery("""
                        select count(p) from Product p
                        where p.imageUrl = :ref
                           or p.galleryImages like :refLike
                           or p.variantData like :refLike
                        """, Long.class)
                .setParameter("ref", ref)
                .setParameter("refLike", "%" + ref + "%")
                .getSingleResult();
        Long sliderUsage = entityManager.createQuery("""
                        select count(s) from HomeSlider s
                        where s.imageUrl = :ref
                        """, Long.class)
                .setParameter("ref", ref)
                .getSingleResult();

        Long collectionUsage = entityManager.createQuery("""
                        select count(c) from ProductCollection c
                        where c.bannerImage = :ref
                        """, Long.class)
                .setParameter("ref", ref)
                .getSingleResult();

        Long videoCarouselUsage = entityManager.createQuery("""
                        select count(v) from VideoCarouselItem v
                        where v.thumbnailUrl = :ref
                           or v.videoUrl = :ref
                        """, Long.class)
                .setParameter("ref", ref)
                .getSingleResult();

        Long divSectionUsage = entityManager.createQuery("""
                        select count(d) from HomeDivSection d
                        where d.imageUrl = :ref
                        """, Long.class)
                .setParameter("ref", ref)
                .getSingleResult();

        return productUsage + sliderUsage + collectionUsage + videoCarouselUsage + divSectionUsage;
    }

    @Transactional
    public HomeDivSection saveHomeDivSection(HomeDivSection section) {
        if (section.getId() == null) {
            entityManager.persist(section);
            return section;
        }
        return entityManager.merge(section);
    }

    @Transactional
    public void deleteHomeDivSection(Long id) {
        HomeDivSection section = entityManager.find(HomeDivSection.class, id);
        if (section != null) {
            entityManager.remove(section);
        }
    }

    @Transactional
    public VideoCarouselItem saveVideoCarouselItem(VideoCarouselItem item) {
        if (item.getId() == null) {
            entityManager.persist(item);
            return item;
        }
        return entityManager.merge(item);
    }

    @Transactional
    public void deleteVideoCarouselItem(Long id) {
        VideoCarouselItem item = entityManager.find(VideoCarouselItem.class, id);
        if (item != null) {
            entityManager.remove(item);
        }
    }

    @Transactional
    public HomeSlider saveSlider(HomeSlider slider) {
        if (slider.getId() == null) {
            entityManager.persist(slider);
            return slider;
        }
        return entityManager.merge(slider);
    }

    @Transactional
    public HomepageSection saveSection(HomepageSection section) {
        if (section.getCollection() != null) {
            if (section.getCollection().getId() != null) {
                ProductCollection managedCollection = entityManager.find(ProductCollection.class, section.getCollection().getId());
                section.setCollection(managedCollection);
            } else {
                section.setCollection(null);
            }
        }

        if (section.getId() == null) {
            entityManager.persist(section);
            return section;
        }

        return entityManager.merge(section);
    }

    @Transactional
    public ProductCollection saveCollection(ProductCollection collection) {
        if (collection.getId() == null) {
            entityManager.persist(collection);
            return collection;
        }
        return entityManager.merge(collection);
    }

    @Transactional
    public Product saveProduct(Product product) {
        if (product.getCollection() != null) {
            if (product.getCollection().getId() != null) {
                ProductCollection managedCollection = entityManager.find(ProductCollection.class, product.getCollection().getId());
                product.setCollection(managedCollection);
            } else {
                product.setCollection(null);
            }
        }

        if (product.getId() == null) {
            entityManager.persist(product);
            return product;
        }

        return entityManager.merge(product);
    }

    @Transactional
    public MarqueeConfig saveMarqueeConfig(MarqueeConfig marqueeConfig) {
        if (marqueeConfig.getId() == null) {
            entityManager.persist(marqueeConfig);
            return marqueeConfig;
        }
        return entityManager.merge(marqueeConfig);
    }

    @Transactional
    public boolean deleteMarqueeConfig(Long id) {
        MarqueeConfig entity = entityManager.find(MarqueeConfig.class, id);
        if (entity == null) {
            return false;
        }
        entityManager.remove(entity);
        return true;
    }

    @Transactional
    public MediaFile saveMediaFile(MediaFile mediaFile) {
        if (mediaFile.getId() == null) {
            entityManager.persist(mediaFile);
            return mediaFile;
        }
        return entityManager.merge(mediaFile);
    }

    @Transactional
    public boolean isExistSKU(String sku, Long ignoreProductId) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(p) FROM Product p WHERE p.sku = :sku");
        if (ignoreProductId != null) {
            jpql.append(" AND p.id <> :id");
        }
        TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);
        query.setParameter("sku", sku);
        if (ignoreProductId != null) {
            query.setParameter("id", ignoreProductId);
        }
        Long count = query.getSingleResult();
        return count > 0;
    }
}
