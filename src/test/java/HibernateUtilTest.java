

import org.example.HibernateUtil;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HibernateUtilTest {

    @BeforeAll
    static void setUp() {
        // Можно использовать test configuration
        System.setProperty("hibernate.config.file", "hibernate.cfg.xml");
    }

    @Test
    void testSessionFactoryCreation() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

        assertNotNull(sessionFactory, "SessionFactory should not be null");
        assertFalse(sessionFactory.isClosed(), "SessionFactory should be open");
    }

    @Test
    void testSessionFactorySingleton() {
        SessionFactory firstInstance = HibernateUtil.getSessionFactory();
        SessionFactory secondInstance = HibernateUtil.getSessionFactory();

        assertSame(firstInstance, secondInstance, "Should return same SessionFactory instance");
    }

    @Test
    void testShutdown() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        HibernateUtil.shutdown();

        assertTrue(sessionFactory.isClosed(), "SessionFactory should be closed after shutdown");
    }

    @AfterAll
    static void tearDown() {
        HibernateUtil.shutdown();
    }
}