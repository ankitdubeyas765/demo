package com.skyscanner.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.util.List;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name="first_Name")
    private String firstName;

    @Column(name="last_Name")
    private String lastName;

    @Column(name="city")
    private String city;

    @Column(name="email")
    private String email;

    @Column(name="mobile")
    private String mobile;

    @Column(name="state")
    private String state;

    @Column(name="country")
    private String country;

    @Column(name="pinCode")
    private String pinCode;

    @Column(name="password")
    private String password;

    @Column(name="user_photo")
    private String userPhoto;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Trip> trips;

}