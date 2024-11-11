package com.todo.planner.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.todo.planner.data.entity.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDate ASC")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId ORDER BY isCompleted ASC, dueDate ASC")
    LiveData<List<Task>> getTasksByCategory(int categoryId);

    @Query("UPDATE tasks SET isCompleted = :isCompleted, completedDate = :completedDate WHERE id = :taskId")
    void updateTaskStatus(int taskId, boolean isCompleted, String completedDate);
}