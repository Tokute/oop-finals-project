package users;

public class Admin extends User {
    public Admin(String name, String password, String id) {
        super(name, password, id);
    }

    @Override
    public void printDetails() {
        System.out.println("Admin:");
        super.printDetails();
    }
}