
import org.example.User;
import org.example.HibernateUtil;
import org.example.UserDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDAOTest {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Transaction transaction;

    @Mock
    private Query<User> query;

    private UserDAO userDAO;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Подменяем SessionFactory в HibernateUtil
        HibernateUtil.setSessionFactoryForTesting(sessionFactory);
        userDAO = new UserDAO();

        testUser = new User("Test User", "test@example.com");
        testUser.setId(1L);
    }

    @Test
    void testSaveUserSuccess() {
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);

        userDAO.saveUser(testUser);

        verify(session, times(1)).save(testUser);
        verify(transaction, times(1)).commit();
        verify(session, times(1)).close();
    }

    @Test
    void testSaveUserWithException() {
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException("Database error")).when(session).save(any(User.class));

        assertDoesNotThrow(() -> userDAO.saveUser(testUser));

        verify(transaction, times(1)).rollback();
        verify(session, times(1)).close();
    }

    @Test
    void testGetUserByIdFound() {
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.get(User.class, 1L)).thenReturn(testUser);

        User result = userDAO.getUserById(1L);

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        verify(session, times(1)).close();
    }

    @Test
    void testGetUserByIdNotFound() {
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.get(User.class, 999L)).thenReturn(null);

        User result = userDAO.getUserById(999L);

        assertNull(result);
        verify(session, times(1)).close();
    }

    @Test
    void testGetAllUsers() {
        List<User> expectedUsers = Arrays.asList(testUser, new User("User2", "user2@example.com"));

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(User.class))).thenReturn(query);
        when(query.list()).thenReturn(expectedUsers);

        List<User> result = userDAO.getAllUsers();

        assertEquals(2, result.size());
        verify(session, times(1)).close();
    }

    @Test
    void testUpdateUserSuccess() {
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);

        userDAO.updateUser(testUser);

        verify(session, times(1)).update(testUser);
        verify(transaction, times(1)).commit();
        verify(session, times(1)).close();
    }

    @Test
    void testDeleteUserExists() {
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.get(User.class, 1L)).thenReturn(testUser);

        userDAO.deleteUser(1L);

        verify(session, times(1)).delete(testUser);
        verify(transaction, times(1)).commit();
        verify(session, times(1)).close();
    }

    @Test
    void testDeleteUserNotExists() {
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.get(User.class, 999L)).thenReturn(null);

        userDAO.deleteUser(999L);

        verify(session, never()).delete(any());
        verify(transaction, times(1)).commit();
        verify(session, times(1)).close();
    }
}