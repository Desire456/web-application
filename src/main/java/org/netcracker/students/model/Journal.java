package org.netcracker.students.model;


import org.netcracker.students.controller.UserController;
import org.netcracker.students.controller.utils.LocalDateTimeAdapter;
import org.netcracker.students.dao.exceptions.managerDAO.GetConnectionException;
import org.netcracker.students.dao.exceptions.userDAO.ReadUserException;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Main shared.model class, which stores tasks
 *
 * @see Task
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@Entity
@Table(name = "journals")
public class Journal implements Serializable {

    @Id
    @Column(name = "journal_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Column(name = "user_id")
    private User user;

    @Column(name = "isprivate")
    private boolean isPrivate;

    @Column(name = "name")
    private String name;

    @Column(name = "creation_date")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime creationDate;

    @Column(name = "description")
    private String description;

    public Journal(int id, String name, String description, int userId,
                   LocalDateTime creationDate, boolean isPrivate) throws GetConnectionException, ReadUserException {

        this.id = id;
        this.isPrivate = isPrivate;
        this.user = UserController.getInstance().getUser(userId);
        this.name = name;
        this.creationDate = creationDate;
        this.description = description;
    }

    public Journal(String name, String description, int userId, LocalDateTime creationDate, boolean isPrivate) throws GetConnectionException, ReadUserException {
        this.name = name;
        this.description = description;
        this.user = UserController.getInstance().getUser(userId);
        this.creationDate = creationDate;
        this.isPrivate = isPrivate;
    }


    public Journal() {
    }

    public Journal(Journal journal) {
        this.id = journal.id;
        this.isPrivate = journal.isPrivate;
        this.name = journal.name;
        this.creationDate = journal.creationDate;
        this.description = journal.description;
        this.user = journal.user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return getUser().getId();
    }

    public void setUserId(int userId) throws GetConnectionException, ReadUserException {
        this.user = UserController.getInstance().getUser(userId);
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}