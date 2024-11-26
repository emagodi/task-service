package za.co.datacentrix.taskservice.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import za.co.datacentrix.taskservice.enums.Role;


import java.util.Collection;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User implements UserDetails { // make our app User a spring security User
/*
    we have two options : implements the UserDetails interface or create a user class that extends User spring class which also
    implements UserDetails
 */
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "The name field can't be blank")
    private String firstname;

    @NotBlank(message = "The lastname field can't be blank")
    private String lastname;

    @NotBlank(message = "The lastname field can't be blank")
    @Column(unique = true)
    @Email(message = "Please enter proper email format!")
    private String email;

    @NotBlank(message = "The password field can't be blank")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Role role;

    @OneToOne(mappedBy = "user")
    private ForgotPassword forgotPassword;

    private boolean temporaryPassword;

    private Long departmentId;

    @NotBlank(message = "The user number field can't be blank")
    @Column(unique = true)
    private String dcNumber;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
