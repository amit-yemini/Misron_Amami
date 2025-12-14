package com.example.msa.repositories;

import com.example.msa.entities.MissileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissileTypeRepository extends JpaRepository<MissileType, Integer> {
}
