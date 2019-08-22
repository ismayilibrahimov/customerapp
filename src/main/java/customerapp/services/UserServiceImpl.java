package customerapp.services;

import customerapp.models.Role;
import customerapp.models.User;
import customerapp.repositories.RoleRepository;
import customerapp.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public boolean create(User user) throws Exception {
        if (checkUsername(user.getUsername())) {
            throw new Exception("Username is not available!");
        }

        if (roleRepository.findById(2L).isPresent()) {
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findById(2L).get());
            user.setRoles(roles);
        }

        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean update(User user, long id, String isAdmin) {
        if (userRepository.findById(id).isPresent()) {
            User userModel = userRepository.findById(id).get();
            userModel.setFullname(user.getFullname());
            userModel.setAge(user.getAge());
            userModel.setGender(user.getGender());
            if (isAdmin != null) {
                if (roleRepository.findById(1L).isPresent()) {
                    userModel.getRoles().add(roleRepository.findById(1L).get());
                }
            } else {
                if (roleRepository.findById(1L).isPresent()) {
                    userModel.getRoles().remove(roleRepository.findById(1L).get());
                }
            }

            userRepository.save(userModel);
            return true;
        }
        return false;
    }

    private boolean checkUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }
}
