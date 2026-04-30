public class User {
    private final int id;
    private final int passengerId;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final String salt;

    public User(int id, int passengerId, String username, String email, String passwordHash, String salt) {
        this.id = id;
        this.passengerId = passengerId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    public int getId() {
        return id;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }
}
