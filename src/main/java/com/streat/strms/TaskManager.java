package com.streat.strms;

import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private static HashMap<Integer, User> users = new HashMap<>();

    public static void load_data() {
        List<User> raw_users = FileHandler.load("users.txt", User::fromLine);

        for (User user: raw_users) {
            users.put(user.get_id(), user);
        }
    }

    private static Integer get_user_id_by_email(String email) {
        for (User user: users.values()) {
            if (user.get_email().equals(email)) {
                return user.get_id();
            }
        }

        return -1;
    }

    public static boolean check_user_credentials(String email, String password) {
        Integer id = get_user_id_by_email(email);

        return id != - 1 && users.get(id).check_password(password);
    }
}
