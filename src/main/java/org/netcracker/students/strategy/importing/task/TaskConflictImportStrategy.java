package org.netcracker.students.strategy.importing.task;

import org.netcracker.students.controller.JournalController;
import org.netcracker.students.controller.TaskController;
import org.netcracker.students.dao.exceptions.NameAlreadyExistException;
import org.netcracker.students.dao.exceptions.journalDAO.ReadJournalException;
import org.netcracker.students.dao.exceptions.managerDAO.GetConnectionException;
import org.netcracker.students.dao.exceptions.taskDAO.CreateTaskException;
import org.netcracker.students.dao.exceptions.taskDAO.ReadTaskException;
import org.netcracker.students.model.Task;
import org.netcracker.students.strategy.StrategyConstants;
import org.netcracker.students.strategy.importing.exceptions.*;
import org.netcracker.students.strategy.importing.ImportStrategy;

public class TaskConflictImportStrategy implements ImportStrategy<Task> {

    @Override
    public void store(Task task) throws ImportException, PrintableImportException {
        try {
            TaskController taskController = TaskController.getInstance();
            JournalController journalController = JournalController.getInstance();

            Task oldTask = taskController.getTask(task.getId());
            if (oldTask != null) throw new ImportMatchIdException();
            if (journalController.getJournal(task.getJournalId()) == null) throw new ImportJournalNotFoundException();

            taskController.addTaskWithId(task);
        } catch (GetConnectionException | ReadTaskException | ReadJournalException |
                CreateTaskException e) {
            throw new ImportException(StrategyConstants.IMPORT_EXCEPTION_MESSAGE + e.getMessage());
        } catch (ImportMatchIdException e) {
            String exceptionMessage = String.format(StrategyConstants.IMPORT_EXCEPTION_MATCH_ID_MESSAGE,
                    StrategyConstants.TASK_TYPE.toLowerCase(), task.getId());
            throw new PrintableImportException(StrategyConstants.IMPORT_EXCEPTION_MESSAGE +
                    exceptionMessage);
        } catch (NameAlreadyExistException e) {
            throw new PrintableImportException(StrategyConstants.IMPORT_EXCEPTION_MESSAGE +
                    e.getMessage());
        } catch (ImportJournalNotFoundException e) {
            String exceptionMessage = String.format(StrategyConstants.IMPORT_EXCEPTION_JOURNAL_NOT_FOUND_MESSAGE,
                    task.getName());
            throw new PrintableImportException(StrategyConstants.IMPORT_EXCEPTION_MESSAGE +
                    exceptionMessage);
        }
    }
}