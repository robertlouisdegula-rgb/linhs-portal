package com.linhs.portal.controller;

// Core Java & Spring Imports
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// Application Model Imports
import com.linhs.portal.model.Announcement;
import com.linhs.portal.model.BorrowRecord;
import com.linhs.portal.model.ClinicLog;
import com.linhs.portal.model.DocumentRequest;
import com.linhs.portal.model.FacilityLog;
import com.linhs.portal.model.Gallery;
import com.linhs.portal.model.GuidanceLog;
import com.linhs.portal.model.LibraryBorrowRecord;
import com.linhs.portal.model.ResourceHub;
import com.linhs.portal.model.SportsEquipment;
import com.linhs.portal.model.Student;
import com.linhs.portal.model.StudentGrade;
import com.linhs.portal.model.Subject;
import com.linhs.portal.model.User;

// Application Repository Imports
import com.linhs.portal.repository.AnnouncementRepository;
import com.linhs.portal.repository.BorrowRecordRepository;
import com.linhs.portal.repository.ClinicLogRepository;
import com.linhs.portal.repository.DocumentRequestRepository;
import com.linhs.portal.repository.FacilityLogRepository;
import com.linhs.portal.repository.GalleryRepository;
import com.linhs.portal.repository.GuidanceLogRepository;
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
    private final GuidanceLogRepository guidanceLogRepository;
    private final DocumentRequestRepository documentRequestRepository;
    private final ClinicLogRepository clinicLogRepository;
    private final FacilityLogRepository facilityLogRepository;
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
            GuidanceLogRepository guidanceLogRepository,
            DocumentRequestRepository documentRequestRepository,
            ClinicLogRepository clinicLogRepository,
            FacilityLogRepository facilityLogRepository,
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
        this.guidanceLogRepository = guidanceLogRepository;
        this.documentRequestRepository = documentRequestRepository;
        this.clinicLogRepository = clinicLogRepository;
        this.facilityLogRepository = facilityLogRepository;
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

            // FIX: Gracefully handle whitespace and database formatting variations
            String role = user.getRoleName() != null ? user.getRoleName().trim().toUpperCase() : "";

            if (role.contains("ADMIN") && !role.contains("FACILITIES") && !role.contains("SPORTS")) {
                return "redirect:/admin-dashboard";
            } else if (role.contains("ADVISER") || role.contains("TEACHER")) {
                return "redirect:/teacher-portal";
            } else if (role.contains("FACILITIES")) {
                return "redirect:/facilities-dashboard";
            } else if (role.contains("GUIDANCE")) {
                return "redirect:/guidance-dashboard";
            } else if (role.contains("NURSE") || role.contains("CLINIC")) {
                return "redirect:/clinic-dashboard";
            } else if (role.contains("REGISTRAR")) {
                return "redirect:/registrar-dashboard";
            } else if (role.contains("LABORATORY") || role.contains("LAB")) {
                return "redirect:/lab-dashboard";
            } else if (role.contains("SPORTS")) {
                return "redirect:/sports-dashboard";
            } else if (role.contains("LIBRARY")) {
                return "redirect:/library-dashboard";
            }

            // Fallback for an unmapped or misspelled role
            System.out.println("Warning: Unrecognized role encountered: " + role);
            return "redirect:/";
        } else {
            model.addAttribute("error", "Invalid username or password credential profile.");
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
    public String showAbout() {
        return "about";
    }

    @GetMapping("/announcements")
    public String showAnnouncements(Model model) {
        model.addAttribute("announcements", announcementRepository.findAll());
        return "announcements";
    }

    @GetMapping("/resources")
    public String showResources(Model model) {
        model.addAttribute("resources", resourceHubRepository.findAll());
        return "resources";
    }

    @GetMapping("/gallery")
    public String showGallery(Model model) {
        model.addAttribute("galleryItems", galleryRepository.findAll());
        return "gallery";
    }

    // =========================================================
    // --- ADVISER / TEACHER PORTAL ---
    // =========================================================

    @GetMapping("/teacher-portal")
    public String showTeacherPortal(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        
        // FIX: Match the flexible login role checking
        if (user == null || user.getRoleName() == null || 
            (!user.getRoleName().trim().toUpperCase().contains("ADVISER") && 
             !user.getRoleName().trim().toUpperCase().contains("TEACHER"))) {
            return "redirect:/login";
        }

        model.addAttribute("username", user.getName());
        model.addAttribute("assignedSection", user.getAssignedSection() != null ? user.getAssignedSection() : "Unassigned");

        // FIX Error 500: Avoid querying null sections which crashes the repo
        List<Student> assignedStudents = new ArrayList<>();
        if (user.getAssignedSection() != null) {
            assignedStudents = studentRepository.findBySection(user.getAssignedSection());
        }
        model.addAttribute("assignedStudents", assignedStudents);

        List<Subject> allSubjects = subjectRepository.findAll();
        model.addAttribute("allSubjects", allSubjects != null ? allSubjects : new ArrayList<>());

        return "teacher-portal";
    }

    // =========================================================
    // --- CLEARANCE TRACKER / PUBLIC ACCESS ---
    // =========================================================

    @GetMapping("/clearance/lookup")
    public String showClearanceLookup(Model model) {
        // PREVENT Error 500 on first load
        model.addAttribute("student", null);
        model.addAttribute("unresolvedGuidance", new ArrayList<>());
        model.addAttribute("unresolvedFacilities", new ArrayList<>());
        return "clearance-tracker";
    }

    @GetMapping("/requests")
    public String showRequests() {
        return "requests";
    }

    @GetMapping("/clearance-tracker")
    public String showClearanceTrackerForm(Model model) {
        // PREVENT Error 500 on first load
        model.addAttribute("student", null);
        model.addAttribute("unresolvedGuidance", new ArrayList<>());
        model.addAttribute("unresolvedFacilities", new ArrayList<>());
        return "clearance-tracker";
    }

    @GetMapping("/clearance/track")
    public String performClearanceTracking(@RequestParam("lrn") String lrn, Model model) {
        Optional<Student> studentOpt = studentRepository.findByLrn(lrn);
        if (studentOpt.isEmpty()) {
            studentOpt = studentRepository.findById(lrn);
        }

        if (studentOpt.isEmpty()) {
            model.addAttribute("error", "Student LRN not found. Please try again.");
            model.addAttribute("notFound", true);
            
            // FIX Error 500: Missing attributes when student not found
            model.addAttribute("student", null);
            model.addAttribute("unresolvedGuidance", new ArrayList<>());
            model.addAttribute("unresolvedFacilities", new ArrayList<>());
            
            return "clearance-status";
        }

        Student student = studentOpt.get();
        model.addAttribute("student", student);

        model.addAttribute("adviserStatus", student.getAdviserClearance());
        model.addAttribute("labStatus", student.getLabClearance());
        model.addAttribute("sportsStatus", student.getSportsClearance());
        model.addAttribute("guidanceStatus", student.getGuidanceClearance());
        model.addAttribute("facilitiesStatus", student.getFacilitiesClearance());
        model.addAttribute("libraryStatus", student.getLibraryClearance());

        List<GuidanceLog> unresolvedGuidance = guidanceLogRepository.findByLrnAndStatus(student.getLrn(), "UNSOLVED");
        List<FacilityLog> unresolvedFacilities = facilityLogRepository.findByLrnAndStatus(student.getLrn(), "UNSOLVED");
        
        // FIX Error 500: Ensure lists are never null
        model.addAttribute("unresolvedGuidance", unresolvedGuidance != null ? unresolvedGuidance : new ArrayList<>());
        model.addAttribute("unresolvedFacilities", unresolvedFacilities != null ? unresolvedFacilities : new ArrayList<>());

        return "clearance-status";
    }

    // =========================================================
    // --- ADMIN DASHBOARD ---
    // =========================================================

    @GetMapping("/admin-dashboard")
    public String showAdminDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        
        // Flexible Admin Role Checking
        if (user == null || user.getRoleName() == null || !user.getRoleName().trim().toUpperCase().contains("ADMIN")) {
            return "redirect:/login";
        }

        model.addAttribute("username", user.getName());
        model.addAttribute("allUsers", userRepository.findAll());
        model.addAttribute("cmsAnnouncements", announcementRepository.findAll());
        model.addAttribute("cmsResources", resourceHubRepository.findAll());

        return "admin-dashboard";
    }

    @PostMapping("/admin/account/edit")
    public String adminEditAccount(@ModelAttribute User userToUpdate) {
        Optional<User> existing = userRepository.findById(userToUpdate.getId());
        if (existing.isPresent()) {
            User current = existing.get();
            current.setUsername(userToUpdate.getUsername());
            current.setPassword(userToUpdate.getPassword());
            current.setRoleName(userToUpdate.getRoleName());
            userRepository.save(current);
            return "redirect:/admin-dashboard?tab=1&success=Account+updated";
        }
        return "redirect:/admin-dashboard?tab=1&error=Account+not+found";
    }

    @PostMapping("/admin/adviser/create")
    public String adminCreateAdviser(
            @RequestParam("name") String name,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("assignedSection") String assignedSection) {

        try {
            User newAdviser = new User();
            newAdviser.setName(name);
            newAdviser.setUsername(username);
            newAdviser.setEmail(email);
            newAdviser.setPassword(password);
            newAdviser.setAssignedSection(assignedSection);
            newAdviser.setRoleName("ADVISER");

            User savedUser = authService.registerUser(newAdviser);
            if (savedUser == null) return "redirect:/admin-dashboard?tab=1&error=Email+address+is+already+in+use.";
            return "redirect:/admin-dashboard?tab=1&success=Adviser+Account+Successfully+Created.";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin-dashboard?tab=1&error=Failed+to+create+account";
        }
    }

    @PostMapping("/admin/adviser/delete")
    public String adminDeleteAccount(@RequestParam("userId") Long userId) {
        userRepository.deleteById(userId);
        return "redirect:/admin-dashboard?tab=1&success=Account+Deleted";
    }

    @PostMapping("/admin/cms/update")
    public String updateCmsAnnouncements(@ModelAttribute Announcement announcement) {
        announcementRepository.save(announcement);
        return "redirect:/admin-dashboard?tab=2&success=Announcement+Published";
    }

    @PostMapping("/admin/resource/add")
    public String updateCmsResources(@ModelAttribute ResourceHub resource) {
        resourceHubRepository.save(resource);
        return "redirect:/admin-dashboard?tab=2&success=Resource+Added";
    }

    @PostMapping("/admin/resource/delete")
    public String deleteResource(@RequestParam("resourceId") Long resourceId) {
        resourceHubRepository.deleteById(resourceId);
        return "redirect:/admin-dashboard?tab=2&success=Resource+Deleted";
    }

    // =========================================================
    // --- ADVISER FUNCTIONS ---
    // =========================================================

    @PostMapping("/adviser/student/add")
    public String addStudentByAdviser(
            @RequestParam("lrn") String lrn,
            @RequestParam("name") String name,
            HttpSession session) {

        User loggedInUser = (User) session.getAttribute("user");
        String section = (loggedInUser != null && loggedInUser.getAssignedSection() != null) 
                            ? loggedInUser.getAssignedSection() : "Not Assigned";

        String cleanLrn = lrn.trim();
        String cleanName = name.trim();

        if (cleanLrn.isEmpty() || cleanName.isEmpty()) {
            return "redirect:/teacher-portal?error=LRN+and+Name+are+required";
        }

        Student newStudent = new Student(cleanLrn, cleanName, section);
        studentRepository.save(newStudent);

        return "redirect:/teacher-portal?success=Student+added+successfully";
    }

    @PostMapping("/adviser/student/delete")
    public String deleteStudentByAdviser(@RequestParam("lrn") String lrn) {
        studentRepository.deleteById(lrn);
        return "redirect:/teacher-portal?success=Student+deleted+successfully";
    }

    @PostMapping("/adviser/subject/add")
    public String addSubjectToSection(@RequestParam("name") String name, HttpSession session) {
        try {
            User loggedInUser = (User) session.getAttribute("user");
            String section = (loggedInUser != null && loggedInUser.getAssignedSection() != null) ? loggedInUser.getAssignedSection() : "Not Assigned";
            
            if (name == null || name.trim().isEmpty()) {
                return "redirect:/teacher-portal?error=Subject+name+cannot+be+blank";
            }

            Subject subj = new Subject();
            subj.setName(name.trim());
            subj.setSection(section);
            subjectRepository.save(subj);
            return "redirect:/teacher-portal?success=Subject+Added";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/teacher-portal?error=Failed+to+save+subject+mapping";
        }
    }

    @PostMapping("/adviser/subject/delete")
    public String deleteSubjectFromSection(@RequestParam("id") Long id) {
        try {
            subjectRepository.deleteById(id);
            return "redirect:/teacher-portal?success=Subject+Removed";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/teacher-portal?error=Failed+to+remove+subject";
        }
    }

    @PostMapping("/adviser/grade/save")
    public String saveStudentGrades(@RequestParam("studentLrn") String studentLrn, @RequestParam Map<String, String> params) {
        try {
            for (String key : params.keySet()) {
                if (key.startsWith("subject_")) {
                    Long subjectId = Long.parseLong(key.replace("subject_", ""));
                    String gradeValue = params.get(key);
                    Optional<Subject> subjOpt = subjectRepository.findById(subjectId);
                    
                    if (subjOpt.isPresent()) {
                        Subject subj = subjOpt.get();
                        List<StudentGrade> existing = studentGradeRepository.findByStudentLrn(studentLrn);
                        StudentGrade currentGrade = existing.stream().filter(g -> g.getSubjectId().equals(subjectId)).findFirst().orElse(new StudentGrade());
                        
                        if (currentGrade.getStudentLrn() == null) {
                            currentGrade.setStudentLrn(studentLrn);
                            currentGrade.setSubjectId(subjectId);
                            currentGrade.setSubjectName(subj.getName());
                        }
                        currentGrade.setGrade(gradeValue);
                        
                        try {
                            double numericGrade = Double.parseDouble(gradeValue);
                            currentGrade.setRemarks(numericGrade >= 75.0 ? "PASSED" : "FAILED");
                        } catch (Exception ex) {
                            currentGrade.setRemarks("FAILED");
                        }
                        
                        studentGradeRepository.save(currentGrade);
                    }
                }
            }
            return "redirect:/teacher-portal?success=Grades+Saved";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/teacher-portal?error=Failed+to+process+and+lock+in+grades";
        }
    }

    // =========================================================
    // --- REQUEST DOCUMENTS (PUBLIC & REGISTRAR) ---
    // =========================================================

    @GetMapping("/request-document")
    public String showRequestDocumentForm() {
        return "request-document";
    }

    @PostMapping("/request-document/submit")
    public String submitDocumentRequest(@ModelAttribute DocumentRequest req) {
        req.setStatus("PENDING");
        documentRequestRepository.save(req);
        return "redirect:/request-document?success=Request+Submitted";
    }

    @GetMapping("/registrar-dashboard")
    public String showRegistrarDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleName() == null || !user.getRoleName().trim().toUpperCase().contains("REGISTRAR")) return "redirect:/login";

        List<DocumentRequest> allRequests = documentRequestRepository.findAll();
        List<DocumentRequest> pending = allRequests.stream().filter(r -> "PENDING".equalsIgnoreCase(r.getStatus())).collect(Collectors.toList());
        List<DocumentRequest> completed = allRequests.stream().filter(r -> "COMPLETED".equalsIgnoreCase(r.getStatus())).collect(Collectors.toList());

        model.addAttribute("pendingRequests", pending);
        model.addAttribute("completedRequests", completed);
        return "registrar-dashboard";
    }

    @PostMapping("/registrar/request/complete")
    public String markRequestComplete(@RequestParam("requestId") Long requestId) {
        Optional<DocumentRequest> reqOpt = documentRequestRepository.findById(requestId);
        if (reqOpt.isPresent()) {
            DocumentRequest r = reqOpt.get();
            r.setStatus("COMPLETED");
            documentRequestRepository.save(r);
        }
        return "redirect:/registrar-dashboard?success=Request+marked+as+Completed";
    }

    @PostMapping("/registrar/request/delete")
    public String deleteDocumentRequest(@RequestParam("requestId") Long requestId) {
        documentRequestRepository.deleteById(requestId);
        return "redirect:/registrar-dashboard?success=Request+Deleted";
    }

    // =========================================================
    // --- FACILITIES / PROPERTY DASHBOARD ---
    // =========================================================

    @GetMapping("/facilities-dashboard")
    public String showFacilitiesDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleName() == null || !user.getRoleName().trim().toUpperCase().contains("FACILITIES")) return "redirect:/login";

        model.addAttribute("username", user.getName());
        model.addAttribute("allStudents", studentRepository.findAllByOrderByNameAsc());
        model.addAttribute("facilityLogs", facilityLogRepository.findAll());
        return "facilities-dashboard";
    }

    @PostMapping({"/facilities/log/add", "/facilities/report/save"})
    public String addFacilityLog(@ModelAttribute FacilityLog log, RedirectAttributes redirectAttributes) {
        try {
            log.setStatus("UNSOLVED");
            if (log.getDateLogged() == null) {
                log.setDateLogged(LocalDate.now().toString());
            }
            facilityLogRepository.save(log);
            
            Optional<Student> studentOpt = studentRepository.findByLrn(log.getLrn());
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                student.setFacilitiesClearance("PENDING");
                studentRepository.save(student);
            }
            return "redirect:/facilities-dashboard?success=Report+saved";
        } catch (Exception e) {
            return "redirect:/facilities-dashboard?error=Failed+to+save+report";
        }
    }

    @PostMapping({"/facilities/log/solve", "/facilities/report/solve"})
    public String solveFacilityReport(@RequestParam("logId") Long logId) {
        Optional<FacilityLog> logOpt = facilityLogRepository.findById(logId);
        if (logOpt.isPresent()) {
            FacilityLog log = logOpt.get();
            log.setStatus("SOLVED");
            facilityLogRepository.save(log);
            
            List<FacilityLog> remaining = facilityLogRepository.findByLrnAndStatus(log.getLrn(), "UNSOLVED");
            if (remaining.isEmpty()) {
                Optional<Student> studentOpt = studentRepository.findByLrn(log.getLrn());
                if (studentOpt.isPresent()) {
                    Student student = studentOpt.get();
                    student.setFacilitiesClearance("CLEARED");
                    studentRepository.save(student);
                }
            }
        }
        return "redirect:/facilities-dashboard?success=Marked+as+solved";
    }

    @PostMapping("/facilities/log/clear")
    public String clearFacilityLogs() {
        facilityLogRepository.deleteAll();
        List<Student> students = studentRepository.findAll();
        for (Student s : students) {
            s.setFacilitiesClearance("CLEARED");
            studentRepository.save(s);
        }
        return "redirect:/facilities-dashboard?success=All+logs+cleared";
    }

    // =========================================================
    // --- GUIDANCE COUNSELOR DASHBOARD ---
    // =========================================================

    @GetMapping("/guidance-dashboard")
    public String showGuidanceDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleName() == null || !user.getRoleName().trim().toUpperCase().contains("GUIDANCE")) return "redirect:/login";

        model.addAttribute("allStudents", studentRepository.findAllByOrderByNameAsc()); 
        model.addAttribute("guidanceLogs", guidanceLogRepository.findAll()); 
        return "guidance-dashboard"; 
    }

    @PostMapping({"/guidance/log/add", "/guidance/report/save"})
    public String addGuidanceLog(@ModelAttribute GuidanceLog log, RedirectAttributes redirectAttributes) {
        try {
            log.setStatus("UNSOLVED");
            if (log.getDateLogged() == null) {
                log.setDateLogged(LocalDate.now().toString());
            }
            guidanceLogRepository.save(log);
            
            Optional<Student> studentOpt = studentRepository.findByLrn(log.getLrn());
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                student.setGuidanceClearance("PENDING");
                studentRepository.save(student);
            }
            return "redirect:/guidance-dashboard?success=Report+saved";
        } catch (Exception e) {
            return "redirect:/guidance-dashboard?error=Failed+to+save+report";
        }
    }

    @PostMapping({"/guidance/log/solve", "/guidance/report/solve"})
    public String solveGuidanceReport(@RequestParam("logId") Long logId) {
        Optional<GuidanceLog> logOpt = guidanceLogRepository.findById(logId);
        if (logOpt.isPresent()) {
            GuidanceLog log = logOpt.get();
            log.setStatus("SOLVED");
            guidanceLogRepository.save(log);
            
            List<GuidanceLog> remaining = guidanceLogRepository.findByLrnAndStatus(log.getLrn(), "UNSOLVED");
            if (remaining.isEmpty()) {
                Optional<Student> studentOpt = studentRepository.findByLrn(log.getLrn());
                if (studentOpt.isPresent()) {
                    Student student = studentOpt.get();
                    student.setGuidanceClearance("CLEARED");
                    studentRepository.save(student);
                }
            }
        }
        return "redirect:/guidance-dashboard?success=Marked+as+solved";
    }

    @PostMapping("/guidance/log/clear")
    public String clearGuidanceLogs() {
        guidanceLogRepository.deleteAll();
        List<Student> students = studentRepository.findAll();
        for (Student s : students) {
            s.setGuidanceClearance("CLEARED");
            studentRepository.save(s);
        }
        return "redirect:/guidance-dashboard?success=All+logs+cleared";
    }

    // =========================================================
    // --- CLINIC / NURSE DASHBOARD ---
    // =========================================================

    @GetMapping({"/clinic-dashboard", "/nurse-dashboard"})
    public String showClinicDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleName() == null || (!user.getRoleName().trim().toUpperCase().contains("CLINIC") && !user.getRoleName().trim().toUpperCase().contains("NURSE"))) return "redirect:/login";

        model.addAttribute("allStudents", studentRepository.findAll()); 
        model.addAttribute("clinicLogs", clinicLogRepository.findAll()); 
        return "clinic-dashboard"; 
    }

    @PostMapping({"/clinic/log/add", "/clinic/log/save"})
    public String addClinicLog(@ModelAttribute ClinicLog log, RedirectAttributes redirectAttributes) {
        try {
            if (log.getDateLogged() == null) {
                log.setDateLogged(LocalDate.now().toString());
            }
            clinicLogRepository.save(log);
            return "redirect:/clinic-dashboard?success=Log+saved";
        } catch (Exception e) {
            return "redirect:/clinic-dashboard?error=Failed+to+save+clinic+log";
        }
    }

    @PostMapping("/clinic/log/clear")
    public String clearClinicLogs() {
        clinicLogRepository.deleteAll();
        return "redirect:/clinic-dashboard?success=All+logs+cleared";
    }

    // =========================================================
    // --- SPORTS & LAB & LIBRARY DASHBOARDS ---
    // =========================================================
    
    @GetMapping("/lab-dashboard")
    public String showLabDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleName() == null || !user.getRoleName().trim().toUpperCase().contains("LAB")) return "redirect:/login";
        return "lab-dashboard"; 
    }

    @GetMapping("/sports-dashboard")
    public String showSportsDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleName() == null || !user.getRoleName().trim().toUpperCase().contains("SPORTS")) return "redirect:/login";
        return "sports-dashboard"; 
    }

    @GetMapping("/library-dashboard")
    public String showLibraryDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleName() == null || !user.getRoleName().trim().toUpperCase().contains("LIBRARY")) return "redirect:/login";
        return "library-dashboard"; 
    }

    @GetMapping("/student-liabilities-details")
    public String showStudentLiabilitiesDetails(Model model) {
        List<Student> allStudents = studentRepository.findAll();
        List<LiabilityDetailsRow> details = new ArrayList<>();

        for (Student s : allStudents) {
            boolean isCleared = "CLEARED".equalsIgnoreCase(s.getAdviserClearance()) &&
                                "CLEARED".equalsIgnoreCase(s.getLabClearance()) &&
                                "CLEARED".equalsIgnoreCase(s.getSportsClearance()) &&
                                "CLEARED".equalsIgnoreCase(s.getGuidanceClearance()) &&
                                "CLEARED".equalsIgnoreCase(s.getFacilitiesClearance()) &&
                                "CLEARED".equalsIgnoreCase(s.getLibraryClearance());

            String overallStatus = isCleared ? "CLEARED" : "HAS UNRESOLVED LIABILITIES";
            LiabilityDetailsRow row = new LiabilityDetailsRow(s.getName(), s.getLrn(), overallStatus);

            List<GuidanceLog> guidLogs = guidanceLogRepository.findByLrnAndStatus(s.getLrn(), "UNSOLVED");
            for (GuidanceLog gl : guidLogs) {
                row.addOpenItem(new OpenLiabilityItem("Guidance Office", gl.getIncident(), LocalDateTime.now(), "UNSOLVED"));
            }

            List<FacilityLog> facLogs = facilityLogRepository.findByLrnAndStatus(s.getLrn(), "UNSOLVED");
            for (FacilityLog fl : facLogs) {
                row.addOpenItem(new OpenLiabilityItem("Property & Facilities", fl.getFacility() + " - " + fl.getDescription(), LocalDateTime.now(), "UNSOLVED"));
            }

            details.add(row);
        }

        model.addAttribute("details", details);
        return "student-liabilities-details";
    }

    public static class LiabilityDetailsRow {
        private final String studentName;
        private final String lrn;
        private final String status;
        private final List<OpenLiabilityItem> openItems = new ArrayList<>();
        public LiabilityDetailsRow(String studentName, String lrn, String status) { this.studentName = studentName; this.lrn = lrn; this.status = status; }
        public String getStudentName() { return studentName; }
        public String getLrn() { return lrn; }
        public String getStatus() { return status; }
        public List<OpenLiabilityItem> getOpenItems() { return openItems; }
        public void addOpenItem(OpenLiabilityItem item) { if (item != null) openItems.add(item); }
    }

    public static class OpenLiabilityItem {
        private final String category;
        private final String description;
        private final LocalDateTime loggedAt;
        private final String state;
        public OpenLiabilityItem(String category, String description, LocalDateTime loggedAt, String state) { this.category = category; this.description = description; this.loggedAt = loggedAt; this.state = state; }
        public String getCategory() { return category; }
        public String getDescription() { return description; }
        public LocalDateTime getLoggedAt() { return loggedAt; }
        public String getState() { return state; }
    }
}