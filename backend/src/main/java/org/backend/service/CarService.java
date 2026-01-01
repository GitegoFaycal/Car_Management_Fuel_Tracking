package org.backend.service;

import org.backend.model.Car;
import org.backend.model.FuelEntry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CarService {

    private final Map<Long, Car> cars = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    // Create a new car
    public Car createCar(String brand, String model, int year) {
        Car car = new Car(idCounter.getAndIncrement(), brand, model, year);
        cars.put(car.getId(), car);
        return car;
    }

    // Get all cars
    public List<Car> getAllCars() {
        return new ArrayList<>(cars.values());
    }

    // Get a car by ID
    public Car getCar(Long id) {
        Car car = cars.get(id);
        if (car == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found with id " + id);
        }
        return car;
    }

    // Add fuel entry to a car
    public void addFuel(Long carId, FuelEntry entry) {
        Car car = getCar(carId);
        car.getFuelEntries().add(entry);
    }

    // Calculate fuel statistics
    public Map<String, Double> getFuelStats(Long carId) {
        Car car = getCar(carId);
        List<FuelEntry> entries = new ArrayList<>(car.getFuelEntries());

        if (entries.isEmpty()) {
            return Map.of(
                    "totalFuel", 0.0,
                    "totalCost", 0.0,
                    "averageConsumption", 0.0
            );
        }

        // Sort entries by odometer to ensure proper distance calculation
        entries.sort(Comparator.comparingDouble(FuelEntry::getOdometer));

        double totalFuel = entries.stream().mapToDouble(FuelEntry::getLiters).sum();
        double totalCost = entries.stream().mapToDouble(FuelEntry::getPrice).sum();
        double distance = entries.get(entries.size() - 1).getOdometer() - entries.get(0).getOdometer();
        double avgConsumption = (distance > 0) ? (totalFuel / distance * 100) : 0.0;

        return Map.of(
                "totalFuel", totalFuel,
                "totalCost", totalCost,
                "averageConsumption", avgConsumption
        );
    }
}
