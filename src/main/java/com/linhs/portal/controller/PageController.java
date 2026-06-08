package com.linhs.portal.controller;

// Core Java & Spring Imports
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Application Model Imports
import com.linhs.portal.model.Announcement;
import com.linhs.portal.model.BorrowRecord;
import com.linhs.portal.model.ClinicLog;
import com.linhs.portal.model.DocumentRequest;
import com.linhs.portal.model.FacilityLiability;
import com.linhs.portal.model.Gallery;
import com.linhs.portal.model.GuidanceRecord;
import com.linhs.portal.model.LibraryBorrowRecord;
import com.linhs.portal.model.ResourceHub;
import com.linhs.portal.model.SportsEquipment;
import com.linhs.portal.model.Student;
import com.linhs.portal.model.StudentGrade;
import com.linhs.portal.model.Subject;
import com.linhs.portal.model.User;

// Application Repository & Service Imports
import com.linhs.portal.repository.AnnouncementRepository;
import com.linhs.portal.repository.BorrowRecordRepository;
import com.linhs.portal.repository.ClinicLogRepository;
import com.linhs.portal.repository.DocumentRequestRepository;
import com.linhs.portal.repository.FacilityLiabilityRepository;
import com.linhs.portal.repository.GalleryRepository;
import com.linhs.portal.repository.GuidanceRecordRepository;
import com.linhs.portal.repository.LibraryBorrowRecordRepository;
import com.linhs.portal.repository.ResourceHubRepository;
import com.linhs.portal.repository.SportsEquipmentRepository;
import com.linhs.portal.repository.StudentGradeRepository;
import com.linhs.portal.repository.StudentRepository;
import com.linhs.portal.repository.SubjectRepository;
import com.linhs.portal.repository.UserRepository;
import com.linhs.portal.service.AuthService;

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

    // Single Constructor for Dependency Injection (No @Autowired needed)
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

    @GetMapping("/")
    public String showLandingPage(Model model) {
        model.addAttribute("announcements", announcementRepository.findAll());
        model.addAttribute("galleryItems", galleryRepository.findAll());
        return "index";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {
        Optional<User> userOpt = authService.authenticate(username, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("user", user);

            String role = user.getRoleName() != null ? user.getRoleName().toUpperCase() : "";
            switch (role) {
                case "ADMIN_PRINCIPAL":
                case "ADMIN":
                    return "redirect:/admin-dashboard";
                case "ADVISER":
                    return "redirect:/adviser-dashboard";
                case "REGISTRAR":
                    return "redirect:/registrar-dashboard";
                case "FACILITIES_ADMIN":
                    return "redirect:/custodian-dashboard";
                case "SPORTS_ADMIN":
                    return "redirect:/sports-dashboard";
                case "GUIDANCE_COUNSELOR":
                    return "redirect:/guidance-dashboard";
                case "NURSE":
                    return "redirect:/clinic-dashboard";
                case "LIBRARIAN":
                    return "redirect:/library-dashboard";
                case "LAB_ADMIN": 
                    return "redirect:/lab-dashboard";
                default:
                    model.addAttribute("error", "Role mapping context unresolved.");
                    return "login";
            }
        } else {
            model.addAttribute("error", "Invalid institutional username or password credential profile.");
            return "login";
        }
    }

    @GetMapping({"/logout", "/signout"})
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("logoutMessage", "You have been successfully signed out.");
        return "redirect:/login";
    }

    // ===== PUBLIC PAGES =====
    @GetMapping("/about")
    public String showAboutPage() {
        return "about";
    }

    @GetMapping("/announcements")
    public String showAnnouncementsPage(Model model) {
        List<Announcement> announcements = announcementRepository.findAll();
        model.addAttribute("announcements", announcements);
        return "announcements";
    }

    @GetMapping("/resources")
    public String showResourcesPage(Model model) {
        List<ResourceHub> resources = resourceHubRepository.findAll();
        model.addAttribute("resources", resources);
        return "resources";
    }

    @GetMapping("/gallery")
    public String showGalleryPage(Model model) {
        List<Gallery> galleryItems = galleryRepository.findAll();
        model.addAttribute("galleryItems", galleryItems);
        return "gallery";
    }

    @GetMapping("/teacher-portal")
    public String showTeacherPortal(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/login";
        }

        String role = user.getRoleName() != null ? user.getRoleName().toUpperCase() : "";
        switch (role) {
                case "ADMIN_PRINCIPAL":
                case "ADMIN":
                    return "redirect:/admin-dashboard";
                case "ADVISER":
                    return "redirect:/adviser-dashboard";
                case "REGISTRAR":
                    return "redirect:/registrar-dashboard";
                case "FACILITIES_ADMIN":
                    return "redirect:/custodian-dashboard";
                case "SPORTS_ADMIN":
                    return "redirect:/sports-dashboard";
                case "GUIDANCE_COUNSELOR":
                    return "redirect:/guidance-dashboard";
                case "NURSE":
                    return "redirect:/clinic-dashboard";
                case "LIBRARIAN":
                    return "redirect:/library-dashboard";
                case "LAB_ADMIN": 
                    return "redirect:/lab-dashboard";
                default:
                    model.addAttribute("error", "Role mapping context unresolved.");
                    return "login";
            }
    }

    // ===== CLEARANCE AND REQUESTS =====
    @GetMapping("/clearance/lookup")
    public String showClearanceLookup() {
        return "clearance-status";
    }

    @GetMapping("/requests")
    public String showRequestsPage() {
        return "request-doc";
    }

    @GetMapping("/clearance-tracker")
    public String showClearanceTrackerPage() {
        return "clearance-status";
    }

    @GetMapping("/clearance/track")
    public String trackClearanceStatus(@RequestParam("lrn") String lrn, Model model) {
        @SuppressWarnings("null")
        Optional<Student> studentOpt = studentRepository.findById(lrn);
        if (studentOpt.isPresent()) {
            model.addAttribute("student", studentOpt.get());
        } else {
            model.addAttribute("error", "No mapping found matching LRN context parameter records.");
        }
        return "clearance-status";
    }

    // =========================================================
    // --- MAIN ADMIN DASHBOARD & FEATURES ---
    // =========================================================
    @GetMapping({"/admin/dashboard", "/admin-dashboard"})
    public String showAdminDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("adminUser", user);

        List<User> allUsers = userRepository.findAll();
        List<User> staffUsers = new ArrayList<>();
        for (User u : allUsers) {
            if (!"ADVISER".equalsIgnoreCase(u.getRoleName())) {
                staffUsers.add(u);
            }
        }
        model.addAttribute("staffAccounts", staffUsers);
        model.addAttribute("adviserAccounts", userRepository.findByRoleName("ADVISER"));

        // CONNECTED DATA MODELS FOR THE PUBLIC PORTAL EDITOR
        model.addAttribute("resources", resourceHubRepository.findAll());
        model.addAttribute("announcements", announcementRepository.findAll());
        model.addAttribute("galleryItems", galleryRepository.findAll());

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
            user.setEmail(email.trim().toLowerCase());
            if (password != null && !password.trim().isEmpty()) {
                user.setPassword(authService.encodePassword(password));
            }
            userRepository.save(user);
        }
        return "redirect:/admin-dashboard?success=Account+Updated";
    }

    // FIXED: Correct Adviser Registration routing securely
    @PostMapping("/admin/adviser/create")
    public String createAdviserAccount(@RequestParam("name") String name,
                                    @RequestParam("section") String section,
                                    @RequestParam("email") String email,
                                    @RequestParam("password") String password,
                                    RedirectAttributes redirectAttributes) {
        try {
            User newAdviser = new User();
            newAdviser.setName(name);
            newAdviser.setAssignedSection(section);
            newAdviser.setEmail(email);
            newAdviser.setPassword(password); // Will be encrypted by AuthService
            newAdviser.setRoleName("ADVISER");

            User savedUser = authService.registerUser(newAdviser);

            if (savedUser == null) {
                return "redirect:/admin-dashboard?tab=1&error=Email+address+is+already+in+use.";
            }

            return "redirect:/admin-dashboard?tab=1&success=Adviser+Account+Successfully+Created.";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin-dashboard?tab=1&error=Failed+to+create+adviser+account.";
        }
    }

    @PostMapping("/admin/adviser/delete")
    public String deleteAdviserAccount(@RequestParam("id") Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin-dashboard?tab=1&success=Adviser+Deleted";
    }

    // FIXED: File paths correctly updating FileUrl & ImageUrl parameters
    @PostMapping("/admin/cms/update")
    public String processCmsUpdate(@RequestParam("type") String type,
                                @RequestParam(value = "title", required = false) String title,
                                @RequestParam(value = "content", required = false) String content,
                                @RequestParam(value = "caption", required = false) String caption,
                                @RequestParam(value = "fileAttachment", required = false) MultipartFile fileAttachment) {
        try {
            String savedPath = "";
            
            if (fileAttachment != null && !fileAttachment.isEmpty()) {
                String subFolder = "gallery".equalsIgnoreCase(type) ? "gallery/" : "uploads/";
                String filename = System.currentTimeMillis() + "_" + fileAttachment.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
                String uploadDir = "src/main/resources/static/uploads/" + subFolder;
                
                java.io.File dir = new java.io.File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                
                java.nio.file.Path path = java.nio.file.Paths.get(uploadDir + filename);
                java.nio.file.Files.write(path, fileAttachment.getBytes());
                
                savedPath = "/uploads/" + subFolder + filename;
            }

            if ("announcement".equalsIgnoreCase(type)) {
                Announcement ann = new Announcement();
                ann.setTitle(title);
                ann.setContent(content);
                announcementRepository.save(ann);
            } else if ("resource".equalsIgnoreCase(type)) {
                ResourceHub res = new ResourceHub();
                res.setTitle(title);
                res.setFileUrl(savedPath); 
                resourceHubRepository.save(res);
            } else if ("gallery".equalsIgnoreCase(type)) {
                Gallery img = new Gallery();
                img.setCaption(caption);
                img.setImageUrl(savedPath); 
                galleryRepository.save(img);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin-dashboard?tab=2&error=File+Upload+Failed";
        }
        
        return "redirect:/admin-dashboard?tab=2&success=CMS+Section+Deployed";
    }

    // ==========================================
    // RESOURCE HUB: FILE UPLOAD AND DELETE LOGIC
    // ==========================================

    @PostMapping("/admin/resource/add")
    public String addResourceUpload(@RequestParam("title") String title,
                                    @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "redirect:/admin-dashboard?tab=2&error=Please+select+a+valid+file+to+upload";
        }
        try {
            String uploadDir = "src/main/resources/static/uploads/resources/";
            java.io.File dir = new java.io.File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalFileName = file.getOriginalFilename();
            String cleanFileName = System.currentTimeMillis() + "_" + originalFileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

            java.nio.file.Path path = java.nio.file.Paths.get(uploadDir + cleanFileName);
            java.nio.file.Files.copy(file.getInputStream(), path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            ResourceHub resource = new ResourceHub();
            resource.setTitle(title);
            resource.setFileUrl("/uploads/resources/" + cleanFileName);
            resourceHubRepository.save(resource);

            return "redirect:/admin-dashboard?tab=2&success=Resource+File+Uploaded+Successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin-dashboard?tab=2&error=Failed+to+upload+resource+file";
        }
    }

    @PostMapping("/admin/resource/delete")
    public String deleteResource(@RequestParam("id") Long id) {
        try {
            resourceHubRepository.deleteById(id);
            return "redirect:/admin-dashboard?tab=2&success=Resource+Deleted+Successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin-dashboard?tab=2&error=Failed+to+delete+resource";
        }
    }

    // =========================================================
    // --- ADVISER DASHBOARD & FEATURES ---
    // =========================================================
    @GetMapping({"/adviser/dashboard", "/adviser-dashboard"})
    public String showAdviserDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADVISER".equalsIgnoreCase(user.getRoleName())) {
            return "redirect:/login";
        }

        model.addAttribute("adviser", user);
        String section = user.getAssignedSection() != null ? user.getAssignedSection() : "Not Assigned";
        
        model.addAttribute("students", studentRepository.findBySection(section));
        model.addAttribute("subjects", subjectRepository.findBySection(section));
        model.addAttribute("allGrades", studentGradeRepository.findAll());
        
        return "adviser-dashboard";
    }

    @PostMapping("/adviser/student/add")
    public String addStudentToSection(@RequestParam("name") String name,
                                    @RequestParam("lrn") String lrn,
                                    HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");
        String section = (loggedInUser != null && loggedInUser.getAssignedSection() != null) 
                        ? loggedInUser.getAssignedSection() : "Not Assigned";

        Student student = new Student(lrn, name, section);
        studentRepository.save(student);
        return "redirect:/adviser-dashboard?tab=students&success=Student+Registered";
    }

    @PostMapping("/adviser/student/delete")
    public String deleteStudentFromSection(@RequestParam("lrn") String lrn) {
        studentRepository.deleteById(lrn);
        return "redirect:/adviser-dashboard?tab=students&success=Student+Removed";
    }

    @PostMapping("/adviser/subject/add")
    public String addSubjectToSection(@RequestParam("name") String name,
                                    HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");
        String section = (loggedInUser != null && loggedInUser.getAssignedSection() != null) 
                        ? loggedInUser.getAssignedSection() : "Not Assigned";

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
                    
                    currentGrade.setGrade(gradeValue);
                    currentGrade.setRemarks(currentGrade.getFinalGrade() != null && currentGrade.getFinalGrade() >= 75.0 ? "PASSED" : "FAILED");
                    studentGradeRepository.save(currentGrade);
                }
            }
        }
        return "redirect:/adviser-dashboard?tab=grading&success=Grades+Saved";
    }

    // =========================================================
    // --- OTHER DASHBOARDS ---
    // =========================================================

    @GetMapping("/request-document")
    public String showRequestDocumentForm(Model model) {
        return "request-doc";
    }

    @PostMapping("/request-document/submit")
    public String handleDocumentRequestSubmission(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("contactNumber") String contactNumber,
            @RequestParam("academicYear") String academicYear,
            @RequestParam("gradeSection") String gradeSection,
            @RequestParam("documentType") String documentType,
            @RequestParam("purpose") String purpose,
            Model model) {

        try {
            DocumentRequest newRequest = new DocumentRequest();
            newRequest.setFirstName(firstName);
            newRequest.setLastName(lastName);
            newRequest.setContactNumber(contactNumber);
            newRequest.setAcademicYear(academicYear);
            newRequest.setGradeSection(gradeSection);
            newRequest.setDocumentType(documentType);
            newRequest.setPurpose(purpose);
            newRequest.setStatus("PENDING");
            newRequest.setRequestedAt(LocalDateTime.now());
            documentRequestRepository.save(newRequest);

            return "redirect:/registrar-dashboard";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "System error processing transaction parameters: " + e.getMessage());
            return "request-doc";
        }
    }

    @GetMapping("/registrar-dashboard")
    public String showRegistrarDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"REGISTRAR".equalsIgnoreCase(user.getRoleName()))
            return "redirect:/login";

        model.addAttribute("username", user.getUsername());
        model.addAttribute("requests", documentRequestRepository.findAll());
        return "registrar-dashboard";
    }

    @GetMapping("/custodian-dashboard")
    public String showCustodianDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"FACILITIES_ADMIN".equalsIgnoreCase(user.getRoleName()))
            return "redirect:/login";

        model.addAttribute("username", user.getUsername());
        model.addAttribute("borrowRecords", borrowRecordRepository.findAll());
        model.addAttribute("liabilities", facilityLiabilityRepository.findAll());
        return "custodian-dashboard";
    }
    
    @GetMapping("/lab-dashboard")
    public String showLabDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        
        if (user == null || !"LAB_ADMIN".equalsIgnoreCase(user.getRoleName())) {
            return "redirect:/login";
        }

        model.addAttribute("adminName", user.getName());
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("activeBorrows", borrowRecordRepository.findAll()); 

        return "lab-dashboard";
    }
    
    @GetMapping("/sports-dashboard")
    public String showSportsDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"SPORTS_ADMIN".equalsIgnoreCase(user.getRoleName()))
            return "redirect:/login";

        model.addAttribute("username", user.getUsername());
        model.addAttribute("equipments", sportsEquipmentRepository.findAll());
        return "sports-dashboard";
    }

    @GetMapping("/guidance-dashboard")
    public String showGuidanceDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"GUIDANCE_COUNSELOR".equalsIgnoreCase(user.getRoleName()))
            return "redirect:/login";

        model.addAttribute("username", user.getUsername());
        model.addAttribute("records", guidanceRecordRepository.findAll());
        return "guidance-dashboard";
    }

    // FIXED: Adjusted attribute naming keys to perfectly match Thymeleaf HTML context templates
    @GetMapping("/clinic-dashboard")
    public String showClinicDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"NURSE".equalsIgnoreCase(user.getRoleName()))
            return "redirect:/login";

        model.addAttribute("username", user.getUsername());
        model.addAttribute("students", studentRepository.findAll()); // Populates the dropdown menu form context
        model.addAttribute("clinicLogs", clinicLogRepository.findAll()); // Matches th:each="log : ${clinicLogs}"
        return "nurse-dashboard"; // Ensure this matches your template filename (e.g., nurse-dashboard.html)
    }

    @PostMapping("/clinic/log/add")
    public String addClinicLog(@RequestParam("studentLrn") String studentLrn,
                               @RequestParam("studentName") String studentName,
                               @RequestParam("reason") String reason,
                               @RequestParam("actionTaken") String actionTaken) {
        try {
            ClinicLog log = new ClinicLog();
            log.setStudentLrn(studentLrn);
            log.setStudentName(studentName);
            log.setReason(reason);
            log.setActionTaken(actionTaken);
            log.setVisitDate(LocalDateTime.now());
            
            clinicLogRepository.save(log);
            return "redirect:/clinic-dashboard?success=Student+Clinic+Record+Saved";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/clinic-dashboard?error=Failed+to+save+clinic+record";
        }
    }

    @GetMapping("/library-dashboard")
    public String showLibraryDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"LIBRARIAN".equalsIgnoreCase(user.getRoleName()))
            return "redirect:/login";

        model.addAttribute("username", user.getUsername());
        model.addAttribute("libraryRecords", libraryBorrowRecordRepository.findAll());
        return "library-dashboard";
    }

    @GetMapping("/student-liabilities-details")
    public String showStudentLiabilitiesDetails(@RequestParam("lrn") String lrn, Model model) {
        @SuppressWarnings("null")
        Optional<Student> studentOpt = studentRepository.findById(lrn);
        if (studentOpt.isEmpty()) {
            model.addAttribute("error", "Student record tracking context missing.");
            return "error";
        }

        Student student = studentOpt.get();
        LiabilityDetailsRow details = new LiabilityDetailsRow(student.getName(), student.getLrn(), "ACTIVE");

        List<BorrowRecord> propertyRecords = borrowRecordRepository.findByStudentLrnAndStatus(lrn, "BORROWED");
        for (BorrowRecord br : propertyRecords) {
            details.addOpenItem(
                    new OpenLiabilityItem("Property Custodian", br.getItemName(), br.getBorrowedAt(), "UNRETURNED"));
        }

        List<SportsEquipment> sportsRecords = sportsEquipmentRepository.findByStudentLrnAndStatus(lrn, "BORROWED");
        for (SportsEquipment se : sportsRecords) {
            details.addOpenItem(new OpenLiabilityItem("Sports & Athletics",
                    se.getEquipmentName() + " (Qty: " + se.getQuantity() + ")", 
                    se.getBorrowDate(), 
                    "UNRETURNED"));
        }

        List<GuidanceRecord> guidanceRecords = guidanceRecordRepository.findByStudentLrn(lrn);
        for (GuidanceRecord gr : guidanceRecords) {
            if (gr.getActionTaken() == null || gr.getActionTaken().trim().isEmpty()) {
                details.addOpenItem(new OpenLiabilityItem("Guidance Office", gr.getInfractionDescription(), gr.getLogDate(),
                        "PENDING_RESOLUTION"));
            }
        }

        List<LibraryBorrowRecord> libraryRecords = libraryBorrowRecordRepository.findByStudentLrnAndStatus(lrn,
                "BORROWED");
        for (LibraryBorrowRecord lbr : libraryRecords) {
            details.addOpenItem(new OpenLiabilityItem("School Library", "Book: " + lbr.getBookTitle(),
                    lbr.getBorrowDate(), "OVERDUE_RETAINED"));
        }

        List<FacilityLiability> physicalRecords = facilityLiabilityRepository.findByStudentLrnAndStatus(lrn,
                "UNRESOLVED");
        for (FacilityLiability fl : physicalRecords) {
            details.addOpenItem(new OpenLiabilityItem("Facilities Damage",
                    fl.getFacilityName() + " - " + fl.getDamageDescription(), fl.getReportedDate(), "DAMAGE_UNPAID"));
        }

        model.addAttribute("details", details);
        return "student-liabilities-details";
    }

    // Helper View DTOs specific to this controller
    public static class LiabilityDetailsRow {
        private final String studentName;
        private final String lrn;
        private final String status;
        private final List<OpenLiabilityItem> openItems = new ArrayList<>();

        public LiabilityDetailsRow(String studentName, String lrn, String status) {
            this.studentName = studentName;
            this.lrn = lrn;
            this.status = status;
        }

        public String getStudentName() {
            return studentName;
        }

        public String getLrn() {
            return lrn;
        }

        public String getStatus() {
            return status;
        }

        public List<OpenLiabilityItem> getOpenItems() {
            return openItems;
        }

        public void addOpenItem(OpenLiabilityItem item) {
            if (item != null)
                openItems.add(item);
        }
    }

    public static class OpenLiabilityItem {
        private final String category;
        private final String description;
        private final LocalDateTime loggedAt;
        private final String state;

        public OpenLiabilityItem(String category, String description, LocalDateTime loggedAt, String state) {
            this.category = category;
            this.description = description;
            this.loggedAt = loggedAt;
            this.state = state;
        }

        public String getCategory() {
            return category;
        }

        public String getDescription() {
            return description;
        }

        public LocalDateTime getLoggedAt() {
            return loggedAt;
        }

        public String getState() {
            return state;
        }
    }
}