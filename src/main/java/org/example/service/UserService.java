package org.example.service;


import org.springframework.transaction.annotation.Transactional;
import org.example.MappingUtil;
import org.example.User;
import org.example.UserRepository;
import org.example.dto.UserDto;
import org.example.dto.UserEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final MappingUtil mappingUtil;
    private final UserRepository userRepository;
    private KafkaProducerService kafkaProducerService;

    @Autowired
    public UserService(MappingUtil mappingUtil, UserRepository userRepository, KafkaProducerService kafkaProducerService) {
        this.mappingUtil = mappingUtil;
        this.userRepository = userRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    public List<UserDto> getAllUsers() {

        return userRepository.findAll().stream()
                .map(mappingUtil::mapToUserDto)
                .collect(Collectors.toList());
    }

    public  Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id).map(mappingUtil::mapToUserDto);
    }
    @Transactional
    public UserDto createUser(User user) throws DataIntegrityViolationException{
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists: " + user.getEmail());
        }

        User savedUser = userRepository.save(user);
        UserEvent userEvent = new UserEvent("USER_CREATED", savedUser.getEmail(), savedUser.getName());
        kafkaProducerService.sendUserEvent(userEvent);
        return mappingUtil.mapToUserDto(savedUser);
    }

    public Optional<User> updateUser(Long id, UserDto userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(userDetails.getName());
                    user.setEmail(userDetails.getEmail());
                    return userRepository.save(user);
                });
    }

    @Transactional
    public boolean deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            userRepository.deleteById(id);

            // Send Kafka event
            UserEvent userEvent = new UserEvent("USER_DELETED", user.getEmail(), user.getName());
            kafkaProducerService.sendUserEvent(userEvent);

            return true;
        }
        return false;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
