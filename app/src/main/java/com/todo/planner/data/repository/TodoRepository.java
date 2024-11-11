package com.todo.planner.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.todo.planner.data.dao.CategoryDao;
import com.todo.planner.data.dao.TaskDao;
import com.todo.planner.data.database.TodoDatabase;
import com.todo.planner.data.entity.Category;
import com.todo.planner.data.entity.Task;

import java.util.List;

public class TodoRepository {
    private TaskDao taskDao;
    private CategoryDao categoryDao;
    private LiveData<List<Task>> allTasks;
    private LiveData<List<Category>> allCategories;

    public TodoRepository(Application application) {
        TodoDatabase db = TodoDatabase.getDatabase(application);
        taskDao = db.taskDao();
        categoryDao = db.categoryDao();
        allTasks = taskDao.getAllTasks();
        allCategories = categoryDao.getAllCategories();
    }

    // Category operations
    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public void insertCategory(Category category) {
        TodoDatabase.databaseWriteExecutor.execute(() -> {
            categoryDao.insert(category);
        });
    }

    public void updateCategory(Category category) {
        TodoDatabase.databaseWriteExecutor.execute(() -> {
            categoryDao.update(category);
        });
    }

    public void deleteCategory(Category category) {
        TodoDatabase.databaseWriteExecutor.execute(() -> {
            categoryDao.delete(category);
        });
    }

    // Task operations
    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<Task>> getTasksByCategory(int categoryId) {
        return taskDao.getTasksByCategory(categoryId);
    }

    public void insertTask(Task task) {
        TodoDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.insert(task);
        });
    }

    public void updateTask(Task task) {
        TodoDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.update(task);
        });
    }

    public void deleteTask(Task task) {
        TodoDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.delete(task);
        });
    }

    public void updateTaskStatus(int taskId, boolean isCompleted, String completedDate) {
        TodoDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.updateTaskStatus(taskId, isCompleted, completedDate);
        });
    }
}
