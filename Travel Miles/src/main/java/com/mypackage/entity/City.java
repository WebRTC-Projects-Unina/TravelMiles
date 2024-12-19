package com.mypackage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "homecity")
public class City {
	
    @Id
    @Column(name = "cityName") 
    private String cityName;
    
    @Column(name = "imagePath") 
    private String imagePath;
  
    public City() {
    }

    public City(String cityName, String imagePath) {
        this.cityName = cityName;
        this.imagePath = imagePath;
    }

    
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
