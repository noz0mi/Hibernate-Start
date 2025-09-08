package org.example;

public class Main {
    public static void main(String[] args) {

        UserDAO userDAO = new UserDAO();

        // Создание пользователя
        User user = new User("John Doe", "john@example.com");
        userDAO.saveUser(user);

        // Получение пользователя
        User retrievedUser = userDAO.getUserById(1L);
        System.out.println("User: " + retrievedUser.getName());

        // Получение всех пользователей
        userDAO.getAllUsers().forEach(u ->
                System.out.println(u.getName() + " - " + u.getEmail()));

        // Обновление пользователя
        retrievedUser.setEmail("newemail@example.com");
        userDAO.updateUser(retrievedUser);

        // Удаление пользователя
        userDAO.deleteUser(1L);

        // Закрытие SessionFactory при завершении
        HibernateUtil.shutdown();
    }
}