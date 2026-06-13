package com.linhs.portal.component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.linhs.portal.model.User;
import com.linhs.portal.repository.StudentRepository;
import com.linhs.portal.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(StudentRepository studentRepository,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🌱 Starting database seeding sequence...");

        // 1. FORCED CLEANUP: Explicitly find and delete the old hardcoded adviser and mock student
        String targetAdviserEmail = "adviser@linhs.edu.ph";
        Optional<User> oldAdviser = userRepository.findByEmail(targetAdviserEmail);
        if (oldAdviser.isPresent()) {
            userRepository.delete(oldAdviser.get());
            System.out.println("🗑️ Successfully purged the old hardcoded adviser account: " + targetAdviserEmail);
        }

        String testLrn = "101234567890";
        if (studentRepository.existsById(testLrn)) {
            studentRepository.deleteById(testLrn);
            System.out.println("🗑️ Successfully purged the mock student with LRN: " + testLrn);
        }


        // 2. Define Core Departmental Portal Email Layouts (ADVISER REMOVED)
        Map<String, String> clearanceRoles = new LinkedHashMap<>();
        clearanceRoles.put("ADMIN_PRINCIPAL", "admin@linhs.edu.ph");
        clearanceRoles.put("REGISTRAR", "registrar@linhs.edu.ph");
        clearanceRoles.put("LAB_ADMIN", "lab@linhs.edu.ph");
        clearanceRoles.put("SPORTS_ADMIN", "sports@linhs.edu.ph");
        clearanceRoles.put("GUIDANCE_COUNSELOR", "guidance@linhs.edu.ph");
        clearanceRoles.put("NURSE", "clinic@linhs.edu.ph");
        clearanceRoles.put("FACILITIES_ADMIN", "facilities@linhs.edu.ph");
        clearanceRoles.put("LIBRARIAN", "library@linhs.edu.ph");

        String defaultPassword = "password123";
        String encryptedPassword = passwordEncoder.encode(defaultPassword);

        for (Map.Entry<String, String> entry : clearanceRoles.entrySet()) {
            String roleStr = entry.getKey();
            String email = entry.getValue();

            Optional<User> existingUserOpt = userRepository.findByEmail(email);

            if (existingUserOpt.isPresent()) {
                // Force update the password to the hashed version if it exists
                User existingUser = existingUserOpt.get();
                existingUser.setPassword(encryptedPassword);
                userRepository.save(existingUser);
                System.out.println("🔄 Updated password for existing system account: " + email);
            } else {
                // Create brand new account
                User user = new User();
                user.setEmail(email);
                user.setPassword(encryptedPassword);
                user.setRoleName(roleStr);

                String personalName;
                String assignedSection = "None";

                switch (roleStr) {
                    case "ADMIN_PRINCIPAL":
                        personalName = "Main Admin";
                        break;
                    case "REGISTRAR":
                        personalName = "Ma'am Aicel Llanes";
                        break;
                    case "LAB_ADMIN":
                        personalName = "Ma'am Marissa Nario";
                        break;
                    case "SPORTS_ADMIN":
                        personalName = "Ghenalyn Mendoza";
                        break;
                    case "GUIDANCE_COUNSELOR":
                        personalName = "Lielanie Gonzales";
                        break;
                    case "NURSE":
                        personalName = "Sir Lawrence Leyesa";
                        break;
                    case "FACILITIES_ADMIN":
                        personalName = "Sir Marcel Olan";
                        break;
                    case "LIBRARIAN":
                        personalName = "Ma'am Geraldine Pasia";
                        break;
                    default:
                        personalName = "LINHS Staff Member";
                        break;
                }

                user.setName(personalName);
                user.setAssignedSection(assignedSection);

                userRepository.save(user);
                System.out.println("✅ Seeded new account: [" + roleStr + "] " + personalName + " -> " + email);
            }
        }
        System.out.println("🚀 Seeding sequence complete! Check Admin Portal to add fresh, clean Advisers.");
    }
}