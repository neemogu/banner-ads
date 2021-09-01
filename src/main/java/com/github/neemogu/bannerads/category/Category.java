package com.github.neemogu.bannerads.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
@NoArgsConstructor
@Table(name = "category")
public final class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    @NotNull(message = "Category name cannot be null")
    @Size(min = 1, max = 255, message = "Category name must be from 1 to 255 symbols length")
    private String name;
    @Column(name = "req_name")
    @NotNull(message = "Category request name cannot be null")
    @Size(min = 1, max = 255, message = "Category request name must be from 1 to 255 symbols length")
    private String reqName;
    @Column(name = "deleted")
    @JsonIgnore
    private Boolean deleted = false;
}
