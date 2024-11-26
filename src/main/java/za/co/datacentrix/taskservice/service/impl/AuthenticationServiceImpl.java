package za.co.datacentrix.taskservice.service.impl;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.datacentrix.taskservice.entities.User;
import za.co.datacentrix.taskservice.enums.TokenType;
import za.co.datacentrix.taskservice.exception.AuthenticationException;
import za.co.datacentrix.taskservice.exception.UserNotFoundException;
import za.co.datacentrix.taskservice.payload.request.AuthenticationRequest;
import za.co.datacentrix.taskservice.payload.request.RegisterRequest;
import za.co.datacentrix.taskservice.payload.response.AuthenticationResponse;
import za.co.datacentrix.taskservice.repository.UserRepository;
import za.co.datacentrix.taskservice.service.AuthenticationService;
import za.co.datacentrix.taskservice.service.JwtService;
import za.co.datacentrix.taskservice.service.RefreshTokenService;
import za.co.datacentrix.taskservice.payload.request.UserUpdateRequest;


import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service @Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static Map<String, Integer> sequentialCounter = new HashMap<>();


    @Override
    public AuthenticationResponse register(RegisterRequest request, boolean createdByAdmin, String token) {
        // Generate the BS number
        String nameInitials = getNameInitials(request.getFirstname(), request.getLastname()); // Get initials from name
        String roleInitials = request.getRole().toString().substring(0, 2).toUpperCase(); // Get role initials
        String dcNumber = generateDcNumber(nameInitials, roleInitials); // Generate DC number

        // Determine the email: use provided email or generate one
        String email;
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            email = dcNumber + "@datacentrix.co.za"; // Generate email from dcNumber
        } else {
            email = request.getEmail(); // Use provided email
            // Check if the provided email already exists
            if (userRepository.findByEmail(email).isPresent()) {
                throw new AuthenticationException("Email already in use.");
            }
        }

        User user;

        if (createdByAdmin && (token != null)) {
            String username = jwtService.extractUserName(token);
            User admin = userRepository.findByEmail(username).orElse(null);
            if (admin == null) {
                throw new AuthenticationException("Admin is not associated with any department so he can't create users");
            }

            user = User.builder()
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(email) // Use provided or generated email
                    .password(passwordEncoder.encode(request.getPassword())) // Encode the password
                    .temporaryPassword(true) // Set the temporary password flag
                    .role(request.getRole())
                    .departmentId(admin.getDepartmentId())
                    .dcNumber(dcNumber)
                    .build();
        } else {
            user = User.builder()
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(email) // Use provided or generated email
                    .password(passwordEncoder.encode(request.getPassword())) // Encode the password
                    .temporaryPassword(false) // Regular password is not temporary
                    .role(request.getRole())
                    .departmentId(request.getDepartmentId())
                    .dcNumber(dcNumber) // Set the generated DC number
                    .build();
        }

        user = userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());

        var roles = user.getRole().getAuthorities()
                .stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .toList();

        return AuthenticationResponse.builder()
                .accessToken(jwt)
                .email(user.getEmail())
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .dcNumber(user.getDcNumber())
                .refreshToken(refreshToken.getToken())
                .roles(roles)
                .departmentId(user.getDepartmentId())
                .tokenType(TokenType.BEARER.name())
                .message("User created successfully.")
                .build();
    }

    // Method to generate the BS number
    public String generateDcNumber(String nameInitials, String roleInitials) {
        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear()).substring(2); // Last two digits of the year
        String month = String.format("%02d", now.getMonthValue()); // Current month in 2 digits

        // Generate a random number (6 digits)
        Random random = new Random();
        int randomNumber = random.nextInt(1000000); // Generates a number between 0 and 999999
        String randomNumberString = String.format("%06d", randomNumber); // Format as 6 digits

        // Generate a sequential number based on the name and role
        String key = nameInitials + roleInitials;
        int sequentialNumber = sequentialCounter.getOrDefault(key, 0) + 1;
        sequentialCounter.put(key, sequentialNumber);

        // Construct the unique BS number
        return String.format("DC%s%s%s%03d%s%s", year, month, randomNumberString, sequentialNumber, nameInitials, roleInitials);
    }

    // Method to get initials from first name and last name
    private String getNameInitials(String firstname, String lastname) {
        String firstInitial = firstname.substring(0, 1).toUpperCase(); // First letter of first name
        String lastInitial = lastname.substring(0, 1).toUpperCase(); // First letter of last name
        return firstInitial + lastInitial; // Combine initials
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Starting authentication for identifier: {}", request.getEmail());

        // Attempt to retrieve user from Redis cache
        User user = (User) redisTemplate.opsForValue().get("user:" + request.getEmail());

        // If user is not found in cache, query the database
        if (user == null) {
            Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
            if (!optionalUser.isPresent()) {
                optionalUser = userRepository.findByDcNumber(request.getEmail());
            }

            if (!optionalUser.isPresent()) {
                log.error("User not found for identifier: {}", request.getEmail());
                throw new AuthenticationException("Invalid email or DC number or password");
            }

            user = optionalUser.get();

            // Cache the user data for future requests
            redisTemplate.opsForValue().set("user:" + user.getEmail(), user);
        }

        log.info("Attempting to authenticate user: {}, password: {}", user.getEmail(), request.getPassword());

        Authentication authentication;
        try {
            // Authenticate with the user's email
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for identifier: {}", request.getEmail());
            throw new AuthenticationException("Invalid email or DC number or password");
        }

        log.info("Successfully authenticated user: {}", user.getEmail());

        // Check if the user has a temporary password
        if (user.isTemporaryPassword()) {
            log.warn("User has a temporary password: {}", user.getEmail());
            return AuthenticationResponse.builder()
                    .accessToken(null) // No access token for temporary password users
                    .roles(Collections.emptyList())
                    .email(user.getEmail())
                    .id(user.getId())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .dcNumber(user.getDcNumber())
                    .departmentId(user.getDepartmentId())
                    .refreshToken(null) // No refresh token for temporary password users
                    .message("Please change your temporary password.") // Include a message field
                    .build();
        }

        // Generate tokens
        String jwt = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();

        // Extract roles
        List<String> roles = user.getRole().getAuthorities()
                .stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Build and return the response
        AuthenticationResponse response = AuthenticationResponse.builder()
                .accessToken(jwt)
                .roles(roles)
                .email(user.getEmail())
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .dcNumber(user.getDcNumber())
                .departmentId(user.getDepartmentId())
                .message("User Authenticated Successfully")
                .refreshToken(refreshToken)
                .tokenType(TokenType.BEARER.name())
                .build();

        log.info("Generated AuthenticationResponse for user: {}", response);
        return response;
    }

    public User getUserById(Long id) {
        String cacheKey = "user:" + id;

        // Check if the user is in the cache
        User user = (User) redisTemplate.opsForValue().get(cacheKey);
        if (user != null) {
            return user; // Return cached user
        }

        // User not found in cache, retrieve from database
        user = userRepository.findById(id).orElse(null);
        if (user != null) {
            // Store the user in cache with an optional expiration time
            redisTemplate.opsForValue().set(cacheKey, user, 1, TimeUnit.HOURS); // Cache for 1 hour
        }

        return user; // Return the user, either from cache or database
    }

    @Transactional
    public User updateUser(Long userId, UserUpdateRequest userUpdateRequest) {
        // Fetch the existing user
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Update user fields
        copyNonNullProperties(userUpdateRequest, existingUser);

        // Save the updated user
        User updatedUser = userRepository.save(existingUser);

        // Invalidate the cache for this user
        String cacheKey = "user:" + updatedUser.getId();
        redisTemplate.delete(cacheKey);

        return updatedUser;
    }

    public void copyNonNullProperties(Object source, Object target) {
        BeanWrapper src = new BeanWrapperImpl(source);
        Set<String> ignoreSet = new HashSet<>();

        for (java.beans.PropertyDescriptor pd : src.getPropertyDescriptors()) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                ignoreSet.add(pd.getName());
            }
        }

        // Add fields to ignore regardless of their value
        ignoreSet.add("roles");

        BeanUtils.copyProperties(source, target, ignoreSet.toArray(new String[0]));
    }

    public Claims validateToken(String token){
        return jwtService.validateToken(token);
    }

    public void changePassword(String email, String currentPassword, String newPassword) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // Validate the current password
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                throw new IllegalArgumentException("Invalid current password");
            }

            // Hash the new password
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setTemporaryPassword(false);
            userRepository.save(user);

            // Invalidate the cache for this user
            String cacheKey = "user:" + user.getId();
            redisTemplate.delete(cacheKey);
        } else {
            throw new IllegalArgumentException("User not found with the provided email");
        }
    }


}
