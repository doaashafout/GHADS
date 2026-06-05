package models;

public class AidDistribution {

    private int distributionId;
    private int familyId;
    private int orgId;
    private int distributedBy;
    private String distributionDate;
    private String aidType;

    private String familyName;
    private String familyVulnerability;
    private String orgName;

    public AidDistribution() {}

    public AidDistribution(int familyId, int orgId, int distributedBy, String distributionDate, String aidType) {
        this.familyId = familyId;
        this.orgId = orgId;
        this.distributedBy = distributedBy;
        this.distributionDate = distributionDate;
        this.aidType = aidType;
    }

    public AidDistribution(int distributionId, int familyId, int orgId, int distributedBy,
                           String distributionDate, String aidType) {
        this.distributionId = distributionId;
        this.familyId = familyId;
        this.orgId = orgId;
        this.distributedBy = distributedBy;
        this.distributionDate = distributionDate;
        this.aidType = aidType;
    }

    public int getDistributionId() { return distributionId; }
    public void setDistributionId(int distributionId) { this.distributionId = distributionId; }

    public int getFamilyId() { return familyId; }
    public void setFamilyId(int familyId) { this.familyId = familyId; }

    public int getOrgId() { return orgId; }
    public void setOrgId(int orgId) { this.orgId = orgId; }

    public int getDistributedBy() { return distributedBy; }
    public void setDistributedBy(int distributedBy) { this.distributedBy = distributedBy; }

    public String getDistributionDate() { return distributionDate; }
    public void setDistributionDate(String distributionDate) { this.distributionDate = distributionDate; }

    public String getAidType() { return aidType; }
    public void setAidType(String aidType) { this.aidType = aidType; }

    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }

    public String getFamilyVulnerability() { return familyVulnerability; }
    public void setFamilyVulnerability(String familyVulnerability) { this.familyVulnerability = familyVulnerability; }

    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }

    @Override
    public String toString() {
        return "Distribution #" + distributionId;
    }
}
