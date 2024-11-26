package za.co.datacentrix.taskservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import za.co.datacentrix.taskservice.entities.User;
import za.co.datacentrix.taskservice.entities.UserPreferences;
import za.co.datacentrix.taskservice.payload.request.UserPreferencesRequest;
import za.co.datacentrix.taskservice.repository.UserPreferencesRepository;
import za.co.datacentrix.taskservice.repository.UserRepository;
import za.co.datacentrix.taskservice.service.UserPreferencesService;
import za.co.datacentrix.taskservice.utils.ObjectUtils;

import java.util.concurrent.TimeUnit;

@Service
public class UserPreferencesServiceImpl implements UserPreferencesService {


    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, UserPreferences> redisTemplate;

    @Autowired
    private RedisTemplate<String, UserPreferences> userPreferencesRedisTemplate;

    public UserPreferences getPreferences(Long userId) {
        // Construct the cache key
        String cacheKey = "userPreferences:" + userId;

        // Check if the preferences are in the cache
        UserPreferences cachedPreferences = redisTemplate.opsForValue().get(cacheKey);
        if (cachedPreferences != null) {
            return cachedPreferences; // Return cached preferences
        }

        // Fetch from the database if not in cache
        UserPreferences preferences = userPreferencesRepository.findByUserId(userId);

        // Store the preferences in cache for future access
        if (preferences != null) {
            redisTemplate.opsForValue().set(cacheKey, preferences, 5, TimeUnit.MINUTES); // Cache for 10 minutes
        }

        return preferences; // Return the preferences from the database
    }

    public UserPreferences savePreferences(Long userId, UserPreferencesRequest userPreferencesRequest) {
        // Fetch the user from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create a new UserPreferences entity from the DTO
        UserPreferences preferences = new UserPreferences();
        preferences.setUser(user); // Link the User entity
        preferences.setEmailNotifications(userPreferencesRequest.isEmailNotifications());
        preferences.setTheme(userPreferencesRequest.getTheme());
        preferences.setPriority(userPreferencesRequest.getPriority());
        preferences.setPushNotifications(userPreferencesRequest.isPushNotifications());

        // Save the preferences to the database
        UserPreferences savedPreferences = userPreferencesRepository.save(preferences);

        // Invalidate the cache for this user’s preferences
        String cacheKey = "userPreferences:" + userId;
        redisTemplate.delete(cacheKey);

        return savedPreferences; // Return the saved preferences
    }

    public void subscribeUserToPushNotifications(Long userId, String subscriptionJson) {
        // Fetch existing preferences
        UserPreferences existingPreferences = userPreferencesRepository.findByUserId(userId);
        if (existingPreferences == null) {
            throw new RuntimeException("Preferences not found for user ID: " + userId);
        }

        // Store the push subscription in the preferences
        existingPreferences.setPushSubscription(subscriptionJson); // Assuming you have this field
        existingPreferences.setPushNotifications(true); // Indicate that the user wants push notifications

        // Save the updated preferences
        userPreferencesRepository.save(existingPreferences);

        // Invalidate the cache for this user’s preferences
        String cacheKey = "userPreferences:" + userId;
        redisTemplate.delete(cacheKey);
    }

    public UserPreferences updatePreferences(Long userId, UserPreferencesRequest userPreferencesRequest) {
        // Fetch the existing preferences
        UserPreferences existingPreferences = userPreferencesRepository.findByUserId(userId);
        if (existingPreferences == null) {
            throw new RuntimeException("Preferences not found for user ID: " + userId);
        }

        // Use copyNonNullProperties to update only non-null fields
        ObjectUtils.copyNonNullProperties(userPreferencesRequest, existingPreferences);

        // Save the updated preferences
        UserPreferences updatedPreferences = userPreferencesRepository.save(existingPreferences);

        // Invalidate the cache for this user’s preferences
        String cacheKey = "userPreferences:" + userId;
        redisTemplate.delete(cacheKey);

        return updatedPreferences; // Return the updated preferences
    }


}
