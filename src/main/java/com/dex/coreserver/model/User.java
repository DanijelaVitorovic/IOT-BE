package com.dex.coreserver.model;

import com.dex.coreserver.model.enums.Actions;
import com.dex.coreserver.util.ApplicationUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "users",
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"username"}, name = "unq_username")
)
public class User extends AbstractDataModel implements UserDetails {
    @NotBlank(message = "{NotBlank.user.firstName}")
    private String firstName;
    @NotBlank(message = "{NotBlank.user.lastName}")
    private String lastName;
    @NotBlank(message = "{NotBlank.user.username}")
    private String username;
    @Size(min = 8, message = "{Size.user.password}")
    private String password;
    @Transient
    private String confirmPassword;
    private boolean isActive;
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private Date lastLoginDate;
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private Date passwordExpirationDate;

    @NotEmpty(message = "{NotEmpty.user.roles}")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @ToString.Exclude
    private Set<Role> roles = new HashSet<>();

    @NotBlank(message = "{NotBlank.user.email}")
    private String email;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isPasswordExpired(){
        return passwordExpirationDate != null && passwordExpirationDate.before(new Date());
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    private Boolean useGoogle2f = false;
    private String google2FaSecret;
    @Transient
    private Boolean google2FaRequired = true;

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public String getConfirmPassword() {
        return confirmPassword;
    }

    @JsonProperty
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @ElementCollection(targetClass = Actions.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_actions")
    @Column(name = "specialActions")
    List<Actions> specialActions;

    public List<Actions> myActions() {
        if (this.getUsername().equals( ApplicationUtils.ROLE_ADMIN )) return Arrays.asList(Actions.values());
        if (this.getRoles() == null || this.getRoles().isEmpty()) return specialActions;

        List<Actions> myActions = new ArrayList<>(specialActions);
        for (Role role : this.getRoles()) {
            myActions.addAll(role.getActions());
        }
        return myActions;
    }
}
