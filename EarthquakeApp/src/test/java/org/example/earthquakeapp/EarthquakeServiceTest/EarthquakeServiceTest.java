package org.example.earthquakeapp.EarthquakeServiceTest;

import org.example.earthquakeapp.Model.Earthquake;
import org.example.earthquakeapp.Repository.EarthquakeRepository;
import org.example.earthquakeapp.Service.Imlementation.EarthquakeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EarthquakeServiceTest {

    @Mock
    private EarthquakeRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EarthquakeService earthquakeService;

    private final String sampleGeoJson = """
        {
          "features": [
            {
              "properties": {
                "mag": 5.2,
                "place": "Test 1",
                "title": "M 5.2",
                "time": 1713200000000,
                "magType": "mw"
              }
            },
            {
              "properties": {
                "mag": 3.0,
                "place": "Test 2",
                "title": "M 3.0",
                "time": 1713201000000,
                "magType": "md"
              }
            },
            {
              "properties": {
                "mag": 1.5,
                "place": "Too small",
                "title": "M 1.5",
                "time": 1713202000000,
                "magType": "ml"
              }
            }
          ]
        }
        """;

    @Test
    void fetchAndStoreEarthquakes_shouldStoreAllValidEarthquakes() {

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(sampleGeoJson);

        earthquakeService.fetchAndStoreEarthquakes();

        verify(repository, times(1)).deleteAllInBatch();
        verify(repository, times(1)).saveAll(anyList());
    }

    @Test
    void fetchAndStore_shouldNotSaveWhenNoValidData() {

        String emptyGeoJson = """
        {
          "features": []
        }
        """;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(emptyGeoJson);

        earthquakeService.fetchAndStoreEarthquakes();

        verify(repository, times(1)).deleteAllInBatch();
        verify(repository, never()).saveAll(anyList());
    }

    @Test
    void findAll_shouldReturnAllEarthquakes() {

        List<Earthquake> mockList = List.of(
                createEarthquake(5.0),
                createEarthquake(3.2)
        );

        when(repository.findAll()).thenReturn(mockList);

        List<Earthquake> result = earthquakeService.findAll();

        assertEquals(2, result.size());
        assertEquals(5.0, result.get(0).getMagnitude());
    }

    @Test
    void filterByMagnitude_shouldReturnOnlyAboveThreshold() {

        List<Earthquake> mockList = List.of(
                createEarthquake(6.0),
                createEarthquake(4.5),
                createEarthquake(1.0)
        );

        when(repository.findAll()).thenReturn(mockList);

        List<Earthquake> result = earthquakeService.filterByMagnitude(4.0);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(e -> e.getMagnitude() > 4.0));
    }


    @Test
    void filterAfterTime_shouldReturnOnlyRecentEarthquakes() {

        long timestamp = 1713200000000L;

        List<Earthquake> mockList = List.of(
                createEarthquakeWithTime(5.0, timestamp + 1000),
                createEarthquakeWithTime(4.0, timestamp - 1000)
        );

        when(repository.findAll()).thenReturn(mockList);

        List<Earthquake> result = earthquakeService.filterAfterTime(timestamp);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getTime() >= timestamp);
    }

    @Test
    void deleteById_shouldCallRepository() {

        Long id = 10L;

        earthquakeService.deleteById(id);

        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void filterByMagnitude_shouldHandleNullValues() {

        List<Earthquake> mockList = List.of(
                createEarthquake(5.0),
                createEarthquake(null),
                createEarthquake(2.0)
        );

        when(repository.findAll()).thenReturn(mockList);

        List<Earthquake> result = earthquakeService.filterByMagnitude(3.0);

        assertEquals(1, result.size());
        assertEquals(5.0, result.get(0).getMagnitude());
    }

    private Earthquake createEarthquake(Double mag) {
        Earthquake e = new Earthquake();
        e.setMagnitude(mag);
        e.setTime(System.currentTimeMillis());
        return e;
    }

    private Earthquake createEarthquakeWithTime(Double mag, Long time) {
        Earthquake e = new Earthquake();
        e.setMagnitude(mag);
        e.setTime(time);
        return e;
    }
}