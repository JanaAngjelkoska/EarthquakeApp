package org.example.earthquakeapp.Web;

import org.example.earthquakeapp.Model.Earthquake;
import org.example.earthquakeapp.Service.Imlementation.EarthquakeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/earthquakes")
public class EarthquakeController {

    private final EarthquakeService service;

    public EarthquakeController(EarthquakeService service) {
        this.service = service;
    }

    @GetMapping("/fetch")
    public String fetchEarthquakes() {
        service.fetchAndStoreEarthquakes();
        return "Earthquakes fetched and stored successfully!";
    }

    @GetMapping
    public List<Earthquake> getAllEarthquakes() {
        return service.findAll();
    }

    @GetMapping("/filter")
    public List<Earthquake> filterByMagnitude(@RequestParam double minMag) {
        return service.filterByMagnitude(minMag);
    }

    @GetMapping("/after")
    public List<Earthquake> getAfterTime(@RequestParam long timestamp) {
        return service.filterAfterTime(timestamp);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEarthquake(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
