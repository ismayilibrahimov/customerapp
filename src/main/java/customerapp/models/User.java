package customerapp.models;

import customerapp.validation.PasswordMatches;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.*;

@Entity
@PasswordMatches(groups = onCreate.class)
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(groups = {onCreate.class, onUpdate.class})
    @Size(min = 3, max = 30, groups = {onCreate.class, onUpdate.class})
    private String fullname;
    @NotNull(groups = {onCreate.class, onUpdate.class})
    @Min(value = 16, groups = {onCreate.class, onUpdate.class})
    @Max(value = 99, groups = {onCreate.class, onUpdate.class})
    private int age;
    @NotNull(groups = {onCreate.class, onUpdate.class})
    @Min(value = 0, groups = {onCreate.class, onUpdate.class})
    @Max(value = 1, groups = {onCreate.class, onUpdate.class})
    private int gender;
    @NotBlank(groups = onCreate.class)
    @Size(min = 3, max = 30, groups = onCreate.class)
    @Column(unique = true)
    private String username;
    @NotBlank(groups = onCreate.class)
    @Size(min = 5, max = 100, groups = onCreate.class)
    private String password;
    @Transient
    @NotBlank(groups = onCreate.class)
    @Size(min = 5, max = 100, groups = onCreate.class)
    private String confirmPassword;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private Set<Role> roles;

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role: this.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }


    public boolean isAdmin() {
        for (GrantedAuthority role: this.getAuthorities()) {
            if ("ROLE_ADMIN".equals(role.getAuthority())) {
                return true;
            }
        }
        return false;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
