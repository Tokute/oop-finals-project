public abstract class User {
    private String name;
    private String password;
    private String id;

    public User(String name, String password, String id) {
        this.name = name;
        this.password = password;
        this.id = id;
    }

    public User() {
        this.name = "Unnamed";
        this.password = "no password";
        this.id = "000";
    }

    public void setName(String name, User requester) {
        if (requester instanceof Admin || requester == this) {
            this.name = name;
        }
    }

    public void setPassword(String password, User requester) {
        if (requester instanceof Admin || requester == this) {
            this.password = password;
        }
    }

    public void setId(String id, User requester) {
        if (requester instanceof Admin || requester == this) {
            this.id = id;
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword(User requestor) {
        if (requestor instanceof Admin || requestor == this)
            return this.password;
        return "\0";
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void printDetails() {
        System.out.printf("Name:    %s\nID:     %s\n", this.name, this.id);
    }
}
