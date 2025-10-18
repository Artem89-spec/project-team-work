package ru.projectteamwork.finance_recommendations.controller;


import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("management")
public class ManagementController {

    @CacheEvict(cacheNames = {"recommendationsCache", "ruleCache", "ruleStatCache", "ruleEvaluationCache"}, allEntries = true)
    @PostMapping("/clear-caches")
    public ResponseEntity<String> clearCaches() {
        // Этот метод ничего делать не должен, кроме вызова, аннотированного @CacheEvict
        return ResponseEntity.ok("Кеши успешно очищены");
    }
}

