package com.project.appointmentmanager.entity;

//imports all JPA annotations (@Entity, @Id etc)
import jakarta.persistence.*;
//lombok.Data — auto generates getters, setters, constructors for you
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "users")
//@Data — Lombok generates all getters/setters automatically, you don't write them
//@Entity — tells Hibernate this class is a DB table
//@Table(name = "users") — table name in MySQL will be users
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
//    role is not a String — it's an enum (fixed set of values)
//    @Enumerated(EnumType.STRING) — store the role as a String in DB ("USER", "PROVIDER", "ADMIN")

    @OneToOne(mappedBy = "user")
    private ServiceProvider serviceProvider;

    @OneToMany(mappedBy = "user")
    private List<Appointment> appointments;
}
