package com.myproject.elearning.demo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final EntityManagerFactory entityManagerFactory;

    public void demonstrateLifecycle() {
        log.info("=== BẮT ĐẦU DEMO HIBERNATE LIFECYCLE ===");
        EntityManager em = entityManagerFactory.createEntityManager();

        try {
            demonstrateTransientState(em);
            Product product = demonstratePersistentState(em);
            demonstrateDirtyChecking(em, product);
            demonstrateDetachedAndMerge(em, product);
            demonstrateRemovedState(em, product);
        } finally {
            em.close();
            log.info("=== KẾT THÚC DEMO HIBERNATE LIFECYCLE ===");
        }
    }

    private void demonstrateTransientState(EntityManager em) {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(99.99));
        log.info("1. NEW/TRANSIENT State - Object chưa được quản lý bởi Hibernate, chưa có ID: {}", product.getId());
    }

    private Product demonstratePersistentState(EntityManager em) {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(99.99));

        executeInTransaction(em, () -> {
            em.persist(product);
            em.flush();
        });
        log.info("2. MANAGED/PERSISTENT State - Object đã được quản lý, có ID: {}", product.getId());
        return product;
    }

    private void demonstrateDirtyChecking(EntityManager em, Product product) {
        executeInTransaction(em, () -> {
            product.setPrice(BigDecimal.valueOf(149.99));
            // Không cần gọi update() - Hibernate tự động phát hiện thay đổi
        });
        log.info("3. Dirty Checking - Tự động phát hiện và cập nhật thay đổi: {}", product.getPrice());
    }

    private void demonstrateDetachedAndMerge(EntityManager em, Product product) {
        executeInTransaction(em, () -> {
            em.refresh(product);

            em.clear(); // Detach tất cả các entity
            product.setPrice(BigDecimal.valueOf(199.99));
            log.info("4. DETACHED State - Thay đổi không được theo dõi: {}", product.getPrice());

            Product freshProduct = em.find(Product.class, product.getId());
            if (freshProduct != null) {
                log.info("5.1 Trước MERGE - Entity được quản lý lại: {}", freshProduct.getPrice());
                freshProduct.setPrice(product.getPrice());
                log.info("5.2 Sau MERGE - Entity được quản lý lại: {}", freshProduct.getPrice());
            }
        });
    }

    private void demonstrateRemovedState(EntityManager em, Product product) {
        executeInTransaction(em, () -> {
            Product managedProduct = em.find(Product.class, product.getId());
            if (managedProduct != null) {
                em.remove(managedProduct);
            }
        });

        Product checkProduct = em.find(Product.class, product.getId());
        log.info("6. REMOVED State - Entity {} tồn tại trong DB", (checkProduct == null ? "không còn" : "vẫn còn"));
    }

    private void executeInTransaction(EntityManager em, Runnable operation) {
        try {
            em.getTransaction().begin();
            operation.run();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            log.error("Lỗi trong quá trình thực thi transaction: ", e);
            if (e instanceof OptimisticLockException) {
                log.warn("Phát hiện xung đột version, vui lòng thử lại");
            }
            throw new RuntimeException("Lỗi transaction", e);
        }
    }
}
