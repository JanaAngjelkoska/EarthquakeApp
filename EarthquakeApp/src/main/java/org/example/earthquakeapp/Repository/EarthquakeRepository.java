package org.example.earthquakeapp.Repository;


import org.example.earthquakeapp.Model.Earthquake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EarthquakeRepository extends JpaRepository<Earthquake, Long> {}