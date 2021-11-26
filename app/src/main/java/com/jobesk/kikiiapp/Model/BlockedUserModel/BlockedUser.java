package com.jobesk.kikiiapp.Model.BlockedUserModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sheraz Ahmed on 1/18/2021.
 * sherazbhutta07@gmail.com
 */
public class BlockedUser {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("phone_verified")
    @Expose
    private Integer phoneVerified;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("upgraded")
    @Expose
    private Integer upgraded;
    @SerializedName("incognito")
    @Expose
    private Integer incognito;
    @SerializedName("show_location")
    @Expose
    private Integer showLocation;
    @SerializedName("gender_identity")
    @Expose
    private String genderIdentity;
    @SerializedName("sexual_identity")
    @Expose
    private String sexualIdentity;
    @SerializedName("pronouns")
    @Expose
    private String pronouns;
    @SerializedName("bio")
    @Expose
    private String bio;
    @SerializedName("relationship_status")
    @Expose
    private String relationshipStatus;
    @SerializedName("height")
    @Expose
    private String height;
    @SerializedName("looking_for")
    @Expose
    private String lookingFor;
    @SerializedName("drink")
    @Expose
    private String drink;
    @SerializedName("smoke")
    @Expose
    private String smoke;
    @SerializedName("cannabis")
    @Expose
    private String cannabis;
    @SerializedName("political_views")
    @Expose
    private String politicalViews;
    @SerializedName("religion")
    @Expose
    private String religion;
    @SerializedName("diet_like")
    @Expose
    private String dietLike;
    @SerializedName("sign")
    @Expose
    private String sign;
    @SerializedName("pets")
    @Expose
    private String pets;
    @SerializedName("kids")
    @Expose
    private String kids;
    @SerializedName("last_online")
    @Expose
    private String lastOnline;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("auth_token")
    @Expose
    private String authToken;
    @SerializedName("device_token")
    @Expose
    private String deviceToken;
    @SerializedName("device_type")
    @Expose
    private String deviceType;
    @SerializedName("status")
    @Expose
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Integer phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Integer getUpgraded() {
        return upgraded;
    }

    public void setUpgraded(Integer upgraded) {
        this.upgraded = upgraded;
    }

    public Integer getIncognito() {
        return incognito;
    }

    public void setIncognito(Integer incognito) {
        this.incognito = incognito;
    }

    public Integer getShowLocation() {
        return showLocation;
    }

    public void setShowLocation(Integer showLocation) {
        this.showLocation = showLocation;
    }

    public String getGenderIdentity() {
        return genderIdentity;
    }

    public void setGenderIdentity(String genderIdentity) {
        this.genderIdentity = genderIdentity;
    }

    public String getSexualIdentity() {
        return sexualIdentity;
    }

    public void setSexualIdentity(String sexualIdentity) {
        this.sexualIdentity = sexualIdentity;
    }

    public String getPronouns() {
        return pronouns;
    }

    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getRelationshipStatus() {
        return relationshipStatus;
    }

    public void setRelationshipStatus(String relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getLookingFor() {
        return lookingFor;
    }

    public void setLookingFor(String lookingFor) {
        this.lookingFor = lookingFor;
    }

    public String getDrink() {
        return drink;
    }

    public void setDrink(String drink) {
        this.drink = drink;
    }

    public String getSmoke() {
        return smoke;
    }

    public void setSmoke(String smoke) {
        this.smoke = smoke;
    }

    public String getCannabis() {
        return cannabis;
    }

    public void setCannabis(String cannabis) {
        this.cannabis = cannabis;
    }

    public String getPoliticalViews() {
        return politicalViews;
    }

    public void setPoliticalViews(String politicalViews) {
        this.politicalViews = politicalViews;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getDietLike() {
        return dietLike;
    }

    public void setDietLike(String dietLike) {
        this.dietLike = dietLike;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPets() {
        return pets;
    }

    public void setPets(String pets) {
        this.pets = pets;
    }

    public String getKids() {
        return kids;
    }

    public void setKids(String kids) {
        this.kids = kids;
    }

    public String getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(String lastOnline) {
        this.lastOnline = lastOnline;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
