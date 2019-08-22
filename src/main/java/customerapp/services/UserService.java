package customerapp.services;

import customerapp.models.User;

public interface UserService {
    boolean create(User user) throws Exception;
    boolean update(User user, long id, String role);
}
