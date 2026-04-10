package com.silicon.config;

import com.silicon.model.Location;
import com.silicon.repositories.LocationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final LocationRepository locationRepository;

    public DataLoader(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public void run(String... args) throws Exception {


        if (locationRepository.count() > 0) {
            return;
        }

        List<Location> locations = List.of(
                Location.builder()
                        .routeIndex(0)
                        .cityName("San Jose")
                        .description("Starting point of the startup journey.")
                        .latitude(37.3382)
                        .longitude(-121.8863)
                        .build(),

                Location.builder()
                        .routeIndex(1)
                        .cityName("San Clare")
                        .description("A major tech hub with strong competition.")
                        .latitude(37.3382)
                        .longitude(-121.9552)
                        .build(),

                Location.builder()
                        .routeIndex(2)
                        .cityName("Palo Alto")
                        .description("Home of investors, founders, and startup energy.")
                        .latitude(37.3382)
                        .longitude(-121.1430)
                        .build(),

                Location.builder()
                        .routeIndex(3)
                        .cityName("Mount View")
                        .description("A stop to regroup and prepare for the next push.")
                        .latitude(37.3382)
                        .longitude(-121.0839)
                        .build(),

                Location.builder()
                        .routeIndex(4)
                        .cityName("Redwood City")
                        .description("A stop to regroup and prepare for the next push.")
                        .latitude(37.4852)
                        .longitude(-121.2364)
                        .build(),

                Location.builder()
                        .routeIndex(5)
                        .cityName("San Mateo")
                        .description("Momentum builds as the team gets closer to the goal.")
                        .latitude(37.5630)
                        .longitude(-121.3255)
                        .build(),

                Location.builder()
                        .routeIndex(6)
                        .cityName("San Francisco")
                        .description("Final destination where the startup must prove itself.")
                        .latitude(37.7749)
                        .longitude(-121.4194)
                        .build()
        );

        locationRepository.saveAll(locations);
        System.out.println("Locations seeded successfully.");

    }
}
