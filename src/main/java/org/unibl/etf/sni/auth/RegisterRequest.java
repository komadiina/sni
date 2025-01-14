package org.unibl.etf.sni.auth;

import org.unibl.etf.sni.model.Role;
import org.unibl.etf.sni.model.User;

public class RegisterRequest {
    private String username;
    private String password;
    private String passwordConfirmation;
    private String email;
    private Boolean active;
    private String firstName;
    private String lastName;
    private String contactPhone;
    private String billingAddress;
    private Role role;

    public RegisterRequest(String username, String password, String passwordConfirmation, String email, Boolean active, String firstName, String lastName, String contactPhone, String billingAddress, Role role) {
        this.username = username;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.email = email;
        this.active = true;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactPhone = contactPhone;
        this.billingAddress = billingAddress;
        this.role = Role.USER;
    }

    public RegisterRequest() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public User formUser() {
        return new User(username, password, email, active, firstName, lastName, contactPhone, billingAddress, Role.USER);
    }
}
