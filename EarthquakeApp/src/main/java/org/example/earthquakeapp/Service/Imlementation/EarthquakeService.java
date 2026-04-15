package org.example.earthquakeapp.Service.Imlementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.earthquakeapp.Model.Earthquake;
import org.example.earthquakeapp.Repository.EarthquakeRepository;
import org.example.earthquakeapp.Service.IEarthquakeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EarthquakeService implements IEarthquakeService {

    private static final Logger log = LoggerFactory.getLogger(EarthquakeService.class);

    private final EarthquakeRepository repository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String URL =
            "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";

    public EarthquakeService(EarthquakeRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    @Override
    public void fetchAndStoreEarthquakes() {
        try {
            String response = restTemplate.getForObject(URL, String.class);
            JsonNode root = objectMapper.readTree(response);

            JsonNode features = root.get("features");

            if (features == null || !features.isArray()) {
                throw new RuntimeException("Invalid API response");
            }

            repository.deleteAllInBatch();

            List<Earthquake> earthquakes = mapToEarthquakes(features);

            if (!earthquakes.isEmpty()) {
                repository.saveAll(earthquakes);
            }

            log.info("Stored {} earthquakes", earthquakes.size());

        } catch (Exception e) {
            log.error("Error fetching earthquake data", e);
            throw new RuntimeException(e);
        }
    }

    private List<Earthquake> mapToEarthquakes(JsonNode features) {
        List<Earthquake> earthquakes = new ArrayList<>();

        for (JsonNode feature : features) {
            Earthquake eq = mapSingleEarthquake(feature);
            if (eq != null) {
                earthquakes.add(eq);
            }
        }

        return earthquakes;
    }

    private Earthquake mapSingleEarthquake(JsonNode feature) {
        JsonNode properties = feature.get("properties");
        if (properties == null) return null;

        Earthquake eq = new Earthquake();
        eq.setMagnitude(safeDouble(properties.get("mag")));
        eq.setPlace(safeText(properties.get("place"), "Unknown"));
        eq.setTitle(safeText(properties.get("title"), "No title"));
        eq.setTime(safeLong(properties.get("time")));
        eq.setMagType(safeText(properties.get("magType"), "unknown"));

        return eq;
    }

    @Override
    public List<Earthquake> filterByMagnitude(double minMagnitude) {
        return repository.findAll()
                .stream()
                .filter(eq -> eq.getMagnitude() != null && eq.getMagnitude() > minMagnitude)
                .collect(Collectors.toList());
    }

    @Override
    public List<Earthquake> filterAfterTime(long timestamp) {
        return repository.findAll()
                .stream()
                .filter(eq -> eq.getTime() != null && eq.getTime() >= timestamp)
                .collect(Collectors.toList());
    }

    @Override
    public List<Earthquake> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private Double safeDouble(JsonNode node) {
        return (node == null || node.isNull()) ? null : node.asDouble();
    }

    private Long safeLong(JsonNode node) {
        return (node == null || node.isNull()) ? null : node.asLong();
    }

    private String safeText(JsonNode node, String defaultValue) {
        return (node == null || node.isNull()) ? defaultValue : node.asText();
    }
}