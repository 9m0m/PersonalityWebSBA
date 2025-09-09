package com.sba301.premium_service.repository;

import com.sba301.premium_service.entity.Premium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PremiumRepository extends JpaRepository<Premium, Integer> {
}
