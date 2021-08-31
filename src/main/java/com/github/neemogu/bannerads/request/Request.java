package com.github.neemogu.bannerads.request;

import com.github.neemogu.bannerads.banner.Banner;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@Table(name = "request")
public final class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(targetEntity = Banner.class)
    @JoinColumn(name = "banner_id")
    private Banner banner;

    @Column(name = "user_agent")
    private String userAgent;
    @Column(name = "ip_address")
    private String ipAddress;
    @Column(name = "date")
    private Date date;
}
