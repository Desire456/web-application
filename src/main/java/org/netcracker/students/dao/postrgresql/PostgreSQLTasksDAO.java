package org.netcracker.students.dao.postrgresql;

import org.netcracker.students.dao.interfaces.TasksDAO;
import org.netcracker.students.model.Task;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class PostgreSQLTasksDAO implements TasksDAO {
    private Connection connection;

    public PostgreSQLTasksDAO(Connection connection){
        this.connection = connection;
    }



    @Override
    public Task create(String name, String status, String description, Date plannedDate, Date dateOfDone, Integer journalId) throws SQLException {
        String sql = "INSERT INTO tasks VALUES (?, ?, ?, ?, ?, ?)";
        String RETURN_CREATED_TASK_SQL = "SELECT * FROM tasks WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,journalId);
            preparedStatement.setString(2,name);
            preparedStatement.setString(3,description);
            preparedStatement.setDate(4, plannedDate);
            preparedStatement.setDate(5, dateOfDone);
            preparedStatement.execute();
        }
        try(PreparedStatement preparedStatement = connection.prepareStatement(RETURN_CREATED_TASK_SQL)){
            preparedStatement.setString(1,name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                Task task = new Task(resultSet.getInt(1),resultSet.getInt(2),resultSet.getString(3),
                        resultSet.getString(4), resultSet.getDate(6).toLocalDate().atStartOfDay(),
                        resultSet.getDate(7).toLocalDate().atStartOfDay(),resultSet.getString(5));
                return task;
            }
        }
        return null;
    }

    @Override
    public Task read(int id) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE task_id = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                Task task = new Task(resultSet.getInt(1), resultSet.getInt(2),
                        resultSet.getString(3), resultSet.getString(4),
                        resultSet.getDate(5).toLocalDate().atStartOfDay(),
                        resultSet.getDate(6).toLocalDate().atStartOfDay(), resultSet.getString(7));
                        return task;
            }
        }
        return null;
    }

    @Override
    public void update(Task task) throws SQLException {
        String sql = "UPDATE tasks SET name = ?, description = ?, planned_date = ?, dateOfDone = ?, status = ? WHERE " +
                "task_id = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1,task.getName());
            preparedStatement.setString(2, task.getDescription());
            preparedStatement.setDate(3, Date.valueOf(task.getPlannedDate().toLocalDate()));
            preparedStatement.setDate(4,Date.valueOf(task.getDateOfDone().toLocalDate()));
            preparedStatement.setString(5, task.getStatus());
            preparedStatement.setInt(6, task.getId());
            preparedStatement.execute();
        }
    }

    @Override
    public void delete(Task task) throws SQLException {
        String sql = "DELETE FROM tasks WHERE task_id = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, task.getId());
            preparedStatement.execute();
        }

    }

    @Override
    public List<Task> getAll() throws SQLException {
        String sql = "SELECT * FROM tasks";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            List<Task> tasks = new ArrayList<Task>();
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Task task = new Task(resultSet.getInt(1), resultSet.getInt(2),
                        resultSet.getString(3), resultSet.getString(4),
                        resultSet.getDate(5).toLocalDate().atStartOfDay(),  resultSet.getDate(6).toLocalDate().atStartOfDay(),
                        resultSet.getString(7));
                tasks.add(task);
            }
            return tasks;
        }
    }

    @Override
    public List<Task> getSortedByCriteria(int journalId, String column, String criteria) throws SQLException {
        String sql = "Select * FROM tasks JOIN journals ON tasks.journal_id = journals.journal_id WHERE journal_id = ? ORDER BY ? ?";
        //String sql = "Select * FROM tasks JOIN journals ON tasks.journal_id = journals.journal_id WHERE (journal_id=?) AND (? = ?)";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, journalId);
            preparedStatement.setString(2, column);
            preparedStatement.setString(3, criteria);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Task> tasks = new ArrayList<Task>();
            while(resultSet.next()){
                Task task = new Task(resultSet.getInt(1), resultSet.getInt(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getDate(5).toLocalDate().atStartOfDay(),
                        resultSet.getDate(6).toLocalDate().atStartOfDay(), resultSet.getString(7));
                tasks.add(task);
            }
            return tasks;
        }
    }

    @Override
    public List<Task> getFilteredByPattern(int journalId, String column, String pattern, String criteria) throws SQLException {
        String sql = "SELECT * FROM tasks JOIN journals ON tasks.journal_id = journals.journal_id WHERE (journal_id = ?)" +
                " AND (? LIKE ?) ORDER BY ? ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,journalId);
            preparedStatement.setString(2, column);
            preparedStatement.setString(3, pattern);
            preparedStatement.setString(4, column);
            preparedStatement.setString(5,criteria);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Task> tasks = new ArrayList<Task>();
            while(resultSet.next()){
                Task task = new Task(resultSet.getInt(1), resultSet.getInt(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getDate(5).toLocalDate().atStartOfDay(),
                        resultSet.getDate(6).toLocalDate().atStartOfDay(), resultSet.getString(7));
                tasks.add(task);
            }
            return tasks;
        }
    }

    @Override
    public List<Task> getFilteredByEquals(int journalId, String column, String equal, String criteria) throws SQLException {
        String sql = "SELECT * FROM tasks JOIN journals ON tasks.journal_id = journals.journal_id WHERE (journal_id = ?) AND (? = ?) ORDER BY ? ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,journalId);
            preparedStatement.setString(2, column);
            preparedStatement.setString(3, equal);
            preparedStatement.setString(4, column);
            preparedStatement.setString(5, criteria);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Task> tasks = new ArrayList<Task>();
            while (resultSet.next()){
                Task task = new Task(resultSet.getInt(1), resultSet.getInt(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getDate(5).toLocalDate().atStartOfDay(),
                        resultSet.getDate(6).toLocalDate().atStartOfDay(), resultSet.getString(7));
                tasks.add(task);
            }
            return tasks;
        }
    }
}
