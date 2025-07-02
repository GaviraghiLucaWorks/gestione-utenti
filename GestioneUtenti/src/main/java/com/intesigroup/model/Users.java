package com.intesigroup.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String name;
    private String surname;
    private String tax_code;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Roles> roles = new HashSet<>();

    //Costruttore JPA
    public Users() {
    }

    public Users(String username, String email, String name, String surname, String tax_code) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.tax_code = tax_code;
    }

    // Getter e Setter
    public Long getId() { return id;}

    public String getUsername() {return username;}
    public void setUsername(String username) { this.username = username;}

    public String getEmail() {return email;}
    public void setEmail(String email) { this.email = email;}

    public String getName() {return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getTax_code() { return tax_code; }
    public void setTax_code(String tax_code) { this.tax_code = tax_code; }

    public Set<Roles> getRoles() { return roles; }
    public void setRoles(Set<Roles> roles) { this.roles = roles; }

}
