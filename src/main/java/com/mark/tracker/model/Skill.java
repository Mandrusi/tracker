package com.mark.tracker.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Entity
@Table(name = "t_skill")
public class Skill{
    @ManyToMany(fetch = FetchType.LAZY)
    //mappedBy = "skills"
    private Set<Employee> employees;

    @Id
    private String id;

    @NonNull
    @OneToOne(cascade = CascadeType.ALL)
    private Field field;

    @Column
    private int experience;

    @Column
    private String summary;
}