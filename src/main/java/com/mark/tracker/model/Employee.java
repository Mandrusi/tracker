package com.mark.tracker.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "t_employee")
public class Employee {
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "employee_skill",
            joinColumns = {
                    @JoinColumn(name = "employee_id", referencedColumnName = "id")
            },

            inverseJoinColumns = {
                    @JoinColumn(name = "skill_id", referencedColumnName = "id")
            }
    )
    private Set<Skill> skills;

    @Id
    @NotBlank
    private String id;
    @NotBlank
    @Column(name = "first_name")
    private String firstName;

    @NotBlank
    @Column(name = "last_name")
    private String lastName;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @NotBlank
    @Column(name = "company_email")
    private String companyEmail;

    @NotBlank
    @Column(name = "birth_date")
    private String birthDate;

    @NotBlank
    @Column(name = "hired_date")
    private String hiredDate;


    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Column(name = "contact_email")
    private String contactEmail;

    @Enumerated(EnumType.STRING)
    private BusinessUnit businessUnit;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @Nullable
    private Employee assignedTo;

}