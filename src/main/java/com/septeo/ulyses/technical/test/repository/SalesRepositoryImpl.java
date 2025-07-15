package com.septeo.ulyses.technical.test.repository;

import com.septeo.ulyses.technical.test.entity.Sales;
import com.septeo.ulyses.technical.test.entity.Vehicle;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

/**
 * Implementation of the SalesRepository interface.
 * This class provides the implementation for all sales-related operations.
 */
@Repository
public class SalesRepositoryImpl implements SalesRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Sales> findAll(Long page) {
        int pageSize = 10;
        int pageNumber = page.intValue();

        String stringQuery = "SELECT s FROM Sales s";
        Query query = entityManager.createQuery(stringQuery);

        query.setFirstResult(pageNumber * pageSize);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    @Override
    public Optional<Sales> findById(Long id) {
        String stringQuery = "SELECT s FROM Sales s WHERE s.id = :id";
        Query query = entityManager.createQuery(stringQuery);
        query.setParameter("id", id);

        try {
            return Optional.of((Sales) query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    @Override
    public List<Sales> getSalesByBrandId(Long id) {
        String stringQuery = "SELECT s FROM Sales s WHERE s.brand.id = :id";
        Query query = entityManager.createQuery(stringQuery);
        query.setParameter("id", id);
        return query.getResultList();
    }

    @Override
    public List<Sales> getSalesByVehicleId(Long id) {
        String stringQuery = "SELECT s FROM Sales s WHERE s.vehicle.id = :id";
        Query query = entityManager.createQuery(stringQuery);
        query.setParameter("id", id);
        return query.getResultList();
    }

    @Override
    public List<Vehicle> getBestSellingVehicle(LocalDate start, LocalDate end) {
        String stringQuery = "SELECT s FROM Sales s";
        if (start != null && end != null) stringQuery += " WHERE s.saleDate BETWEEN :start AND :end";
        else if (start != null) stringQuery += " WHERE s.saleDate >= :start";
        else if (end != null)  stringQuery += " WHERE s.saleDate <= :end";

        Query query = entityManager.createQuery(stringQuery, Sales.class);
        if (start != null) query.setParameter("start", start);
        if (end != null) query.setParameter("end", end);

        List<Sales> salesList = query.getResultList();

        // Contar ventas por vehículo
        Map<Vehicle, Integer> vehicleCount = new HashMap<>();
        for (Sales sale : salesList) {
            Vehicle vehicle = sale.getVehicle();
            vehicleCount.put(vehicle, vehicleCount.getOrDefault(vehicle, 0) + 1);
        }

        // Seleccionar los 5 vehículos más vendidos manualmente (sin sort, ni utilidades)
        List<Vehicle> topVehicles = new ArrayList<>();
        for (int i = 0; i < 5 && !vehicleCount.isEmpty(); i++) {
            Vehicle maxVehicle = null;
            int maxCount = -1;
            for (Map.Entry<Vehicle, Integer> entry : vehicleCount.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    maxVehicle = entry.getKey();
                }
            }
            if (maxVehicle != null) {
                topVehicles.add(maxVehicle);
                vehicleCount.remove(maxVehicle);
            }
        }

        return topVehicles;
    }


}
