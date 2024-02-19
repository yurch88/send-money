package org.application.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "USER")
public class User {

   @JsonIgnore
   @Id
   @Column(name = "ID")
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SEQ")
   @SequenceGenerator(name = "USER_SEQ", sequenceName = "USER_SEQ", allocationSize = 1)
   private Long id;

   @Column(name = "USERNAME", length = 50, unique = true)
   @NotNull
   @Size(min = 4, max = 50)
   private String username;

   @JsonIgnore
   @Column(name = "PASSWORD", length = 100)
   @NotNull
   @Size(min = 4, max = 100)
   private String password;

   @Column(name = "FIRSTNAME", length = 50)
   @NotNull
   @Size(min = 4, max = 50)
   private String firstname;

   @Column(name = "LASTNAME", length = 50)
   @NotNull
   @Size(min = 4, max = 50)
   private String lastname;

   @Column(name = "EMAIL", length = 50)
   @NotNull
   @Size(min = 4, max = 50)
   private String email;

   @JsonIgnore
   @Column(name = "ACTIVATED")
   @NotNull
   private boolean activated;

   @Column(name = "AMOUNT")
   private Float amount;

   @Column(name = "PHONENUMBER")
   private String phonenumber;

   @Size(min = 4, max = 4)
   @Column(name = "OTP")
   private String OTP;



   @ManyToMany
   @JoinTable(
      name = "USER_AUTHORITY",
      joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
      inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_NAME", referencedColumnName = "NAME")})
   @BatchSize(size = 20)
   private Set<Authority> authorities = new HashSet<>();

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

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

   public String getFirstname() {
      return firstname;
   }

   public void setFirstname(String firstname) {
      this.firstname = firstname;
   }

   public String getLastname() {
      return lastname;
   }

   public void setLastname(String lastname) {
      this.lastname = lastname;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public boolean isActivated() {
      return activated;
   }

   public void setActivated(boolean activated) {
      this.activated = activated;
   }

   public Set<Authority> getAuthorities() {
      return authorities;
   }

   public void setAuthorities(Set<Authority> authorities) {
      this.authorities = authorities;
   }

   public Float getAmount() {
      return amount;
   }

   public void setAmount(Float amount) {
      this.amount = amount;
   }

   public String getOTP() {
      return OTP;
   }

   public void setOTP(String OTP) {
      this.OTP = OTP;
   }

   public String getPhonenumber() {
      return phonenumber;
   }

   public void setPhonenumber(String phonenumber) {
      this.phonenumber = phonenumber;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      User user = (User) o;
      return id.equals(user.id);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id);
   }

   @Override
   public String toString() {
      return "User{" +
              "id=" + id +
              ", username='" + username + '\'' +
              ", password='" + password + '\'' +
              ", firstname='" + firstname + '\'' +
              ", lastname='" + lastname + '\'' +
              ", email='" + email + '\'' +
              ", activated=" + activated +
              ", amount=" + amount +
              ", OTP='" + OTP + '\'' +
              ", phoneNumber='" + phonenumber + '\'' +
              ", authorities=" + authorities +
              '}';
   }
}
