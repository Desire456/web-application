package org.netcracker.students.dao.postrgresql;

import org.netcracker.students.dao.connection.ConnectionBuilder;
import org.netcracker.students.dao.connection.PoolConnectionBuilder;
import org.netcracker.students.dao.interfaces.DAOManager;
import org.netcracker.students.dao.interfaces.JournalDAO;
import org.netcracker.students.dao.interfaces.TasksDAO;
import org.netcracker.students.dao.interfaces.UsersDAO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgreSQLDAOManager implements DAOManager {
    private static PostgreSQLDAOManager instance;
    ConnectionBuilder connectionBuilder;

    private PostgreSQLDAOManager(String path) {
        connectionBuilder = new PoolConnectionBuilder();
        executeSqlStartScript(path);
    }

    private PostgreSQLDAOManager(){
        connectionBuilder = new PoolConnectionBuilder();
        executeSqlStartScript();
    }

    public static PostgreSQLDAOManager getInstance(){
        if (instance == null)
            instance = new PostgreSQLDAOManager();
        return instance;
    }

    public static PostgreSQLDAOManager getInstance(String path) throws SQLException {
        if (instance == null)
            instance = new PostgreSQLDAOManager(path);
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return connectionBuilder.getConnect();
    }

    @Override
    public TasksDAO getTasksDao() throws SQLException {
        return new PostgreSQLTasksDAO(getConnection());
    }

    @Override
    public JournalDAO getJournalDao() throws SQLException {
        return new PostgreSQLJournalDAO(getConnection());
    }

    @Override
    public UsersDAO getUsersDao() throws SQLException {
        return new PostgreSQLUsersDAO(getConnection());
    }

    private void executeSqlStartScript(String path) {
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/Users/andreyryazanov/Documents/labs-cracker/task-manager-web-app/src/main/java/org/netcracker/students/dao/script.sql"));
            String line;
            while((line = bufferedReader.readLine()) != null){
                Statement statement = connectionBuilder.getConnect().createStatement();
                statement.execute(line);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void executeSqlStartScript() {
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/Users/andreyryazanov/Documents/labs-cracker/task-manager-web-app/src/main/java/org/netcracker/students/dao/script.sql"));
            String line;
            while((line = bufferedReader.readLine()) != null){
                Statement statement = connectionBuilder.getConnect().createStatement();
                statement.execute(line);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}