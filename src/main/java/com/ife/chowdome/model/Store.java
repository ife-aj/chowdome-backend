package com.ife.chowdome.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "stores")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String firstName;
    private String lastName;
    private String storeName;
    private String location;
    private String contactNumber;
    private String email;
    private String openingHours;
    private String closingHours;
    private String description;
    private String imageUrl;
    private String password;

    @OneToMany(mappedBy = "store", cascade= CascadeType.ALL)
    private List<Food> foods; // Assuming a store can have multiple foods

    @ManyToOne
    @JoinColumn(name = "user_id") // This column in the store table refers to the user ID
    private Users user;
    
    // Optional, if you want to store an image URL for the store

    // Default constructor
    public Store() {}

    public Store(Users user, String FirstName,String password, String LastName, String StoreName, String closingHours, String contactNumber, String description, String email, List<Food> foods, long id, String imageUrl, String location, String openingHours) {
        this.firstName = FirstName;
        this.lastName = LastName;
        this.storeName = StoreName;
        this.closingHours = closingHours;
        this.contactNumber = contactNumber;
        this.description = description;
        this.email = email;
        this.foods = foods;
        this.id = id;
        this.imageUrl = imageUrl;
        this.location = location;
        this.openingHours = openingHours;
        this.password = password;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String FirstName) {
        this.firstName = FirstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String LastName) {
        this.lastName = LastName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String StoreName) {
        this.storeName = StoreName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getClosingHours() {
        return closingHours;
    }

    public void setClosingHours(String closingHours) {
        this.closingHours = closingHours;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

}
