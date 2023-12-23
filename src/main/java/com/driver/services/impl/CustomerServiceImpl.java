package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.CabRepository;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Autowired
	CabRepository cabRepository2;

	@Override
	public void register(Customer customer) {
		// Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		customerRepository2.deleteById(customerId);

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		// Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		// Avoid using SQL query
		List<Driver> driverList = driverRepository2.findAll();
		Collections.sort(driverList, (a,b) -> {
			return a.getDriverId() - b.getDriverId();
		});
		Driver driver = null;
		for(Driver d : driverList){
			if(d.getCab().isAvailable() == true){
				driver = d;
				break;
			}
		}
		if(driver == null){
			throw new Exception("No cab available!");
		}
		Customer customer = customerRepository2.findById(customerId).orElse(null);
		int bill = driver.getCab().getPerKmRate() * distanceInKm;
		TripBooking tripBooking = new TripBooking();
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setTripStatus(TripStatus.CONFIRMED);
		tripBooking.setBill(bill);
		tripBooking.setDriver(driver);
		tripBooking.setCustomer(customer);
		tripBookingRepository2.save(tripBooking);
		Cab cab = driver.getCab();
		cab.setAvailable(false);
		cabRepository2.save(cab);
		driver.getTripBookingList().add(tripBooking);
		driverRepository2.save(driver);
		customer.getTripBookingList().add(tripBooking);
		customerRepository2.save(customer);
		return tripBooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		// Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).orElse(null);
		tripBooking.setTripStatus(TripStatus.CANCELED);
		tripBooking.setBill(0);
		tripBookingRepository2.save(tripBooking);
		Cab cab = tripBooking.getDriver().getCab();
		cab.setAvailable(true);
		cabRepository2.save(cab);
		Driver driver = tripBooking.getDriver();
		driver.getTripBookingList().remove(tripBooking);
		driverRepository2.save(driver);
		Customer customer = tripBooking.getCustomer();
		customer.getTripBookingList().remove(tripBooking);
		customerRepository2.save(customer);
	}

	@Override
	public void completeTrip(Integer tripId){
		// Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).orElse(null);
		tripBooking.setTripStatus(TripStatus.COMPLETED);
		tripBookingRepository2.save(tripBooking);
		Cab cab = tripBooking.getDriver().getCab();
		cab.setAvailable(true);
		cabRepository2.save(cab);
		Driver driver = tripBooking.getDriver();
		driver.getTripBookingList().remove(tripBooking);
		driverRepository2.save(driver);
		Customer customer = tripBooking.getCustomer();
		customer.getTripBookingList().remove(tripBooking);
		customerRepository2.save(customer);
	}
}
