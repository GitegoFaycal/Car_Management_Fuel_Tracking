package org.backend.controller;

import org.backend.model.Car;
import org.backend.model.FuelEntry;
import org.backend.service.CarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    // Create a car
    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody Car car) {
        Car created = carService.createCar(
                car.getBrand(),
                car.getModel(),
                car.getYear()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // List all cars
    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    // Add fuel entry
    @PostMapping("/{id}/fuel")
    public ResponseEntity<String> addFuel(@PathVariable Long id,
                                          @RequestBody FuelEntry fuelEntry) {
        try {
            carService.addFuel(id, fuelEntry);
            return ResponseEntity.ok("Fuel entry added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Car with id " + id + " not found");
        }
    }

    // Get fuel statistics
    @GetMapping("/{id}/fuel/stats")
    public ResponseEntity<Map<String, Double>> getFuelStats(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(carService.getFuelStats(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
