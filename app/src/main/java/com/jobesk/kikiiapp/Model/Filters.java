
package com.jobesk.kikiiapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Filters {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("distance")
    @Expose
    private String distance;

    @SerializedName("distance_in")
    @Expose
    private String distanceIn;
    @SerializedName("height")
    @Expose
    private String height;
    @SerializedName("gender_identity")
    @Expose
    private Object genderIdentity;
    @SerializedName("sexual_identity")
    @Expose
    private Object sexualIdentity;
    @SerializedName("pronouns")
    @Expose
    private Object pronouns;
    @SerializedName("relationship_status")
    @Expose
    private Object relationshipStatus;
    @SerializedName("diet_like")
    @Expose
    private Object dietLike;
    @SerializedName("sign")
    @Expose
    private Object sign;
    @SerializedName("looking_for")
    @Expose
    private Object lookingFor;
    @SerializedName("drink")
    @Expose
    private Object drink;
    @SerializedName("cannabis")
    @Expose
    private Object cannabis;
    @SerializedName("political_views")
    @Expose
    private Object politicalViews;
    @SerializedName("religion")
    @Expose
    private Object religion;
    @SerializedName("pets")
    @Expose
    private Object pets;
    @SerializedName("kids")
    @Expose
    private Object kids;
    @SerializedName("smoke")
    @Expose
    private Object smoke;
    @SerializedName("last_online")
    @Expose
    private Object lastOnline;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("from_age")
    @Expose
    private String fromAge;
    @SerializedName("to_age")
    @Expose
    private String toAge;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDistanceIn() {
        return distanceIn;
    }

    public void setDistanceIn(String distanceIn) {
        this.distanceIn = distanceIn;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public Object getGenderIdentity() {
        return genderIdentity;
    }

    public void setGenderIdentity(Object genderIdentity) {
        this.genderIdentity = genderIdentity;
    }

    public Object getSexualIdentity() {
        return sexualIdentity;
    }

    public void setSexualIdentity(Object sexualIdentity) {
        this.sexualIdentity = sexualIdentity;
    }

    public Object getPronouns() {
        return pronouns;
    }

    public void setPronouns(Object pronouns) {
        this.pronouns = pronouns;
    }

    public Object getRelationshipStatus() {
        return relationshipStatus;
    }

    public void setRelationshipStatus(Object relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public Object getDietLike() {
        return dietLike;
    }

    public void setDietLike(Object dietLike) {
        this.dietLike = dietLike;
    }

    public Object getSign() {
        return sign;
    }

    public void setSign(Object sign) {
        this.sign = sign;
    }

    public Object getLookingFor() {
        return lookingFor;
    }

    public void setLookingFor(Object lookingFor) {
        this.lookingFor = lookingFor;
    }

    public Object getDrink() {
        return drink;
    }

    public void setDrink(Object drink) {
        this.drink = drink;
    }

    public Object getCannabis() {
        return cannabis;
    }

    public void setCannabis(Object cannabis) {
        this.cannabis = cannabis;
    }

    public Object getPoliticalViews() {
        return politicalViews;
    }

    public void setPoliticalViews(Object politicalViews) {
        this.politicalViews = politicalViews;
    }

    public Object getReligion() {
        return religion;
    }

    public void setReligion(Object religion) {
        this.religion = religion;
    }

    public Object getPets() {
        return pets;
    }

    public void setPets(Object pets) {
        this.pets = pets;
    }

    public Object getKids() {
        return kids;
    }

    public void setKids(Object kids) {
        this.kids = kids;
    }

    public Object getSmoke() {
        return smoke;
    }

    public void setSmoke(Object smoke) {
        this.smoke = smoke;
    }

    public Object getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Object lastOnline) {
        this.lastOnline = lastOnline;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFromAge() {
        return fromAge;
    }

    public void setFromAge(String fromAge) {
        this.fromAge = fromAge;
    }

    public String getToAge() {
        return toAge;
    }

    public void setToAge(String toAge) {
        this.toAge = toAge;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
