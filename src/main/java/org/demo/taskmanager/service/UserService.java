package org.demo.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.demo.taskmanager.model.User;
import org.demo.taskmanager.repository.UserRepository;
import org.demo.taskmanager.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service pour la gestion des utilisateurs (principalement côté Admin).
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));
        userRepository.delete(user);
    }
}
