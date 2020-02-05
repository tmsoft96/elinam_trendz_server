package com.tmsoft.tm.elitrends.Holders;

public class allUserClass {
    private String userId, profilePicture, fullName, phoneNumber;

    public allUserClass() {
    }

    public allUserClass(String userId, String profilePicture, String fullName, String phoneNumber) {
        this.userId = userId;
        this.profilePicture = profilePicture;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
