package services;

import dao.AidDistributionDAO;
import dao.FamilyDAO;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import models.AidDistribution;
import models.Family;

public class DistributionService {

    private AidDistributionDAO distDAO;
    private FamilyDAO familyDAO;

    public DistributionService() {
        distDAO = new AidDistributionDAO();
        familyDAO = new FamilyDAO();
    }

    public String checkDuplicate(int familyId, String aidType, String distributionDate) {
        Family family = familyDAO.findById(familyId);
        if (family == null) return "FAMILY_NOT_FOUND";

        String vuln = family.getVulnerabilityLevel();

        if ("HIGH".equals(vuln)) {
            return "ALLOWED";
        }

        boolean hasRecent = distDAO.hasRecentDistributionByType(familyId, aidType, distributionDate, 30);
        if (hasRecent) {
            AidDistribution last = distDAO.findLatestByFamilyId(familyId);
            return "REJECTED:" + family.getHouseholdName() + ":" + vuln + ":"
                 + (last != null ? last.getOrgName() : "Unknown") + ":"
                 + (last != null ? last.getDistributionDate() : "Unknown");
        }

        return "ALLOWED";
    }

    public boolean recordDistribution(AidDistribution ad) {
        boolean result = distDAO.insertOne(ad);
        if (result) {
            familyDAO.updateLastAidDate(ad.getFamilyId(), ad.getDistributionDate());
        }
        return result;
    }
}
