package com.example.msa.repositories;

import com.example.msa.entities.AlertToMissile;
import com.example.msa.entities.CompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertToMissileRepository extends JpaRepository<AlertToMissile, CompositeKey>{
}
