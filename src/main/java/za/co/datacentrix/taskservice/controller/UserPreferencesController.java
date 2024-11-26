package za.co.datacentrix.taskservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import za.co.datacentrix.taskservice.entities.UserPreferences;
import za.co.datacentrix.taskservice.payload.request.UserPreferencesRequest;
import za.co.datacentrix.taskservice.service.UserPreferencesService;

@RestController
@RequestMapping("/api/preferences")
public class UserPreferencesController {

    @Autowired
    private UserPreferencesService userPreferencesService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserPreferences> getPreferences(@PathVariable Long userId) {
        UserPreferences preferences = userPreferencesService.getPreferences(userId);
        return ResponseEntity.ok(preferences);
    }

    @PostMapping("/{userId}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserPreferences> savePreferences(
            @PathVariable Long userId,
            @RequestBody UserPreferencesRequest userPreferencesRequest) {

        UserPreferences savedPreferences = userPreferencesService.savePreferences(userId, userPreferencesRequest);
        return ResponseEntity.ok(savedPreferences);
    }

    @PostMapping("/{userId}/subscribe")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Void> subscribeToPushNotifications(
            @PathVariable Long userId,
            @RequestBody String subscriptionJson) {
        userPreferencesService.subscribeUserToPushNotifications(userId, subscriptionJson);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserPreferences> updatePreferences(
            @PathVariable Long userId,
            @RequestBody UserPreferencesRequest userPreferencesRequest) {

        UserPreferences updatedPreferences = userPreferencesService.updatePreferences(userId, userPreferencesRequest);
        return ResponseEntity.ok(updatedPreferences);
    }
}