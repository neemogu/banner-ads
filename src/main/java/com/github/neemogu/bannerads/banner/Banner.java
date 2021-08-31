package com.github.neemogu.bannerads.banner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.neemogu.bannerads.category.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
@NoArgsConstructor
@Table(name = "banner")
public final class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    @NotNull(message = "Banner name cannot be null")
    @Size(min = 1, max = 255, message = "Banner name must be from 1 to 255 symbols length")
    private String name;
    @Column(name = "price")
    @NotNull(message = "Banner price cannot be null")
    @Min(value = 0, message = "Banner price must be positive or zero value")
    private Double price;
    @ManyToOne(targetEntity = Category.class)
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "content")
    @NotNull(message = "Banner content cannot be null")
    @Size(min = 1, max = 10000, message = "Banner content must be from 1 to 10000 symbols length")
    private String content;
    @Column(name = "deleted")
    @JsonIgnore
    private Boolean deleted;
}
