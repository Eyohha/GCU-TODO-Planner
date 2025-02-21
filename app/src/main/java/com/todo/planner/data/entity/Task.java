package com.todo.planner.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks",
        foreignKeys = @ForeignKey(entity = Category.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.CASCADE), indices = {@Index("categoryId")})
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private String dueDate;
    private int categoryId;
    private boolean isCompleted;
    private String completedDate;

    public Task(String title, String description, String dueDate, int categoryId) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.categoryId = categoryId;
        this.isCompleted = false;
        this.completedDate = null;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }
}
