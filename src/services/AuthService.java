package services;

import dao.UserDAO;
import models.User;

public class AuthService {

    private UserDAO userDAO;

    public AuthService() {
        userDAO = new UserDAO();
    }

    public User login(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean usernameExists(String username) {
        return userDAO.usernameExists(username);
    }

    public boolean emailExists(String email) {
        return userDAO.emailExists(email);
    }

    public boolean updateUser(User user) {
        return userDAO.updateOne(user);
    }
}
