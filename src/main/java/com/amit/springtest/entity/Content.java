package com.amit.springtest.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Table(name = "content")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String title;

    @Column
    private String description;

    @Column(name = "year_of_release")
    private Long yearOfRelease;

}
