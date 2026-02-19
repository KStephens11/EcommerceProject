package com.tus.ecom.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class RoleEntity {

    public RoleEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public RoleEntity() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // ADMIN, CUSTOMER

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
