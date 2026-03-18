package com.streat.strms;

public class User {
    private final Integer id;
    private final String name;
    private final String email;
    private final String password;

    private static int current_id = 0;

    public User(Integer id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;

        if (this.id > current_id) {
            current_id = this.id + 1;
        }
    }

    public User(String name, String email, String password) {
        this.id = current_id;
        this.name = name;
        this.email = email;
        this.password = PasswordUtils.hash(password);

        current_id++;
    }

    public Integer get_id() {
        return this.id;
    }

    public String get_email() {
        return this.email;
    }

    public static User fromLine(String line) {
        String[] parts = line.split(";");

        return new User(
                Integer.parseInt(parts[0]),
                parts[1],
                parts[2],
                parts[3]
        );
    }

    public boolean check_password(String password) {
        return PasswordUtils.verify(password, this.password);
    }
}
