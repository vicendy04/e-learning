package com.myproject.elearning.demo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "products")
// @EntityListeners(ProductListener.class)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String name;

    String description;

    @Column(nullable = false)
    BigDecimal price;

    @Column(name = "deleted")
    boolean deleted = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ProductStatus status = ProductStatus.DRAFT;

    @Version
    Long version;

    public enum ProductStatus {
        DRAFT,
        PUBLISHED,
        ARCHIVED
    }

    //    @PostPersist
    //    private void afterPersist() {
    //        System.out.println("PostPersist: Product was created - " + name);
    //    }
    //
    //    @PostUpdate
    //    private void afterUpdate() {
    //        System.out.println("PostUpdate: Product was updated - " + name);
    //    }
    //
    //    @PreRemove
    //    private void beforeRemove() {
    //        System.out.println("PreRemove: Product about to be removed - " + name);
    //    }
    //
    //    @PostRemove
    //    private void afterRemove() {
    //        System.out.println("PostRemove: Product was removed - " + name);
    //    }
    //
    //    @PostLoad
    //    private void afterLoad() {
    //        System.out.println("PostLoad: Product was loaded - " + name);
    //    }
}
