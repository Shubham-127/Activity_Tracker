package com.example.activity_tracker_monitor.repository;


import com.example.activity_tracker_monitor.model.InstallToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


    public interface InstallTokenRepository extends JpaRepository<InstallToken, Long>{

        Optional<InstallToken> findByTokenAndUsedFalse(String token);
    }

