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

        // Force Java to use the exact model entity by writing the full package path
java.util.List<com.linhs.portal.model.SportsEquipment> sportsRecords = 
    sportsEquipmentRepository.findByStudentLrnAndStatus(lrn, "BORROWED");

// Also force the loop to use the exact model entity
for (com.linhs.portal.model.SportsEquipment se : sportsRecords) {
    details.addOpenItem(new OpenLiabilityItem("Sports & Athletics",
            se.getEquipmentName() + " (Qty: " + se.getQuantity() + ")", 
            se.getBorrowDate(), 
            "UNRETURNED"));
}

        // Explicitly query the model entity package and filter out records where action has already been taken
        List<com.linhs.portal.model.GuidanceRecord> guidanceRecords = guidanceRecordRepository.findByStudentLrn(lrn);
        for (com.linhs.portal.model.GuidanceRecord gr : guidanceRecords) {
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

    public static class GuidanceRecord {
        private Long id;
        private String studentLrn;
        private String infractionDescription;
        private LocalDateTime logDate;
        private String status;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getStudentLrn() {
            return studentLrn;
        }

        public void setStudentLrn(String studentLrn) {
            this.studentLrn = studentLrn;
        }

        public String getInfractionDescription() {
            return infractionDescription;
        }

        public void setInfractionDescription(String infractionDescription) {
            this.infractionDescription = infractionDescription;
        }

        public LocalDateTime getLogDate() {
            return logDate;
        }

        public void setLogDate(LocalDateTime logDate) {
            this.logDate = logDate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class ClinicLog {
        private Long id;
        private String studentLrn;
        private String symptoms;
        private String treatment;
        private LocalDateTime visitDate;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getStudentLrn() {
            return studentLrn;
        }

        public void setStudentLrn(String studentLrn) {
            this.studentLrn = studentLrn;
        }

        public String getSymptoms() {
            return symptoms;
        }

        public void setSymptoms(String symptoms) {
            this.symptoms = symptoms;
        }

        public String getTreatment() {
            return treatment;
        }

        public void setTreatment(String treatment) {
            this.treatment = treatment;
        }

        public LocalDateTime getVisitDate() {
            return visitDate;
        }

        public void setVisitDate(LocalDateTime visitDate) {
            this.visitDate = visitDate;
        }
    }

    public static class Subject {
        private Long id;
        private String code;
        private String name;
        private String gradeLevel;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String name() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGradeLevel() {
            return gradeLevel;
        }

        public void setGradeLevel(String gradeLevel) {
            this.gradeLevel = gradeLevel;
        }
    }

    public static class StudentGrade {
        private Long id;
        private String studentLrn;
        private String subjectCode;
        private Double quarter1;
        private Double quarter2;
        private Double quarter3;
        private Double quarter4;
        private Double finalGrade;
        private String remarks;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getStudentLrn() {
            return studentLrn;
        }

        public void setStudentLrn(String studentLrn) {
            this.studentLrn = studentLrn;
        }

        public String getSubjectCode() {
            return subjectCode;
        }

        public void setSubjectCode(String subjectCode) {
            this.subjectCode = subjectCode;
        }

        public Double getQuarter1() {
            return quarter1;
        }

        public void setQuarter1(Double quarter1) {
            this.quarter1 = quarter1;
        }

        public Double getQuarter2() {
            return quarter2;
        }

        public void setQuarter2(Double quarter2) {
            this.quarter2 = quarter2;
        }

        public Double getQuarter3() {
            return quarter3;
        }

        public void setQuarter3(Double quarter3) {
            this.quarter3 = quarter3;
        }

        public Double getQuarter4() {
            return quarter4;
        }

        public void setQuarter4(Double quarter4) {
            this.quarter4 = quarter4;
        }

        public Double getFinalGrade() {
            return finalGrade;
        }

        public void setFinalGrade(Double finalGrade) {
            this.finalGrade = finalGrade;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }

    public static class ResourceHub {
        private Long id;
        private String title;
        private String description;
        private String fileUrl;
        private String uploadedBy;
        private LocalDateTime uploadedAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getUploadedBy() {
            return uploadedBy;
        }

        public void setUploadedBy(String uploadedBy) {
            this.uploadedBy = uploadedBy;
        }

        public LocalDateTime getUploadedAt() {
            return uploadedAt;
        }

        public void setUploadedAt(LocalDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
        }
    }

    public static class Gallery {
        private Long id;
        private String imageUrl;
        private String caption;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }
    }

    public static class Announcement {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime createdAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

}