package com.myproject.elearning.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * A role.
 */
@Setter
@Getter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    private String name;

    @JsonIgnoreProperties("roles")
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    /* Uncomment when need
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
    		name = "roles_privileges",
    		joinColumns = @JoinColumn(
    				name = "role_name",
    				referencedColumnName = "name"),
    		inverseJoinColumns = @JoinColumn(
    				name = "privilege_name",
    				referencedColumnName = "name"))
    private Set<Privilege> privileges;
     */
}
