package com.mypackage.repository;

import com.mypackage.entity.City;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, String> {
    City findByCityName(String cityName);
}
