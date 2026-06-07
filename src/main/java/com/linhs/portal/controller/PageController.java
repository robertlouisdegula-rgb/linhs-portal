package com.linhs.portal.controller;

import com.linhs.portal.model.*;
import com.linhs.portal.repository.*;
import com.linhs.portal.service.AuthService;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class PageController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final SportsEquipmentRepository sportsEquipmentRepository;
    private final GuidanceRecordRepository guidanceRecordRepository;
    private final DocumentRequestRepository documentRequestRepository;
    private final ClinicLogRepository clinicLogRepository;
    private final FacilityLiabilityRepository facilityLiabilityRepository;
    private final SubjectRepository subjectRepository;
    private final StudentGradeRepository studentGradeRepository;
    private final ResourceHubRepository resourceHubRepository;
    private final GalleryRepository galleryRepository;
    private final AnnouncementRepository announcementRepository;
    private final LibraryBorrowRecordRepository libraryBorrowRecordRepository;

    public PageController(AuthService authService,
                          UserRepository userRepository,
                          StudentRepository studentRepository,
                          BorrowRecordRepository borrowRecordRepository,
                          SportsEquipmentRepository sportsEquipmentRepository,
                          GuidanceRecordRepository guidanceRecordRepository,
                          DocumentRequestRepository documentRequestRepository,
                          ClinicLogRepository clinicLogRepository,
                          FacilityLiabilityRepository facilityLiabilityRepository,
                          SubjectRepository subjectRepository,
                          StudentGradeRepository studentGradeRepository,
                          ResourceHubRepository resourceHubRepository,
                          GalleryRepository galleryRepository,
                          AnnouncementRepository announcementRepository,
                          LibraryBorrowRecordRepository libraryBorrowRecordRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.borrowRecordRepository = borrowRecordRepository;
        this.sportsEquipmentRepository = sportsEquipmentRepository;
        this.guidanceRecordRepository = guidanceRecordRepository;
        this.documentRequestRepository = documentRequestRepository;
        this.clinicLogRepository = clinicLogRepository;
        this.facilityLiabilityRepository = facilityLiabilityRepository;
        this.subjectRepository = subjectRepository;
        this.studentGradeRepository = studentGradeRepository;
        this.resourceHubRepository = resourceHubRepository;
        this.galleryRepository = galleryRepository;
        this.announcementRepository = announcementRepository;
        this.libraryBorrowRecordRepository = libraryBorrowRecordRepository;
    }

    // =========================================================
    // --- MAIN ADMIN DASHBOARD MAPPINGS ---
    // =========================================================
    @GetMapping({"/admin/dashboard", "/admin-dashboard"})
    public String showAdminDashboard(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            loggedInUser = (User) session.getAttribute("loggedInUser");
        }
        // Fail-safe for manual testing
        if (loggedInUser == null) {
            loggedInUser = new User();
            loggedInUser.setName("Main System Administrator");
            loggedInUser.setEmail("admin@linhs.edu.ph");
            loggedInUser.setRoleName("ADMIN");
        }
        model.addAttribute("adminUser", loggedInUser);

        // Tab 1: Staff accounts (Excluding advisers)
        List<User> allUsers = userRepository.findAll();
        List<User> staffUsers = new ArrayList<>();
        for (User u : allUsers) {
            if (!"ADVISER".equalsIgnoreCase(u.getRoleName())) {
                staffUsers.add(u);
            }
        }
        model.addAttribute("staffAccounts", staffUsers);

        // Tab 2: Adviser accounts
        List<User> advisers = userRepository.findByRoleName("ADVISER");
        model.addAttribute("adviserAccounts", advisers);

        return "admin-dashboard";
    }

    @PostMapping("/admin/account/edit")
    public String editStaffAccount(@RequestParam("id") Long id,
                                   @RequestParam("name") String name,
                                   @RequestParam("email") String email,
                                   @RequestParam("password") String password) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setName(name);
            user.setEmail(email);
            if (password != null && !password.trim().isEmpty()) {
                user.setPassword(password);
            }
            userRepository.save(user);
        }
        return "redirect:/admin-dashboard?success=Account+Updated";
    }

    @PostMapping("/admin/adviser/create")
    public String createAdviserAccount(@RequestParam("name") String name,
                                       @RequestParam("section") String section,
                                       @RequestParam("email") String email,
                                       @RequestParam("password") String password) {
        User adviser = new User();
        adviser.setName(name);
        adviser.setAssignedSection(section);
        adviser.setEmail(email);
        adviser.setPassword(password);
        adviser.setRoleName("ADVISER");
        userRepository.save(adviser);
        return "redirect:/admin-dashboard?tab=1&success=Adviser+Created";
    }

    @PostMapping("/admin/adviser/delete")
    public String deleteAdviserAccount(@RequestParam("id") Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin-dashboard?tab=1&success=Adviser+Deleted";
    }

    // Dummy placeholders for CMS Tab requests (Tab 3)
    @PostMapping("/admin/cms/update")
    public String processCmsUpdate(@RequestParam("type") String type, @RequestParam Map<String, String> allParams) {
        return "redirect:/admin-dashboard?tab=2&success=CMS+Section+Deployed";
    }


    // =========================================================
    // --- ADVISER DASHBOARD MAPPINGS ---
    // =========================================================
    @GetMapping({"/adviser/dashboard", "/adviser-dashboard"})
    public String showAdviserDashboard(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            loggedInUser = (User) session.getAttribute("loggedInUser");
        }
        
        // Match user requested initial default credentials if none logged in
        if (loggedInUser == null || !"ADVISER".equalsIgnoreCase(loggedInUser.getRoleName())) {
            loggedInUser = new User();
            loggedInUser.setName("Robert Louis De Gula");
            loggedInUser.setAssignedSection("Grade 12 - Azurite");
            loggedInUser.setRoleName("ADVISER");
        }
        
        model.addAttribute("adviser", loggedInUser);
        String section = loggedInUser.getAssignedSection();

        // Tab 1: Students in this section
        List<Student> students = studentRepository.findBySection(section);
        model.addAttribute("students", students);

        // Tab 3: Subjects assigned to this section
        List<Subject> subjects = subjectRepository.findBySection(section);
        model.addAttribute("subjects", subjects);

        // Tab 2: Map grades to easily render into the modal matrix
        List<StudentGrade> allGrades = studentGradeRepository.findAll();
        model.addAttribute("allGrades", allGrades);

        return "adviser-dashboard";
    }

    @PostMapping("/adviser/student/add")
    public String addStudentToSection(@RequestParam("name") String name,
                                      @RequestParam("lrn") String lrn,
                                      HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) loggedInUser = (User) session.getAttribute("loggedInUser");
        String section = (loggedInUser != null) ? loggedInUser.getAssignedSection() : "Grade 12 - Azurite";

        Student student = new Student(lrn, name, section);
        studentRepository.save(student);
        return "redirect:/adviser-dashboard?tab=students&success=Student+Registered";
    }

    @PostMapping("/adviser/subject/add")
    public String addSubjectToSection(@RequestParam("name") String name,
                                      HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) loggedInUser = (User) session.getAttribute("loggedInUser");
        String section = (loggedInUser != null) ? loggedInUser.getAssignedSection() : "Grade 12 - Azurite";

        Subject subj = new Subject();
        subj.setName(name);
        subj.setSection(section);
        subjectRepository.save(subj);
        return "redirect:/adviser-dashboard?tab=subjects&success=Subject+Added";
    }

    @PostMapping("/adviser/subject/delete")
    public String deleteSubjectFromSection(@RequestParam("id") Long id) {
        subjectRepository.deleteById(id);
        return "redirect:/adviser-dashboard?tab=subjects&success=Subject+Removed";
    }

    @PostMapping("/adviser/grade/save")
    public String saveStudentGrades(@RequestParam("studentLrn") String studentLrn,
                                    @RequestParam Map<String, String> params) {
        for (String key : params.keySet()) {
            if (key.startsWith("subject_")) {
                Long subjectId = Long.parseLong(key.replace("subject_", ""));
                String gradeValue = params.get(key);
                
                Optional<Subject> subjOpt = subjectRepository.findById(subjectId);
                if (subjOpt.isPresent()) {
                    Subject subj = subjOpt.get();
                    
                    // Look for existing grade entry to overwrite or create new
                    List<StudentGrade> existing = studentGradeRepository.findByStudentLrn(studentLrn);
                    StudentGrade currentGrade = null;
                    for (StudentGrade g : existing) {
                        if (g.getSubjectId().equals(subjectId)) {
                            currentGrade = g;
                            break;
                        }
                    }
                    
                    if (currentGrade == null) {
                        currentGrade = new StudentGrade();
                        currentGrade.setStudentLrn(studentLrn);
                        currentGrade.setSubjectId(subjectId);
                        currentGrade.setSubjectName(subj.getName());
                    }
                    
                    currentGrade.setGrade(gradeValue); // Safely sets gradeValue string & internal parsing
                    currentGrade.setRemarks(currentGrade.getFinalGrade() != null && currentGrade.getFinalGrade() >= 75.0 ? "PASSED" : "FAILED");
                    studentGradeRepository.save(currentGrade);
                }
            }
        }
        return "redirect:/adviser-dashboard?tab=grading&success=Grades+Saved";
    }
}