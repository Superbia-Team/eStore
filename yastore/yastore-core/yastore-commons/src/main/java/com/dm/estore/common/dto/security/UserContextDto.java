package com.dm.estore.common.dto.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserContextDto implements Serializable {

    private static final long serialVersionUID = 8676736290286309436L;

    private String userName;
    
    private String firstName, lastName;

    private boolean authenticated = Boolean.FALSE;

    private List<String> roles = new ArrayList<String>();

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
