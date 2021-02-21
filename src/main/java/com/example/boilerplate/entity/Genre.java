package com.example.boilerplate.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Genre extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;
}
