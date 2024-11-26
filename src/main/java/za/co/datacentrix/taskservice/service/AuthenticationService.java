package za.co.datacentrix.taskservice.service;


import za.co.datacentrix.taskservice.entities.User;
import za.co.datacentrix.taskservice.payload.request.AuthenticationRequest;
import za.co.datacentrix.taskservice.payload.request.RegisterRequest;
import za.co.datacentrix.taskservice.payload.request.UserUpdateRequest;
import za.co.datacentrix.taskservice.payload.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request, boolean createdByAdmin, String token);
    AuthenticationResponse authenticate(AuthenticationRequest request);

    public User getUserById(Long id);


    public User updateUser(Long userId, UserUpdateRequest userUpdateRequest);

    public void changePassword(String email, String currentPassword, String newPassword);


}
