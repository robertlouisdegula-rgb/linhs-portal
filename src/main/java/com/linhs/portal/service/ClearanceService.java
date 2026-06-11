package com.linhs.portal.service;

import com.linhs.portal.model.BorrowRecord;
import com.linhs.portal.model.FacilityLog;
import com.linhs.portal.model.GuidanceLog;
import com.linhs.portal.repository.BorrowRecordRepository;
import com.linhs.portal.repository.FacilityLogRepository;
import com.linhs.portal.repository.GuidanceLogRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClearanceService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final GuidanceLogRepository guidanceLogRepository;
    private final FacilityLogRepository facilityLogRepository;

    public ClearanceService(BorrowRecordRepository borrowRecordRepository, 
                            GuidanceLogRepository guidanceLogRepository,
                            FacilityLogRepository facilityLogRepository) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.guidanceLogRepository = guidanceLogRepository;
        this.facilityLogRepository = facilityLogRepository;
    }

    /**
     * Aggregates active student financial, material, or behavioral liabilities.
     * Maps across the unified student tracking schemas.
     */
    public List<String> calculateLiabilities(String lrn) {
        List<String> liabilitiesList = new ArrayList<>();

        // 1. Check General Asset & Lab Borrowing Liabilities
        List<BorrowRecord> unreturnedAssets = borrowRecordRepository.findByStudentLrnAndStatus(lrn, "BORROWED");
        for (BorrowRecord record : unreturnedAssets) {
            liabilitiesList.add(String.format("Unreturned item: '%s' (Borrowed: %s)", 
                record.getItemName(), record.getBorrowedAt()));
        }

        // 2. Check Guidance Behavioral/Incident Cases (Now strictly checks for "UNSOLVED")
        List<GuidanceLog> infractions = guidanceLogRepository.findByLrnAndStatus(lrn, "UNSOLVED");
        for (GuidanceLog incident : infractions) {
            liabilitiesList.add(String.format("Unresolved Guidance Case: %s (Logged: %s)", 
                incident.getIncident(), incident.getDateLogged()));
        }

        // 3. Check Facilities Damages (Added to match new dashboard architecture)
        List<FacilityLog> damages = facilityLogRepository.findByLrnAndStatus(lrn, "UNSOLVED");
        for (FacilityLog damage : damages) {
            liabilitiesList.add(String.format("Unpaid Facility Damage: %s - %s (Logged: %s)", 
                damage.getFacility(), damage.getDescription(), damage.getDateLogged()));
        }

        return liabilitiesList;
    }
}