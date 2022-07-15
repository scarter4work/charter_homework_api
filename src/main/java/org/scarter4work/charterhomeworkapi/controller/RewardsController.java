package org.scarter4work.charterhomeworkapi.controller;

import org.scarter4work.charterhomeworkapi.dto.RewardDTO;
import org.scarter4work.charterhomeworkapi.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/rewards")
public class RewardsController {
    private static final String ID = "id";

    private final RewardService rewardService;

    public RewardsController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping("/quarterly/customer/{id}")
    public ResponseEntity<RewardDTO> getRewardsForQuarter(@PathVariable(ID) Integer id) {
        if (id == null) return ResponseEntity.badRequest().build();
        RewardDTO rewardDTO;
        try {
            rewardDTO = this.rewardService.calculateRewardPoints(id);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return rewardDTO != null ? ResponseEntity.ok(rewardDTO) : ResponseEntity.notFound().build();
    }
}
