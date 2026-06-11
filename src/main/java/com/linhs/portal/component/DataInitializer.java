package com.linhs.portal.component;

import com.linhs.portal.model.Student;
import com.linhs.portal.model.User;
import com.linhs.portal.repository.StudentRepository;
import com.linhs.portal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Optional;

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

    /**
     * Clears adviser test data that may cause collisions.
     *
     * WARNING: This is meant for dev/test only.
     */
    private void clearAdviserTestData() {
        // Remove demo students that were seeded by this initializer.
        // These are only students (not subjects/grades) so they won't affect admin-created subjects.
        studentRepository.findAll()
                .stream()
                .filter(s -> {
                    String sec = s.getSection();
                    return sec != null && (sec.equalsIgnoreCase("Grade 12 - Azurite") || sec.equalsIgnoreCase("Grade 12-Azurite"));
                })
                .forEach(s -> studentRepository.deleteById(s.getLrn()));
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🌱 Starting revised database seeding sequence...");

        // Clear any dev/test adviser collisions before reseeding.
        clearAdviserTestData();

        // 1. Seed Initial Mock Students
        String testLrn = "101234567890";
        if (!studentRepository.existsById(testLrn)) {
            Student student = new Student();
            student.setLrn(testLrn);
            student.setName("John Denver Robles");
            student.setSection("Grade 12 - Azurite");
            student.setAdviserClearance("PENDING");
            student.setLabClearance("PENDING");
            student.setSportsClearance("PENDING");
            student.setGuidanceClearance("PENDING");
            studentRepository.save(student);
            System.out.println("✅ Test Student [John Denver Robles] seeded successfully.");
        }

        // 2. Define All Core Department Portal Email Layouts
        Map<String, String> clearanceRoles = new LinkedHashMap<>();
        clearanceRoles.put("ADMIN_PRINCIPAL", "admin@linhs.edu.ph");
        clearanceRoles.put("ADVISER", "adviser@linhs.edu.ph");
        clearanceRoles.put("REGISTRAR", "registrar@linhs.edu.ph");
        clearanceRoles.put("LAB_ADMIN", "lab@linhs.edu.ph");
        clearanceRoles.put("SPORTS_ADMIN", "sports@linhs.edu.ph");
        clearanceRoles.put("GUIDANCE_COUNSELOR", "guidance@linhs.edu.ph");
        clearanceRoles.put("NURSE", "clinic@linhs.edu.ph");
        clearanceRoles.put("FACILITIES_ADMIN", "facilities@linhs.edu.ph");
        clearanceRoles.put("LIBRARIAN", "library@linhs.edu.ph");

        String defaultPassword = "password123";
        // Dynamically crypt password using our active configuration context encoder
        String encryptedPassword = passwordEncoder.encode(defaultPassword);

        for (Map.Entry<String, String> entry : clearanceRoles.entrySet()) {
            String roleStr = entry.getKey();
            String email = entry.getValue();

            Optional<User> existingUserOpt = userRepository.findByEmail(email);

            if (existingUserOpt.isPresent()) {
                // IMPORTANT FIX: If the account already exists from an old run, FORCE UPDATE the password to the hashed version!
                User existingUser = existingUserOpt.get();
                existingUser.setPassword(encryptedPassword);
                userRepository.save(existingUser);
                System.out.println("🔄 Fixed/Updated password for existing account: " + email);
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
                    case "ADVISER":
                        personalName = "Sir Robert De Gula";
                        assignedSection = "Grade 12 - Azurite";
                        break;
                    case "REGISTRAR":
                        personalName = "Maam Aicel Llanes";
                        break;
                    case "LAB_ADMIN":
                        personalName = "Maam Marissa Nario";
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
                        personalName = "Maam Geraldine Pasia";
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
        System.out.println("🚀 All departmental accounts are dynamically initialized and ready to login!");
    }
}