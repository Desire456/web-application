package org.netcracker.students.dao.postrgresql;

import org.netcracker.students.dao.exception.journalDAO.*;
import org.netcracker.students.dao.interfaces.JournalDAO;
import org.netcracker.students.factories.JournalFactory;
import org.netcracker.students.model.Journal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgreSQLJournalDAO implements JournalDAO {
    private Connection connection;

    public PostgreSQLJournalDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Journal create(String name, String description, Integer userId, Date creationDate, String accessModifier) throws CreateJournalException {
        String sql = "INSERT INTO journals VALUES (default, ?, ?, ?, ?, ?)";
        String RETURN_JOURNAL_SQL = "SELECT * FROM journals WHERE name = ?";
        boolean privateFlag = false;
        if (accessModifier.equals("private")) privateFlag = true;
        else if (accessModifier.equals("public")) privateFlag = false;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, description);
            preparedStatement.setDate(4, creationDate);
            preparedStatement.setBoolean(5, privateFlag);
            preparedStatement.execute();
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(RETURN_JOURNAL_SQL)) {
                preparedStatement1.setString(1, name);
                JournalFactory journalFactory = new JournalFactory();
                ResultSet resultSet = preparedStatement1.executeQuery();
                if (resultSet.next()) {
                    return journalFactory.createJournal(resultSet.getInt(1), resultSet.getString(3),
                            resultSet.getString(4), resultSet.getInt(2),
                            resultSet.getTimestamp(5).toLocalDateTime(), accessModifier);
                }
        }
        } catch (SQLException e) {
            throw new CreateJournalException(DAOErrorConstants.CREATE_JOURNAL_EXCEPTION_MESSAGE);
        }
        return null;
    }

    @Override
    public Journal read(int id) throws ReadJournalException {
        String sql = "SELECT * FROM journals WHERE journal_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            String accessModifier;
            if (resultSet.next()) {
                if (resultSet.getBoolean(6)) accessModifier = "private";
                else accessModifier = "public";
                JournalFactory journalFactory = new JournalFactory();
                return journalFactory.createJournal(resultSet.getInt(1), resultSet.getString(3),
                        resultSet.getString(4), resultSet.getInt(2),
                        resultSet.getTimestamp(5).toLocalDateTime(), accessModifier);
            }
        } catch (SQLException e) {
            throw new ReadJournalException(DAOErrorConstants.READ_JOURNAL_EXCEPTION);
        }
        return null;
    }

    @Override
    public void update(Journal journal) throws UpdateJournalException {
        String sql = "UPDATE journals SET isprivate = ?, name = ?, creation_date = ?, description = ? WHERE journal_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(5, journal.getId());
            boolean isPrivate = journal.getAccessModifier().equals("private");
            preparedStatement.setBoolean(1, isPrivate);
            preparedStatement.setString(2, journal.getName());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(journal.getCreationDate()));
            preparedStatement.setString(4, journal.getDescription());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new UpdateJournalException(DAOErrorConstants.UPDATE_JOURNAL_EXCEPTION);
        }
    }

    @Override
    public void delete(int id) throws DeleteJournalException {
        String sql = "DELETE FROM journals WHERE journal_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new DeleteJournalException(DAOErrorConstants.DELETE_JOURNAL_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public List<Journal> getAll() throws GetAllJournalException {
        String sql = "SELECT * FROM journals";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            List<Journal> journals = new ArrayList<Journal>();
            ResultSet resultSet = preparedStatement.executeQuery();
            String accessModifier;
            JournalFactory journalFactory = new JournalFactory();
            while (resultSet.next()) {
                if (resultSet.getBoolean(6)) accessModifier = "private";
                else accessModifier = "public";
                Journal journal = journalFactory.createJournal(resultSet.getInt(1), resultSet.getString(3),
                        resultSet.getString(4), resultSet.getInt(2),
                        resultSet.getTimestamp(5).toLocalDateTime(), accessModifier);
                journals.add(journal);
            }
            return journals;
        } catch (SQLException e) {
            throw new GetAllJournalException(DAOErrorConstants.GET_ALL_JOURNAL_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public List<Journal> getAll(int userId) throws GetAllJournalByUserIdException {
        String sql = "SELECT * FROM journals WHERE user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            List<Journal> journals = new ArrayList<Journal>();
            ResultSet resultSet = preparedStatement.executeQuery();
            String accessModifier;
            JournalFactory journalFactory = new JournalFactory();
            while (resultSet.next()) {
                if (resultSet.getBoolean(6)) accessModifier = "private";
                else accessModifier = "public";
                Journal journal = journalFactory.createJournal(resultSet.getInt(1), resultSet.getString(3),
                        resultSet.getString(4), resultSet.getInt(2),
                        resultSet.getTimestamp(5).toLocalDateTime(), accessModifier);
                journals.add(journal);
            }
            return journals;
        } catch (SQLException e) {
            throw new GetAllJournalByUserIdException(DAOErrorConstants.GET_ALL_JOURNAL_BY_USER_ID_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public List<Journal> getSortedByCriteria(String column, String criteria) throws GetSortedByCriteriaJournalException {
        String sql = "SELECT * FROM journals ORDER BY ? ? ";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, column);
            preparedStatement.setString(2, criteria);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Journal> journals = new ArrayList<Journal>();
            String accessModifier;
            JournalFactory journalFactory = new JournalFactory();
            while (resultSet.next()) {
                if (resultSet.getBoolean(6)) accessModifier = "private";
                else accessModifier = "public";
                Journal journal = journalFactory.createJournal(resultSet.getInt(1), resultSet.getString(3),
                        resultSet.getString(4), resultSet.getInt(2),
                        resultSet.getTimestamp(5).toLocalDateTime(), accessModifier);
                journals.add(journal);
            }
            return journals;
        } catch (SQLException e) {
            throw new GetSortedByCriteriaJournalException(DAOErrorConstants.GET_SORTED_BY_CRITERIA_JOURNAL_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public List<Journal> getFilteredByPattern(String column, String pattern, String criteria) throws GetFilteredByPatternJournalException {
        String sql = "SELECT * FROM journals WHERE ? LIKE ? ORDER BY ? ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, column);
            preparedStatement.setString(2, pattern);
            preparedStatement.setString(3, column);
            preparedStatement.setString(4, criteria);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Journal> journals = new ArrayList<Journal>();
            String accessModifier;
            JournalFactory journalFactory = new JournalFactory();
            while (resultSet.next()) {
                if (resultSet.getBoolean(6)) accessModifier = "Private";
                else accessModifier = "Public";
                Journal journal = journalFactory.createJournal(resultSet.getInt(1), resultSet.getString(3),
                        resultSet.getString(4), resultSet.getInt(2),
                        resultSet.getTimestamp(5).toLocalDateTime(), accessModifier);
                journals.add(journal);
            }
            return journals;
        } catch (SQLException e) {
            throw new GetFilteredByPatternJournalException(DAOErrorConstants.GET_FILTERED_BY_PATTERN_JOURNAL_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public List<Journal> getFilteredByEquals(String column, String equal, String criteria) throws GetFilteredByEqualsJournalException {
        String sql = "SELECT * FROM journals WHERE ? = ? ORDER BY ? ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, column);
            preparedStatement.setString(2, equal);
            preparedStatement.setString(3, column);
            preparedStatement.setString(4, criteria);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Journal> journals = new ArrayList<Journal>();
            String accessModifier;
            JournalFactory journalFactory = new JournalFactory();
            while (resultSet.next()) {
                if (resultSet.getBoolean(6)) accessModifier = "private";
                else accessModifier = "public";
                Journal journal = journalFactory.createJournal(resultSet.getInt(1), resultSet.getString(3),
                        resultSet.getString(4), resultSet.getInt(2),
                        resultSet.getTimestamp(5).toLocalDateTime(), accessModifier);
                journals.add(journal);
            }
            return journals;
        } catch (SQLException e) {
            throw new GetFilteredByEqualsJournalException(DAOErrorConstants.GET_FILTERED_BY_EQUALS_JOURNAL_EXCEPTION_MESSAGE);
        }
    }
}
