package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Occupancy;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ComponentScan
class OccupancyCalculationServiceTest {

    private static Occupancy buildOccupancy(Location location, double occupancy, ZonedDateTime time) {
        Occupancy obj = new Occupancy(location, occupancy, "test");
        obj.setTimestamp(time);
        return obj;
    }

    @Test
    void getAverageOccupancy() {
        Location location = new Location(1L, "LIDL", 41.0D, 8.0D, null, null);
        ZonedDateTime time = ZonedDateTime.now();

        List<Occupancy> occupancyList = new ArrayList<>();
        occupancyList.add(buildOccupancy(location, 0.5, time.minusMinutes(15)));
        occupancyList.add(buildOccupancy(location, 0.8, time.minusMinutes(30)));
        occupancyList.add(buildOccupancy(location, 1.0, time.minusMinutes(45)));

        assertTrue(1.0 > OccupancyService.calculateAverage(occupancyList, time));
    }

    @Test
    void bellCurve() {
        assertTrue(0.95 < OccupancyService.bellCurve(-15));
        assertTrue(1.0 > OccupancyService.bellCurve(-15));

        assertTrue(0.0 < OccupancyService.bellCurve(-30));
        assertTrue(0.0 < OccupancyService.bellCurve(-45));
        assertTrue(0.0 < OccupancyService.bellCurve(-60));
        assertTrue(0.0 < OccupancyService.bellCurve(-105));
        assertTrue(0.0 < OccupancyService.bellCurve(-120));
    }
}