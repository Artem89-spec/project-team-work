package ru.projectteamwork.finance_recommendations.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.projectteamwork.finance_recommendations.domain.service.RuleService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @PostMapping("/rule")
    public ResponseEntity<RuleResponse> create(@RequestBody RuleRequest request) {
        return ResponseEntity.ok(ruleService.create(request));
    }

    @GetMapping("/rule")
    public ResponseEntity<Map<String, Object>> list() {
        Map<String, Object> body = new HashMap<>();
        body.put("data", ruleService.list());
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/rule/{productId}")
    public ResponseEntity<Void> delete(@PathVariable UUID productId) {
        ruleService.deleteByProductId(productId);
        return ResponseEntity.noContent().build();
    }
}