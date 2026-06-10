package com.rentafriend.backend.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_accounts")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String legalName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String country;

    private Double latitude;

    private Double longitude;

    @Column(nullable = false)
    private boolean locationConsentGranted;

    private LocalDateTime locationCapturedAt;

    @Column(length = 400)
    private String emotionalGoal;

    private String preferredSupportStyle;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String governmentIdType;

    @Column(nullable = false, length = 4)
    private String governmentIdLastFour;

    @Column(nullable = false)
    private String emergencyContactName;

    @Column(nullable = false)
    private String emergencyContactPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus;

    @Column(nullable = false)
    private boolean emailVerified;

    @Column(nullable = false)
    private boolean phoneVerified;

    @Column(nullable = false)
    private String termsVersion;

    @Column(nullable = false)
    private LocalDateTime termsAcceptedAt;

    @Column(nullable = false)
    private LocalDateTime safetyPolicyAcceptedAt;

    @Column(nullable = false)
    private LocalDateTime physicalBoundaryAcceptedAt;

    @Column(nullable = false)
    private LocalDateTime verificationSubmittedAt;

    @Column(length = 1200)
    private String verificationNotes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public boolean isLocationConsentGranted() {
        return locationConsentGranted;
    }

    public void setLocationConsentGranted(boolean locationConsentGranted) {
        this.locationConsentGranted = locationConsentGranted;
    }

    public LocalDateTime getLocationCapturedAt() {
        return locationCapturedAt;
    }

    public void setLocationCapturedAt(LocalDateTime locationCapturedAt) {
        this.locationCapturedAt = locationCapturedAt;
    }

    public String getEmotionalGoal() {
        return emotionalGoal;
    }

    public void setEmotionalGoal(String emotionalGoal) {
        this.emotionalGoal = emotionalGoal;
    }

    public String getPreferredSupportStyle() {
        return preferredSupportStyle;
    }

    public void setPreferredSupportStyle(String preferredSupportStyle) {
        this.preferredSupportStyle = preferredSupportStyle;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGovernmentIdType() {
        return governmentIdType;
    }

    public void setGovernmentIdType(String governmentIdType) {
        this.governmentIdType = governmentIdType;
    }

    public String getGovernmentIdLastFour() {
        return governmentIdLastFour;
    }

    public void setGovernmentIdLastFour(String governmentIdLastFour) {
        this.governmentIdLastFour = governmentIdLastFour;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public String getTermsVersion() {
        return termsVersion;
    }

    public void setTermsVersion(String termsVersion) {
        this.termsVersion = termsVersion;
    }

    public LocalDateTime getTermsAcceptedAt() {
        return termsAcceptedAt;
    }

    public void setTermsAcceptedAt(LocalDateTime termsAcceptedAt) {
        this.termsAcceptedAt = termsAcceptedAt;
    }

    public LocalDateTime getSafetyPolicyAcceptedAt() {
        return safetyPolicyAcceptedAt;
    }

    public void setSafetyPolicyAcceptedAt(LocalDateTime safetyPolicyAcceptedAt) {
        this.safetyPolicyAcceptedAt = safetyPolicyAcceptedAt;
    }

    public LocalDateTime getPhysicalBoundaryAcceptedAt() {
        return physicalBoundaryAcceptedAt;
    }

    public void setPhysicalBoundaryAcceptedAt(LocalDateTime physicalBoundaryAcceptedAt) {
        this.physicalBoundaryAcceptedAt = physicalBoundaryAcceptedAt;
    }

    public LocalDateTime getVerificationSubmittedAt() {
        return verificationSubmittedAt;
    }

    public void setVerificationSubmittedAt(LocalDateTime verificationSubmittedAt) {
        this.verificationSubmittedAt = verificationSubmittedAt;
    }

    public String getVerificationNotes() {
        return verificationNotes;
    }

    public void setVerificationNotes(String verificationNotes) {
        this.verificationNotes = verificationNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

