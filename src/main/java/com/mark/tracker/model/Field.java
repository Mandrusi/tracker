package com.mark.tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_field")
public class Field {

    @Id
    private String id;

    @NotBlank
    @Column
    private String name;

    @NotBlank
    @Column
    private String type;
}