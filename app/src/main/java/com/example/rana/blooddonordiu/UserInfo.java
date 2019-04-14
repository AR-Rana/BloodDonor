package com.example.rana.blooddonordiu;

public class UserInfo {
    public String fullName;
    public String nickName;
    public String birthDate;
    public String mobileNo;
    public String height;
    public String weight;
    public String email;
    public String area;
    public String address;
    public String bloodGroup;
    public String lastDateOfDonateBlood;
    public String dpLink;
    public String status;
    public String gender;
    public String interestToDonateBlood;
    public String joinDate;

    public UserInfo(){

    }

    public UserInfo(String fullName, String nickName, String birthDate, String mobileNo,
                    String height, String weight, String email, String area,
                    String address, String bloodGroup, String lastDateOfDonateBlood, String dpLink,
                    String status, String gender, String interestToDonateBlood, String joinDate) {
        this.fullName = fullName;
        this.nickName = nickName;
        this.birthDate = birthDate;
        this.mobileNo = mobileNo;
        this.height = height;
        this.weight = weight;
        this.email = email;
        this.area = area;
        this.address = address;
        this.bloodGroup = bloodGroup;
        this.lastDateOfDonateBlood = lastDateOfDonateBlood;
        this.dpLink = dpLink;
        this.status = status;
        this.gender = gender;
        this.interestToDonateBlood = interestToDonateBlood;
        this.joinDate = joinDate;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getLastDateOfDonateBlood() {
        return lastDateOfDonateBlood;
    }

    public void setLastDateOfDonateBlood(String lastDateOfDonateBlood) {
        this.lastDateOfDonateBlood = lastDateOfDonateBlood;
    }

    public String getDpLink() {
        return dpLink;
    }

    public void setDpLink(String dpLink) {
        this.dpLink = dpLink;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getInterestToDonateBlood() {
        return interestToDonateBlood;
    }

    public void setInterestToDonateBlood(String interestToDonateBlood) {
        this.interestToDonateBlood = interestToDonateBlood;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }
}
