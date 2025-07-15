package com.septeo.ulyses.technical.test.repository;

import com.septeo.ulyses.technical.test.entity.Brand;
import com.septeo.ulyses.technical.test.entity.Sales;
import com.septeo.ulyses.technical.test.entity.Vehicle;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Sales entity.
 */
@Repository
public interface SalesRepository {
    /**
     * Find all sales.
     *
     * @return a list of all sales
     */
    List<Sales> findAll(Long pages);

    /**
     * Find a sale by its ID.
     *
     * @param id the ID of the sale to find
     * @return an Optional containing the sale if found, or empty if not found
     */
    Optional<Sales> findById(Long id);

    /**
     * Find Sales By Brand Id
     *
     * @param id the ID of the brand to find the sales
     * @return a list of sales of this brand
     */
    List<Sales> getSalesByBrandId(Long id);

    /**
     * Find Sales by Vehicle id
     *
     * @param id the ID of the vehicle to find his sales
     * @return a list of sales of this vehicle
     */
    List<Sales> getSalesByVehicleId(Long id);

    /**
     * Find the 5 best sales of all the sales
     *
     * @return a list of the 5 bes sales
     */
    List<Vehicle> getBestSellingVehicle(LocalDate start,LocalDate end);
}
