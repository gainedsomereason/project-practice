package com.example.demo.mapper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.ChargingSuccess;

@Repository
public interface ChargingSuccessMapper extends JpaRepository<ChargingSuccess, Long>{

}
