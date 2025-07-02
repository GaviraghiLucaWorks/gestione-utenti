package com.intesigroup.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")

public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // salva il valore come testo (ad es "OWNER", "OPERATOR" ...)
    @Column(name = "name_role")
    private RolesType nameRole;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private Set<Users> users = new HashSet<>();

    public Roles() {
    }

    public Roles(RolesType nameRole) {
        this.nameRole = nameRole;
    }

    public Long getId() { return id; }

    public Set<Users> getUsers() { return users; }
    public void setUsers(Set<Users> users) { this.users = users; }

    public RolesType getNameRole() { return nameRole; }
    public void setNameRole(RolesType nameRole) { this.nameRole = nameRole; }
}

