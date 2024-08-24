package com.ecommerce.models;

import jakarta.persistence.*;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id")
    private Integer roleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name")
    private AppRole roleName;

    public Role() {
    }

    public Role(AppRole roleName) {
        this.roleName = roleName;
    }
}
