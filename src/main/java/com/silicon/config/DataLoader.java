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
    public void run(String... args) {

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
                        .cityName("Santa Clara")
                        .description("A major tech hub with strong competition.")
                        .latitude(37.3541)
                        .longitude(-121.9552)
                        .build(),

                Location.builder()
                        .routeIndex(2)
                        .cityName("Palo Alto")
                        .description("Home of investors, founders, and startup energy.")
                        .latitude(37.4419)
                        .longitude(-122.1430)
                        .build(),

                Location.builder()
                        .routeIndex(3)
                        .cityName("Mountain View")
                        .description("Engineering pressure rises as the product grows.")
                        .latitude(37.3861)
                        .longitude(-122.0839)
                        .build(),

                Location.builder()
                        .routeIndex(4)
                        .cityName("Redwood City")
                        .description("A midpoint checkpoint where momentum matters.")
                        .latitude(37.4852)
                        .longitude(-122.2364)
                        .build(),

                Location.builder()
                        .routeIndex(5)
                        .cityName("San Mateo")
                        .description("Momentum builds as the team gets closer to the goal.")
                        .latitude(37.5630)
                        .longitude(-122.3255)
                        .build(),

                Location.builder()
                        .routeIndex(6)
                        .cityName("South San Francisco")
                        .description("The pressure increases as the final stretch begins.")
                        .latitude(37.6547)
                        .longitude(-122.4077)
                        .build(),

                Location.builder()
                        .routeIndex(7)
                        .cityName("Brisbane")
                        .description("A small stop before the final push into the city.")
                        .latitude(37.6808)
                        .longitude(-122.3997)
                        .build(),

                Location.builder()
                        .routeIndex(8)
                        .cityName("Daly City")
                        .description("One of the last checkpoints before the destination.")
                        .latitude(37.6879)
                        .longitude(-122.4702)
                        .build(),

                Location.builder()
                        .routeIndex(9)
                        .cityName("San Francisco")
                        .description("Final destination where the startup must prove itself.")
                        .latitude(37.7749)
                        .longitude(-122.4194)
                        .build()
        );

        locationRepository.saveAll(locations);
        System.out.println("Locations seeded successfully.");
    }
}