package projects;

import projects.exception.DbConnection;

public class Main {
    public static void main(String[] args) {
        DbConnection.getConnection();
    }
}
