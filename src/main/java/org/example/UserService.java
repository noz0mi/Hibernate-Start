package org.example;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final MappingUtil mappingUtil;
    private final UserRepository userRepository;

    @Autowired
    public UserService(MappingUtil mappingUtil, UserRepository userRepository) {
        this.mappingUtil = mappingUtil;
        this.userRepository = userRepository;
    }

    public List<UserDto> getAllUsers() {

        return userRepository.findAll().stream()
                .map(mappingUtil::mapToUserDto)
                .collect(Collectors.toList());
    }

    public  Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id).map(mappingUtil::mapToUserDto);
    }

    public UserDto createUser(User user) {
        User savedUser = userRepository.save(user);
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

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
