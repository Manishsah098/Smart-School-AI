package com.smartschool.model;

import java.time.LocalDateTime;

public class Parent {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String occupation;
    private LocalDateTime createdAt;

    public Parent() {}

    public Long getId()                     { return id; }
    public void setId(Long id)              { this.id = id; }
    public Long getUserId()                 { return userId; }
    public void setUserId(Long userId)      { this.userId = userId; }
    public String getName()                 { return name; }
    public void setName(String name)        { this.name = name; }
    public String getEmail()                { return email; }
    public void setEmail(String email)      { this.email = email; }
    public String getPhone()                { return phone; }
    public void setPhone(String phone)      { this.phone = phone; }
    public String getAddress()              { return address; }
    public void setAddress(String address)  { this.address = address; }
    public String getOccupation()           { return occupation; }
    public void setOccupation(String o)     { this.occupation = o; }
    public LocalDateTime getCreatedAt()     { return createdAt; }
    public void setCreatedAt(LocalDateTime c){ this.createdAt = c; }
}
