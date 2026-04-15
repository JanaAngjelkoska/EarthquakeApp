package org.example.earthquakeapp.Service;

import org.example.earthquakeapp.Model.Earthquake;

import java.util.List;

public interface IEarthquakeService{

    List<Earthquake> findAll();
    List<Earthquake> filterByMagnitude(double minMag);
    List<Earthquake> filterAfterTime(long time);
    void fetchAndStoreEarthquakes();
    void deleteById(Long id);
}
