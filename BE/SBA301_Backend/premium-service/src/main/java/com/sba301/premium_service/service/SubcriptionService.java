package com.sba301.premium_service.service;

import com.sba301.premium_service.dto.SubcriptionRequest;
import com.sba301.premium_service.dto.SubcriptionResponse;
import com.sba301.premium_service.entity.Subscription;
import com.sba301.premium_service.mapper.SubcriptionMapper;
import com.sba301.premium_service.repository.PremiumRepository;
import com.sba301.premium_service.repository.SubcriptionRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubcriptionService {
    private final SubcriptionRepository subcriptionRepository;
    private final SubcriptionMapper subcriptionMapper;
    private final PremiumRepository premiumRepository;

    public URI createSubscription(SubcriptionRequest subcriptionRequest) {
        var subscription = subcriptionMapper.toSubscription(subcriptionRequest);
        var premium = premiumRepository.findById(subcriptionRequest.premiumId())
                .orElseThrow(() -> new IllegalArgumentException("Premium with ID " + subcriptionRequest.premiumId() + " does not exist."));
        subscription.setPremium(premium);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(subscription.getStartDate().plusDays(premium.getDuration()));
        subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        subscription = subcriptionRepository.save(subscription);
        return URI.create("/subscriptions/" + subscription.getId());
    }

    public List<SubcriptionResponse> getSubscriptions(String uid, Integer premiumId, String status, LocalDate from, LocalDate to) {
        Specification<Subscription> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (uid != null) {
                predicates.add(cb.equal(root.get("uid"), uid));
            }
            if (premiumId != null) {
                predicates.add(cb.equal(root.get("premium").get("id"), premiumId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), Subscription.SubscriptionStatus.valueOf(status.toUpperCase())));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), to));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return subcriptionRepository.findAll(spec)
                .stream()
                .map(subcriptionMapper::toSubcriptionResponse)
                .toList();
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // Run every 24 hours
    public void updateExpiredSubscriptions() {
        LocalDate today = LocalDate.now();
        List<Subscription> expiredSubscriptions = subcriptionRepository.findByStatus(Subscription.SubscriptionStatus.ACTIVE);

        for (Subscription subscription : expiredSubscriptions) {
            if (subscription.getEndDate().isBefore(today)) {
                subscription.setStatus(Subscription.SubscriptionStatus.EXPIRED);
                subcriptionRepository.save(subscription);
            }
        }
    }
}
