package za.co.datacentrix.taskservice.service;

import za.co.datacentrix.taskservice.entities.UserPreferences;
import za.co.datacentrix.taskservice.payload.request.UserPreferencesRequest;

public interface UserPreferencesService {

    public UserPreferences getPreferences(Long userId);

    public UserPreferences savePreferences(Long userId, UserPreferencesRequest userPreferencesRequest);

    public void subscribeUserToPushNotifications(Long userId, String subscriptionJson);
    public UserPreferences updatePreferences(Long userId, UserPreferencesRequest userPreferencesRequest);


}
