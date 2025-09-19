

import org.example.MappingUtil;
import org.example.User;
import org.example.UserDto;
import org.example.UserRepository;
import org.example.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MappingUtil mappingUtil;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto userDto1;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe", "john@example.com");
        testUser.setId(1L);


         userDto1 = new UserDto("John Doe", "john@example.com");
         userDto1.setId(1L);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        User user2 = new User("Jane Smith", "jane@example.com");
        user2.setId(2L);

        UserDto userDto2 = new UserDto("Jane Smith", "jane@example.com");

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));
        when(mappingUtil.mapToUserDto(testUser)).thenReturn(userDto1);
        when(mappingUtil.mapToUserDto(user2)).thenReturn(userDto2);

        // Act
        List<UserDto> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Smith", result.get(1).getName());

        verify(userRepository, times(1)).findAll();
        verify(mappingUtil, times(2)).mapToUserDto(any(User.class));
    }

    @Test
    void getAllUsers_WhenNoUsers_ShouldReturnEmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of());

        // Act
        List<UserDto> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
        verify(mappingUtil, never()).mapToUserDto(any(User.class));
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(mappingUtil.mapToUserDto(testUser)).thenReturn(userDto1);

        // Act
        Optional<UserDto> result = userService.getUserById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        assertEquals("john@example.com", result.get().getEmail());

        verify(userRepository, times(1)).findById(1L);
        verify(mappingUtil, times(1)).mapToUserDto(testUser);
    }


    @Test
    void createUser_WithValidData_ShouldCreateUser() {
        // Arrange
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(mappingUtil.mapToUser(userDto1)).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(mappingUtil.mapToUserDto(testUser)).thenReturn(userDto1);

        // Act
        UserDto result = userService.createUser(testUser);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());

        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(userRepository, times(1)).save(testUser);
        verify(mappingUtil, times(1)).mapToUserDto(testUser);
    }

    @Test
    void updateUser_WhenUserExists_ShouldUpdateUser() {

        UserDto updatedUserDto = new UserDto("John Updated", "john.updated@example.com");
        User updatedUser = new User("John Updated", "john.updated@example.com");
        updatedUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(mappingUtil.mapToUserDto(updatedUser)).thenReturn(updatedUserDto);

        Optional<User> result = userService.updateUser(1L, updatedUserDto);


        assertTrue(result.isPresent());
        assertEquals("John Updated", result.get().getName());
        assertEquals("john.updated@example.com", result.get().getEmail());

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
        verify(mappingUtil, times(1)).mapToUserDto(updatedUser);
    }

    @Test
    void deleteUser_WhenUserExists_ShouldReturnTrue() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        boolean result = userService.deleteUser(1L);


        assertTrue(result);
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_WhenUserNotExists_ShouldReturnFalse() {

        when(userRepository.existsById(999L)).thenReturn(false);


        boolean result = userService.deleteUser(999L);

        assertFalse(result);
        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }
}