package com.linhs.portal.controller;

// Core Java & Spring Imports
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.linhs.portal.model.Announcement;
import com.linhs.portal.model.BorrowRecord;
import com.linhs.portal.model.ClinicLog;
import com.linhs.portal.model.DocumentRequest;
import com.linhs.portal.model.FacilityLog;
import com.linhs.portal.model.Gallery;
import com.linhs.portal.model.GuidanceLog;
import com.linhs.portal.model.LabLiability;
import com.linhs.portal.model.Liability;
import com.linhs.portal.model.ResourceHub;
import com.linhs.portal.model.Student;
import com.linhs.portal.model.StudentGrade;
import com.linhs.portal.model.Subject;
import com.linhs.portal.model.User;
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

import jakarta.servlet.http.HttpSession;

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

    @org.springframework.beans.factory.annotation.Autowired
    private com.linhs.portal.repository.LabEquipmentRepository labEquipmentRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private com.linhs.portal.repository.LabLiabilityRepository labLiabilityRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private com.linhs.portal.repository.LiabilityRepository liabilityRepository;

    // =========================================================
    // --- AUTHENTICATION & PUBLIC ENDPOINTS ---
    // =========================================================

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

            String role = (user.getRoleName() != null) ? user.getRoleName().trim().toUpperCase() : "";
            
            // Debug print so we can see exactly what happens in your IDE terminal
            System.out.println("==== SUCCESSFUL LOGIN | User Role: [" + role + "] ====");

            // THE FIX: Uses '!' to make sure the Admin is NOT a librarian
            if (role.contains("ADMIN") && !role.contains("FACILITIES") && !role.contains("SPORTS") && !role.contains("LAB") && !role.contains("LIBRARY") && !role.contains("LIBRARIAN")) {
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
            } else if (role.contains("LIBRARY") || role.contains("LIBRARIAN")) {
                return "redirect:/library-dashboard";
            }

            System.out.println("==== ERROR: Role didn't match any dashboard. Redirecting to home. ====");
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

    @GetMapping("/about")
    public String showAbout() { return "about"; }

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
    // --- CLEARANCE TRACKER / PUBLIC ACCESS ---
    // =========================================================

    @GetMapping("/clearance/lookup")
    public String showClearanceLookup() {
        return "clearance-status";
    }

    @GetMapping("/clearance-tracker")
    public String showClearanceTrackerForm() {
        return "clearance-status";
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
        if (user == null || user.getRoleName() == null || !user.getRoleName().trim().toUpperCase().contains("ADMIN")) {
            return "redirect:/login";
        }

        model.addAttribute("adminUser", user);
        model.addAttribute("username", user.getName());
        
        List<User> usersList = userRepository.findAll();
        model.addAttribute("allUsers", usersList);
        model.addAttribute("users", usersList);
        
        List<User> staffList = usersList.stream()
                .filter(u -> u.getRoleName() != null && !u.getRoleName().toUpperCase().contains("ADVISER") && !u.getRoleName().toUpperCase().contains("ADMIN"))
                .collect(Collectors.toList());
        model.addAttribute("staffAccounts", staffList);

        List<User> advisersList = usersList.stream()
                .filter(u -> u.getRoleName() != null && u.getRoleName().toUpperCase().contains("ADVISER"))
                .collect(Collectors.toList());
        model.addAttribute("adviserAccounts", advisersList);

        model.addAttribute("resources", resourceHubRepository.findAll());
        model.addAttribute("cmsAnnouncements", announcementRepository.findAll());
        model.addAttribute("announcements", announcementRepository.findAll());

        return "admin-dashboard";
    }

    @PostMapping("/admin/account/edit")
    public String adminEditAccount(@ModelAttribute User userToUpdate, @RequestParam Map<String, String> params) {
        Long id = userToUpdate.getId() != null ? userToUpdate.getId() : (params.containsKey("id") && !params.get("id").isEmpty() ? Long.parseLong(params.get("id")) : null);
        
        if (id != null) {
            Optional<User> existing = userRepository.findById(id);
            if (existing.isPresent()) {
                User current = existing.get();
                if(params.containsKey("name")) current.setName(params.get("name"));
                if(params.containsKey("email")) current.setEmail(params.get("email"));
                if(params.containsKey("username")) current.setUsername(params.get("username"));
                
                String pass = params.get("password");
                if (pass != null && !pass.trim().isEmpty()) {
                    current.setPassword(pass);
                }
                userRepository.save(current);
                return "redirect:/admin-dashboard?tab=0&success=Account+updated";
            }
        }
        return "redirect:/admin-dashboard?tab=0&error=Account+not+found";
    }

    @PostMapping("/admin/adviser/create")
    public String adminCreateAdviser(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "section", required = false) String section,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam Map<String, String> allParams) {

        try {
            String finalName = (name != null) ? name : allParams.getOrDefault("name", "");
            String finalEmail = (email != null) ? email : allParams.getOrDefault("email", "");
            String finalPassword = (password != null) ? password : allParams.getOrDefault("password", "");
            String finalSection = (section != null) ? section : allParams.getOrDefault("assignedSection", "");

            User newAdviser = new User();
            newAdviser.setName(finalName);
            newAdviser.setUsername(finalEmail);
            newAdviser.setEmail(finalEmail);
            newAdviser.setPassword(finalPassword);
            newAdviser.setAssignedSection(finalSection);
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
    public String adminDeleteAccount(@RequestParam(value = "id", required = false) Long id, @RequestParam(value = "userId", required = false) Long userId) {
        Long targetId = (id != null) ? id : userId;
        if (targetId != null) {
            userRepository.deleteById(targetId);
            return "redirect:/admin-dashboard?tab=1&success=Account+Deleted";
        }
        return "redirect:/admin-dashboard?tab=1&error=ID+not+provided";
    }

    @PostMapping("/admin/cms/update")
    public String updateCmsAnnouncements(
            @RequestParam(value = "type", required = false, defaultValue = "announcement") String type,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "caption", required = false) String caption,
            @RequestParam(value = "fileAttachment", required = false) MultipartFile fileAttachment) {
        try {
            if ("announcement".equalsIgnoreCase(type) || "about".equalsIgnoreCase(type)) {
                Announcement announcement = new Announcement();
                announcement.setTitle(title != null ? title : type);
                announcement.setContent(content);
                announcementRepository.save(announcement);
            } else if ("gallery".equalsIgnoreCase(type)) {
                Gallery gallery = new Gallery();
                gallery.setCaption(caption);
                if (fileAttachment != null && !fileAttachment.isEmpty()) {
                    gallery.setImageUrl(fileAttachment.getOriginalFilename());
                }
                galleryRepository.save(gallery);
            }
            return "redirect:/admin-dashboard?tab=2&success=Content+Published";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin-dashboard?tab=2&error=Failed+to+publish+content";
        }
    }

    @PostMapping("/admin/resource/add")
    public String updateCmsResources(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            ResourceHub resource = new ResourceHub();
            resource.setTitle(title);
            if (file != null && !file.isEmpty()) {
                resource.setFileUrl(file.getOriginalFilename());
            }
            resourceHubRepository.save(resource);
            return "redirect:/admin-dashboard?tab=2&success=Resource+Added";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin-dashboard?tab=2&error=Failed+to+add+resource";
        }
    }

    @PostMapping("/admin/resource/delete")
    public String deleteResource(@RequestParam(value = "id", required = false) Long id, @RequestParam(value = "resourceId", required = false) Long resourceId) {
        Long targetId = (id != null) ? id : resourceId;
        if(targetId != null) {
            resourceHubRepository.deleteById(targetId);
            return "redirect:/admin-dashboard?tab=2&success=Resource+Deleted";
        }
        return "redirect:/admin-dashboard?tab=2&error=Failed+to+delete+resource";
    }

    // =========================================================
    // --- ADVISER / TEACHER PORTAL ---
    // =========================================================

    @GetMapping({"/teacher-portal", "/adviser-dashboard"})
    public String showTeacherPortal(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        
        if (user == null || user.getRoleName() == null || 
            (!user.getRoleName().trim().toUpperCase().contains("ADVISER") && 
             !user.getRoleName().trim().toUpperCase().contains("TEACHER"))) {
            return "redirect:/login";
        }

        model.addAttribute("adviser", user);
        model.addAttribute("username", user.getName());
        model.addAttribute("assignedSection", user.getAssignedSection() != null ? user.getAssignedSection() : "Unassigned");

        List<Student> assignedStudents = new ArrayList<>();
        if (user.getAssignedSection() != null) {
            assignedStudents = studentRepository.findBySection(user.getAssignedSection());
        }
        model.addAttribute("assignedStudents", assignedStudents);
        model.addAttribute("students", assignedStudents);

        List<Subject> allSubjects = subjectRepository.findAll();
        model.addAttribute("allSubjects", allSubjects != null ? allSubjects : new ArrayList<>());
        
        List<Subject> sectionSubjects = allSubjects.stream()
                .filter(s -> s.getSection() != null && s.getSection().equalsIgnoreCase(user.getAssignedSection()))
                .collect(Collectors.toList());
        model.addAttribute("subjects", sectionSubjects);

        List<StudentGrade> allGrades = studentGradeRepository.findAll();
        model.addAttribute("allGrades", allGrades);

        return "adviser-dashboard"; 
    }

    @PostMapping("/adviser/student/add")
    public String addStudentByAdviser(
            @RequestParam("lrn") String lrn,
            @RequestParam("name") String name,
            HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");
        String section = (loggedInUser != null && loggedInUser.getAssignedSection() != null) 
                            ? loggedInUser.getAssignedSection() : "Not Assigned";

        if (lrn.trim().isEmpty() || name.trim().isEmpty()) {
            return "redirect:/adviser-dashboard?tab=students&error=LRN+and+Name+are+required";
        }

        Student newStudent = new Student(lrn.trim(), name.trim(), section);
        studentRepository.save(newStudent);
        return "redirect:/adviser-dashboard?tab=students&success=Student+added+successfully";
    }

    @PostMapping("/adviser/student/edit")
    public String editStudentByAdviser(@RequestParam("lrn") String lrn, @RequestParam("name") String name) {
        Optional<Student> studentOpt = studentRepository.findById(lrn);
        if (studentOpt.isPresent()) {
            Student s = studentOpt.get();
            s.setName(name.trim());
            studentRepository.save(s);
            return "redirect:/adviser-dashboard?tab=students&success=Student+Updated";
        }
        return "redirect:/adviser-dashboard?tab=students&error=Student+Not+Found";
    }

    @PostMapping("/adviser/student/delete")
    public String deleteStudentByAdviser(@RequestParam("lrn") String lrn) {
        studentRepository.deleteById(lrn);
        return "redirect:/adviser-dashboard?tab=students&success=Student+deleted+successfully";
    }

    @PostMapping("/adviser/subject/add")
    public String addSubjectToSection(@RequestParam("name") String name, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");
        String section = (loggedInUser != null && loggedInUser.getAssignedSection() != null) ? loggedInUser.getAssignedSection() : "Not Assigned";
        
        if (name == null || name.trim().isEmpty()) {
            return "redirect:/adviser-dashboard?tab=subjects&error=Subject+name+cannot+be+blank";
        }

        Subject subj = new Subject();
        subj.setName(name.trim());
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
            return "redirect:/adviser-dashboard?tab=grading&success=Grades+Saved";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/adviser-dashboard?tab=grading&error=Failed+to+process+and+lock+in+grades";
        }
    }

    // =========================================================
    // --- REQUEST DOCUMENTS (PUBLIC & REGISTRAR) ---
    // =========================================================

    @GetMapping("/requests")
    public String showRequests() {
        return "request-doc";
    }

    @GetMapping("/request-document")
    public String showRequestDocumentForm() {
        return "request-doc";
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
    public String addFacilityLog(@ModelAttribute FacilityLog log) {
        try {
            log.setStatus("UNSOLVED");
            if (log.getDateLogged() == null) log.setDateLogged(LocalDate.now().toString());
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
    public String addGuidanceLog(@ModelAttribute GuidanceLog log) {
        try {
            log.setStatus("UNSOLVED");
            if (log.getDateLogged() == null) log.setDateLogged(LocalDate.now().toString());
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
    public String addClinicLog(@ModelAttribute ClinicLog log) {
        try {
            if (log.getDateLogged() == null) log.setDateLogged(LocalDate.now().toString());
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

        // 1. Fetch Inventory
        model.addAttribute("labEquipments", labEquipmentRepository.findAll());

        // 2. Fetch Active Borrows
        List<BorrowRecord> activeBorrows = borrowRecordRepository.findAll().stream()
                .filter(b -> "LAB_BORROWED".equalsIgnoreCase(b.getStatus()))
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("activeBorrows", activeBorrows);

        // 3. Fetch Unresolved Liabilities
        List<LabLiability> unresolved = labLiabilityRepository.findAll().stream()
                .filter(l -> "UNPAID".equalsIgnoreCase(l.getStatus()))
                .collect(Collectors.toList());
        model.addAttribute("labLiabilities", unresolved);

        return "lab-dashboard"; 
    }

    @PostMapping("/lab/borrow/add")
    public String addLabBorrow(
            @RequestParam("studentLrn") String studentLrn,
            @RequestParam("equipmentName") String equipmentName) { 
        try {
            // SECURITY CHECK 1: Verify the student exists
            java.util.Optional<Student> studentOpt = studentRepository.findById(studentLrn);
            if (studentOpt.isEmpty()) {
                return "redirect:/lab-dashboard?tab=borrowed&error=Loan+Failed:+Student+LRN+not+found.";
            }

            // SECURITY CHECK 2: Verify the apparatus exists AND is in stock
            java.util.Optional<com.linhs.portal.model.LabEquipment> equipOpt = labEquipmentRepository.findByName(equipmentName);
            if (equipOpt.isEmpty()) {
                return "redirect:/lab-dashboard?tab=borrowed&error=Loan+Failed:+Apparatus+not+found+in+inventory.";
            }
            
            com.linhs.portal.model.LabEquipment equipment = equipOpt.get();
            if (equipment.getQuantity() <= 0) {
                return "redirect:/lab-dashboard?tab=borrowed&error=Loan+Failed:+Apparatus+is+currently+out+of+stock!";
            }

            // 1. Deduct from lab inventory
            equipment.setQuantity(equipment.getQuantity() - 1);
            if (equipment.getQuantity() == 0) {
                equipment.setStatus("OUT OF STOCK"); 
            }
            labEquipmentRepository.save(equipment);

            // 2. Log the borrow using your EXISTING BorrowRecord
            BorrowRecord record = new BorrowRecord();
            record.setStudentLrn(studentLrn);
            
            // Uses the fields specific to your BorrowRecord model
            record.setItemName(equipmentName); 
            record.setBorrowedAt(java.time.LocalDateTime.now()); 
            
            // TRICK: Saves as LAB_BORROWED so it doesn't mix with Sports items!
            record.setStatus("LAB_BORROWED"); 
            borrowRecordRepository.save(record);

            // 3. Flag the student's Lab clearance as pending
            Student s = studentOpt.get();
            s.setLabClearance("PENDING"); 
            studentRepository.save(s);
            
            return "redirect:/lab-dashboard?tab=borrowed&success=Apparatus+loan+authorized+and+inventory+updated";
        } catch (Exception e) {
            return "redirect:/lab-dashboard?tab=borrowed&error=Failed+to+record+loan";
        }
    }

    @PostMapping("/lab/borrow/return")
    public String returnLabBorrow(@RequestParam("id") Long id) {
        java.util.Optional<BorrowRecord> opt = borrowRecordRepository.findById(id);
        if (opt.isPresent()) {
            BorrowRecord record = opt.get();
            
            // 1. Mark record as returned
            record.setStatus("LAB_RETURNED");
            borrowRecordRepository.save(record);

            // 2. Add the apparatus back to the inventory stock
            // Make sure to use getItemName() to match your model
            java.util.Optional<com.linhs.portal.model.LabEquipment> equipOpt = labEquipmentRepository.findByName(record.getItemName());
            if (equipOpt.isPresent()) {
                com.linhs.portal.model.LabEquipment equipment = equipOpt.get();
                equipment.setQuantity(equipment.getQuantity() + 1);
                
                if ("OUT OF STOCK".equals(equipment.getStatus()) || "UNAVAILABLE".equals(equipment.getStatus())) {
                    equipment.setStatus("AVAILABLE");
                }
                labEquipmentRepository.save(equipment);
            }
        }
        return "redirect:/lab-dashboard?tab=borrowed&success=Apparatus+returned+and+inventory+restocked";
    }

    @PostMapping("/lab/liability/clear")
    public String clearLabLiability(@RequestParam("liabilityId") Long liabilityId) {
        java.util.Optional<com.linhs.portal.model.LabLiability> opt = labLiabilityRepository.findById(liabilityId);
        if (opt.isPresent()) {
            com.linhs.portal.model.LabLiability liability = opt.get();
            liability.setStatus("CLEARED");
            labLiabilityRepository.save(liability);
            
            // Check if student has other pending liabilities before fully clearing them
            List<com.linhs.portal.model.LabLiability> remaining = labLiabilityRepository.findByStudentLrnAndStatus(liability.getStudentLrn(), "UNPAID");
            if (remaining.isEmpty()) {
                java.util.Optional<Student> studentOpt = studentRepository.findById(liability.getStudentLrn());
                if (studentOpt.isPresent()) {
                    Student s = studentOpt.get();
                    s.setLabClearance("CLEARED");
                    studentRepository.save(s);
                }
            }
        }
        return "redirect:/lab-dashboard?tab=liabilities&success=Liability+resolved+and+student+cleared";
    }

    @GetMapping("/sports-dashboard")
    public String showSportsDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleName() == null || !user.getRoleName().trim().toUpperCase().contains("SPORTS")) {
            return "redirect:/login";
        }

        model.addAttribute("sportsEquipments", sportsEquipmentRepository.findAll());

        List<BorrowRecord> activeBorrows = borrowRecordRepository.findAll().stream()
                .filter(b -> "BORROWED".equalsIgnoreCase(b.getStatus()))
                .collect(Collectors.toList());
        model.addAttribute("activeBorrows", activeBorrows);

        // FIXED: Filter by "PENDING" to match your Liability.java model
        List<Liability> unresolved = liabilityRepository.findAll().stream()
                .filter(l -> "PENDING".equalsIgnoreCase(l.getStatus()))
                .collect(Collectors.toList());
        model.addAttribute("sportsLiabilities", unresolved);

        return "sports-dashboard";
    }

    // --- SPORTS DASHBOARD POST ROUTES ---

    @PostMapping("/sports/equipment/add")
    public String addSportsEquipment(@ModelAttribute com.linhs.portal.model.SportsEquipment equipment) {
        sportsEquipmentRepository.save(equipment);
        return "redirect:/sports-dashboard?tab=inventory&success=Equipment+registered+to+inventory";
    }

    @PostMapping("/sports/borrow/add")
    public String addSportsBorrow(
            @RequestParam("studentLrn") String studentLrn,
            @RequestParam("equipmentName") String equipmentName) { 
        try {
            // SECURITY CHECK 1: Verify the student actually exists
            java.util.Optional<Student> studentOpt = studentRepository.findById(studentLrn);
            if (studentOpt.isEmpty()) {
                return "redirect:/sports-dashboard?tab=borrowed&error=Loan+Failed:+Student+LRN+not+found.";
            }

            // SECURITY CHECK 2: Verify the item exists AND is in stock
            java.util.Optional<com.linhs.portal.model.SportsEquipment> equipOpt = sportsEquipmentRepository.findByEquipmentName(equipmentName);
            if (equipOpt.isEmpty()) {
                return "redirect:/sports-dashboard?tab=borrowed&error=Loan+Failed:+Equipment+name+not+found+in+inventory.";
            }
            
            com.linhs.portal.model.SportsEquipment equipment = equipOpt.get();
            if (equipment.getQuantity() <= 0) {
                return "redirect:/sports-dashboard?tab=borrowed&error=Loan+Failed:+Item+is+currently+out+of+stock!";
            }

            // --- ALL CHECKS PASSED: EXECUTE THE LOAN ---

            // 1. Deduct from inventory
            equipment.setQuantity(equipment.getQuantity() - 1);
            if (equipment.getQuantity() == 0) {
                equipment.setStatus("OUT OF STOCK"); // Auto-update status
            }
            sportsEquipmentRepository.save(equipment);

            // 2. Log the active borrow record
            BorrowRecord record = new BorrowRecord();
            record.setStudentLrn(studentLrn);
            record.setItemName(equipmentName); 
            record.setBorrowedAt(java.time.LocalDateTime.now()); 
            record.setStatus("BORROWED");
            borrowRecordRepository.save(record);

            // 3. Flag the student's PE clearance as pending
            Student s = studentOpt.get();
            s.setSportsClearance("PENDING"); 
            studentRepository.save(s);
            
            return "redirect:/sports-dashboard?tab=borrowed&success=Equipment+loan+authorized+and+inventory+updated";
        } catch (Exception e) {
            return "redirect:/sports-dashboard?tab=borrowed&error=Failed+to+record+loan";
        }
    }

    @PostMapping("/sports/borrow/return")
    public String returnSportsBorrow(@RequestParam("id") Long id) {
        java.util.Optional<BorrowRecord> opt = borrowRecordRepository.findById(id);
        if (opt.isPresent()) {
            BorrowRecord record = opt.get();
            
            // 1. Mark record as returned
            record.setStatus("RETURNED");
            borrowRecordRepository.save(record);

            // 2. Add the item back to the inventory stock
            java.util.Optional<com.linhs.portal.model.SportsEquipment> equipOpt = sportsEquipmentRepository.findByEquipmentName(record.getItemName());
            if (equipOpt.isPresent()) {
                com.linhs.portal.model.SportsEquipment equipment = equipOpt.get();
                equipment.setQuantity(equipment.getQuantity() + 1);
                
                // If it was previously out of stock, mark it available again
                if ("OUT OF STOCK".equals(equipment.getStatus())) {
                    equipment.setStatus("AVAILABLE");
                }
                sportsEquipmentRepository.save(equipment);
            }
        }
        return "redirect:/sports-dashboard?tab=borrowed&success=Item+returned+and+inventory+restocked";
    }

    @PostMapping("/sports/equipment/edit")
    public String editSportsEquipment(
            @RequestParam("id") Long id,
            @RequestParam("equipmentName") String equipmentName,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("status") String status) {
        
        java.util.Optional<com.linhs.portal.model.SportsEquipment> opt = sportsEquipmentRepository.findById(id);
        if (opt.isPresent()) {
            com.linhs.portal.model.SportsEquipment equipment = opt.get();
            
            // Overwrite the old values with the new form values
            equipment.setEquipmentName(equipmentName);
            equipment.setQuantity(quantity);
            equipment.setStatus(status);
            
            sportsEquipmentRepository.save(equipment);
            return "redirect:/sports-dashboard?tab=inventory&success=Equipment+updated+successfully";
        }
        return "redirect:/sports-dashboard?tab=inventory&error=Equipment+not+found";
    }

    @PostMapping("/sports/equipment/delete")
    public String deleteSportsEquipment(@RequestParam("id") Long id) {
        try {
            // Delete the item from the database
            sportsEquipmentRepository.deleteById(id);
            return "redirect:/sports-dashboard?tab=inventory&success=Equipment+deleted+successfully";
        } catch (Exception e) {
            // If the item can't be deleted (e.g., it is linked to an active borrow record), catch the error safely
            return "redirect:/sports-dashboard?tab=inventory&error=Cannot+delete+equipment.+Please+ensure+all+borrowed+units+are+returned+first.";
        }
    }

    @PostMapping("/sports/liability/clear")
    public String clearSportsLiability(@RequestParam("liabilityId") Long liabilityId) {
        java.util.Optional<com.linhs.portal.model.Liability> opt = liabilityRepository.findById(liabilityId);
        if (opt.isPresent()) {
            com.linhs.portal.model.Liability liability = opt.get();
            liability.setStatus("CLEARED");
            liabilityRepository.save(liability);
            
            // FIXED: Properly fetches the student using your Entity relationship
            Student s = liability.getStudent();
            if (s != null) {
                s.setSportsClearance("CLEARED");
                studentRepository.save(s);
            }
        }
        return "redirect:/sports-dashboard?tab=liabilities&success=Liability+resolved+and+student+cleared";
    }

    // =========================================================
    // --- LIBRARY DASHBOARD (TRANSACTION LOGGER) ---
    // =========================================================

    @GetMapping("/library-dashboard")
    public String showLibraryDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleName() == null || 
           (!user.getRoleName().trim().toUpperCase().contains("LIBRARY") && 
            !user.getRoleName().trim().toUpperCase().contains("LIBRARIAN"))) {
            return "redirect:/login";
        }

        // 1. Fetch Active Borrows (Reusing BorrowRecord with "LIBRARY_BORROWED" tag)
        List<BorrowRecord> activeBorrows = borrowRecordRepository.findAll().stream()
                .filter(b -> "LIBRARY_BORROWED".equalsIgnoreCase(b.getStatus()))
                .collect(Collectors.toList());
        model.addAttribute("activeBorrows", activeBorrows);

        // 2. Fetch Unresolved Liabilities (Filters general liabilities specifically for Library)
        List<Liability> unresolved = liabilityRepository.findAll().stream()
                .filter(l -> "PENDING".equalsIgnoreCase(l.getStatus()) && l.getTitle() != null && 
                            (l.getTitle().toUpperCase().contains("LIBRARY") || l.getTitle().toUpperCase().contains("BOOK")))
                .collect(Collectors.toList());
        model.addAttribute("libraryLiabilities", unresolved);

        return "library-dashboard"; 
    }

    @PostMapping("/library/borrow/add")
    public String addLibraryBorrow(@RequestParam("studentLrn") String studentLrn, @RequestParam("title") String title) {
        try {
            java.util.Optional<Student> studentOpt = studentRepository.findById(studentLrn);
            if (studentOpt.isEmpty()) return "redirect:/library-dashboard?tab=borrowed&error=Student+LRN+not+found";

            // Log borrow directly without checking inventory
            BorrowRecord record = new BorrowRecord();
            record.setStudentLrn(studentLrn);
            record.setItemName(title); 
            record.setBorrowedAt(java.time.LocalDateTime.now());
            record.setStatus("LIBRARY_BORROWED");
            
            // Saves student name if your model requires it
            if(studentOpt.isPresent()){
                record.setStudentName(studentOpt.get().getName());
            }
            borrowRecordRepository.save(record);

            // Lock student's clearance
            Student s = studentOpt.get();
            s.setLibraryClearance("PENDING");
            studentRepository.save(s);

            return "redirect:/library-dashboard?tab=borrowed&success=Book+loan+recorded";
        } catch (Exception e) {
            return "redirect:/library-dashboard?tab=borrowed&error=Failed+to+record+loan";
        }
    }

    @PostMapping("/library/borrow/return")
    public String returnLibraryBorrow(@RequestParam("id") Long id) {
        java.util.Optional<BorrowRecord> opt = borrowRecordRepository.findById(id);
        if (opt.isPresent()) {
            BorrowRecord record = opt.get();
            record.setStatus("LIBRARY_RETURNED");
            borrowRecordRepository.save(record);
        }
        return "redirect:/library-dashboard?tab=borrowed&success=Book+marked+as+returned";
    }

    @PostMapping("/library/liability/add")
    public String addLibraryLiability(@RequestParam("studentLrn") String studentLrn, @RequestParam("description") String description) {
        try {
            java.util.Optional<Student> studentOpt = studentRepository.findById(studentLrn);
            if (studentOpt.isEmpty()) return "redirect:/library-dashboard?tab=liabilities&error=Student+LRN+not+found";

            Liability liability = new Liability();
            liability.setStudent(studentOpt.get());
            liability.setTitle("Library Book Liability");
            liability.setDescription(description);
            liability.setStatus("PENDING");
            liability.setCreatedAt(java.time.LocalDateTime.now());
            liabilityRepository.save(liability);

            return "redirect:/library-dashboard?tab=liabilities&success=Liability+recorded+successfully";
        } catch (Exception e) {
            return "redirect:/library-dashboard?tab=liabilities&error=Failed+to+record+liability";
        }
    }

    @PostMapping("/library/liability/clear")
    public String clearLibraryLiability(@RequestParam("liabilityId") Long liabilityId) {
        java.util.Optional<com.linhs.portal.model.Liability> opt = liabilityRepository.findById(liabilityId);
        if (opt.isPresent()) {
            com.linhs.portal.model.Liability liability = opt.get();
            liability.setStatus("CLEARED");
            liabilityRepository.save(liability);
            
            Student s = liability.getStudent();
            if (s != null) {
                s.setLibraryClearance("CLEARED");
                studentRepository.save(s);
            }
        }
        return "redirect:/library-dashboard?tab=liabilities&success=Liability+resolved+and+student+cleared";
    }

    // =========================================================
    // --- BACKGROUND PROCESSING COMPONENTS ---
    // =========================================================

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