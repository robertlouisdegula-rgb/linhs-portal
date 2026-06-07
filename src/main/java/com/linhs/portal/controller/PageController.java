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
                default:
                    model.addAttribute("error", "Role mapping context unresolved.");
                    return "login";
            }
        } else {
            model.addAttribute("error", "Invalid institutional username or password credential profile.");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
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
    public String showTeacherPortal(HttpSession session) {
        User user = (User) session.getAttribute("user");
        
        // 1. If not authenticated, redirect straight to the login page
        if (user == null) {
            return "redirect:/login";
        }

        // 2. If already authenticated, bypass login and go directly to their respective dashboard
        String role = user.getRoleName() != null ? user.getRoleName().toUpperCase() : "";
        switch (role) {
            case "ADMIN_PRINCIPAL":
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
            default:
                return "redirect:/login";
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

    @GetMapping("/admin-dashboard")
    public String showAdminDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN_PRINCIPAL".equalsIgnoreCase(user.getRoleName()))
            return "redirect:/login";

        model.addAttribute("username", user.getUsername());
        model.addAttribute("advisers", userRepository.findByRoleName("ADVISER"));
        model.addAttribute("allStudents", studentRepository.findAll());
        return "admin-dashboard";
    }

    @GetMapping("/adviser-dashboard")
    public String showAdviserDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADVISER".equalsIgnoreCase(user.getRoleName()))
            return "redirect:/login";

        model.addAttribute("username", user.getUsername());
        model.addAttribute("adviserSection",
                user.getAssignedSection() != null ? user.getAssignedSection() : "Not Assigned");

        if (user.getAssignedSection() != null) {
            List<Student> sectionStudents = studentRepository.findBySection(user.getAssignedSection());
            model.addAttribute("students", sectionStudents);
        } else {
            model.addAttribute("students", new ArrayList<Student>());
        }
        return "adviser-dashboard";
    }

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

    @GetMapping("/clinic-dashboard")
    public String showClinicDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"NURSE".equalsIgnoreCase(user.getRoleName()))
            return "redirect:/login";

        model.addAttribute("username", user.getUsername());
        model.addAttribute("logs", clinicLogRepository.findAll());
        return "clinic-dashboard";
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