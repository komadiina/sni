package org.unibl.etf.sni.service;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unibl.etf.sni.db.UserRepository;
import org.unibl.etf.sni.exceptions.EmailTakenException;
import org.unibl.etf.sni.model.User;

import java.util.List;
import java.util.Optional;

@Service
public class UserService  implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    public User addUser(User user) {
        String email = user.getEmail();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailTakenException(email);
        }

        return userRepository.save(user);
    }

    public void deleteUser(String username) {
        Optional<User> result = userRepository.findByUsername(username);

        if (result.isPresent()) {
            userRepository.delete(result.get());
        } else throw new IllegalStateException("User not found: " + username);
    }

    @Transactional
    public User updateUser(String username, User user) {
        Optional<User> result = userRepository.findByUsername(username);

        if (result.isPresent()) {
            User updatedUser = result.get();

            updatedUser.setFirstName(user.getFirstName());
            updatedUser.setLastName(user.getLastName());
            updatedUser.setEmail(user.getEmail());
            updatedUser.setContactPhone(user.getContactPhone());
            updatedUser.setBillingAddress(user.getBillingAddress());

            return updatedUser;
        } else {
            throw new IllegalStateException("User not found: " + username);
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
