package com.myproject.elearning.demo;

import jakarta.persistence.*;

public class ProductListener {

    @PrePersist
    public void prePersist(Product product) {
        System.out.println("Before Persist: " + product.getName());
    }

    @PostPersist
    public void postPersist(Product product) {
        System.out.println("After Persist: " + product.getName());
    }

    @PreUpdate
    public void preUpdate(Product product) {
        System.out.println("Before Update: " + product.getName());
    }

    @PostUpdate
    public void postUpdate(Product product) {
        System.out.println("After Update: " + product.getName());
    }

    @PostLoad
    public void postLoad(Product product) {
        System.out.println("EntityListener PostLoad: " + product.getName());
    }
}
