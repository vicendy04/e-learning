package com.myproject.elearning.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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
