package com.driver.model;

import javax.persistence.*;

@Entity
public class Cab {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer Id;
    private int perKmRate;
    private boolean available;
    @OneToOne
    private Driver driver;

    public Cab() {
    }

    public Cab(Integer id, int perKmRate, boolean available, Driver driver) {
        Id = id;
        this.perKmRate = perKmRate;
        this.available = available;
        this.driver = driver;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public int getPerKmRate() {
        return perKmRate;
    }

    public void setPerKmRate(int perKmRate) {
        this.perKmRate = perKmRate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
