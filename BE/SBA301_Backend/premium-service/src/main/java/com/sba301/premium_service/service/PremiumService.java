package com.sba301.premium_service.service;

import com.sba301.premium_service.dto.PremiumCreateRequest;
import com.sba301.premium_service.dto.PremiumResponse;
import com.sba301.premium_service.dto.PremiumUpdateRequest;
import com.sba301.premium_service.entity.Premium;
import com.sba301.premium_service.mapper.PremiumMapper;
import com.sba301.premium_service.repository.PremiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;

    public PremiumResponse updatePremium(int id, PremiumUpdateRequest premiumUpdateRequest) {
        var premium = premiumRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Premium not found with id: " + id));

        return premiumMapper.toPremiumResponse(
                premiumRepository.save(premiumMapper.toPremium(premiumUpdateRequest, premium))
        );
    }

    public PremiumResponse getPremium(int id) {
        return premiumMapper.toPremiumResponse(
                premiumRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Premium not found with id: " + id))
        );
    }

    public List<PremiumResponse> getPremiums() {
        return premiumRepository.findAll()
                .stream()
                .map(premiumMapper::toPremiumResponse)
                .toList();
    }

    public URI createPremium(PremiumCreateRequest premiumCreateRequest) {
        var premium = premiumMapper.toPremium(premiumCreateRequest);
        premium.setStatus(Premium.PremiumStatus.ACTIVE);
        premium = premiumRepository.save(premium);
        return URI.create("/premiums/" + premium.getId());
    }
}
