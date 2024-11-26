package za.co.datacentrix.taskservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import za.co.datacentrix.taskservice.entities.Task;
import za.co.datacentrix.taskservice.entities.User;
import za.co.datacentrix.taskservice.entities.UserPreferences;
import za.co.datacentrix.taskservice.enums.Priority;
import za.co.datacentrix.taskservice.enums.Role;
import za.co.datacentrix.taskservice.enums.Status;
import za.co.datacentrix.taskservice.repository.TaskRepository;
import za.co.datacentrix.taskservice.repository.UserPreferencesRepository;
import za.co.datacentrix.taskservice.repository.UserRepository;
import com.github.javafaker.Faker;
import za.co.datacentrix.taskservice.service.impl.AuthenticationServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;


@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository; // Assuming you have this repository
    @Autowired
    private UserPreferencesRepository userPreferencesRepository; // Assuming you have this repository
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker(Locale.ENGLISH);

    @Autowired
    private AuthenticationServiceImpl authenticationServiceImpl;

    @Override
    public void run(String... args) throws Exception {
        // Create super admin user
        createSuperAdmin();

        // Load additional data
        loadUsers(10); // Load 10 random users
        loadTasks(50); // Load 50 random tasks
        loadUserPreferences(); // Load preferences for users
    }

    private void createSuperAdmin() {
        if (!userRepository.findByEmail("superadmin@datacentrix.co.za").isPresent()) {
            User superAdmin = new User();
            superAdmin.setFirstname("Super");
            superAdmin.setLastname("Admin");
            superAdmin.setEmail("superadmin@datacentrix.co.za");
            superAdmin.setPassword(passwordEncoder.encode("Password@123"));
            superAdmin.setRole(Role.ADMIN);
            superAdmin.setTemporaryPassword(false);
            superAdmin.setDepartmentId(0L);
            superAdmin.setDcNumber("DC12345789SA");

            userRepository.save(superAdmin);
            System.out.println("Default SUPER ADMIN user created.");
        } else {
            System.out.println("SUPER ADMIN user already exists.");
        }
    }

    private void loadUsers(int count) {
        List<User> users = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String firstname = faker.name().firstName();
            String lastname = faker.name().lastName();

            User user = new User();
            user.setFirstname(firstname);
            user.setLastname(lastname);

            // Create username: first letter of first name + last name
            String username = firstname.substring(0, 1).toLowerCase() + lastname.toLowerCase();
            String email = username + "@datacentrix.co.za";
            user.setEmail(email);

            user.setPassword(passwordEncoder.encode("Password@123")); // Use a default password or generate one
            user.setRole(Role.USER); // Assign a default role
            user.setTemporaryPassword(false);
            user.setDepartmentId(faker.number().randomNumber()); // Example for department ID

            // Generate initials for the name
            String nameInitials = firstname.substring(0, 1).toUpperCase() + lastname.substring(0, 1).toUpperCase();

            // Create role initials based on the user's role
            String role = user.getRole().toString(); // Assuming Role is an enum or class with a toString method
            String roleInitials = generateRoleInitials(role);

            // Call the generateDcNumber method
            String dcNumber = authenticationServiceImpl.generateDcNumber(nameInitials, roleInitials);
            user.setDcNumber(dcNumber); // Set the generated DC number

            users.add(user);
        }
        userRepository.saveAll(users);
        System.out.println(count + " random users created.");
    }

    // Method to generate role initials
    private String generateRoleInitials(String role) {
        if (role == null || role.isEmpty()) {
            return "XX"; // Default initials if role is null or empty
        }
        // Get first two characters and convert to uppercase
        return role.length() >= 2 ? role.substring(0, 2).toUpperCase() : (role + "X").toUpperCase();
    }
    private void loadTasks(int count) {
        List<Task> tasks = new ArrayList<>();
        List<User> users = userRepository.findAll(); // Get all users for task assignment
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            if (!users.isEmpty()) {
                Task task = new Task();
                task.setTitle(faker.lorem().sentence());
                task.setDescription(faker.lorem().paragraph());
                // Select a random user from the list
                User assignedUser = users.get(random.nextInt(users.size()));
                task.setAssignedTo(assignedUser); // Assign the randomly selected user
                task.setStatus(faker.options().option(Status.values())); // Assuming Status is an enum

                tasks.add(task);
            }
        }
        taskRepository.saveAll(tasks);
        System.out.println(count + " random tasks created.");
    }

    private void loadUserPreferences() {
        List<User> users = userRepository.findAll(); // Get all users for preferences
        List<UserPreferences> preferences = new ArrayList<>();

        for (User user : users) {
            UserPreferences userPreference = new UserPreferences();
            userPreference.setUser(user);
            userPreference.setEmailNotifications(faker.bool().bool());
            userPreference.setTheme(faker.options().option("Light", "Dark", "System Default"));
            userPreference.setPriority(faker.options().option(Priority.values())); // Assuming Priority is an enum
            userPreference.setPushNotifications(faker.bool().bool());
            userPreference.setPushSubscription(faker.internet().uuid()); // Example for push subscription

            preferences.add(userPreference);
        }
        userPreferencesRepository.saveAll(preferences);
        System.out.println(preferences.size() + " user preferences created.");
    }
}
