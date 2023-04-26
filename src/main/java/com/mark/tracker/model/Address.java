package com.mark.tracker.model;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_address")
public class Address {
    @Id
    private String id;
    @Column
    @NotBlank
    private String street;
    @NotBlank
    @Column
    private String suite;
    @NotBlank
    @Column
    private String city;
    @NotBlank
    @Column
    private String region;
    @NotBlank
    @Column
    private String postal;
    @NotBlank
    @Column
    private String country;

}