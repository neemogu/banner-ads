package com.github.neemogu.bannerads.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface RequestRepository extends JpaRepository<Request, Integer> {
    void deleteAllByDateBefore(Date date);
}
