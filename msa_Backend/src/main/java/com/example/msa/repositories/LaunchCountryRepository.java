package com.example.msa.repositories;

import com.example.msa.entities.LaunchCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaunchCountryRepository extends JpaRepository<LaunchCountry, Integer> {

}
