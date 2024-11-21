package com.todo.planner.data.repository;

import static com.todo.planner.data.database.TodoDatabase.databaseWriteExecutor;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.todo.planner.data.dao.CategoryDao;
import com.todo.planner.data.dao.TaskDao;
import com.todo.planner.data.database.TodoDatabase;
import com.todo.planner.data.entity.Category;
import com.todo.planner.data.entity.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TodoRepository {
    private TaskDao taskDao;
    private CategoryDao categoryDao;
    private LiveData<List<Task>> allTasks;
    private LiveData<List<Category>> allCategories;
    private final ExecutorService executorService;

    public TodoRepository(Application application) {
        TodoDatabase db = TodoDatabase.getDatabase(application);
        taskDao = db.taskDao();
        categoryDao = db.categoryDao();
        allTasks = taskDao.getAllTasks();
        allCategories = categoryDao.getAllCategories();
        executorService = Executors.newFixedThreadPool(4);
    }

    // Category operations
    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public void getCategoryByName(String name, OnCategoryFoundListener listener) {
        databaseWriteExecutor.execute(() -> {
            Category category = categoryDao.getCategoryByName(name);
            new Handler(Looper.getMainLooper()).post(() -> listener.onCategoryFound(category));
        });
    }

    public void insertCategory(Category category) {
        databaseWriteExecutor.execute(() -> {
            categoryDao.insert(category);
        });
    }

    public void updateCategory(Category category) {
        databaseWriteExecutor.execute(() -> {
            categoryDao.update(category);
        });
    }

    public void deleteCategory(Category category) {
        databaseWriteExecutor.execute(() -> {
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

    private final Map<Integer, List<Task>> taskCache = new ConcurrentHashMap<>();

    public void insertTask(Task task) {
        executorService.execute(() -> {
            taskDao.insert(task);
            // Refresh cache to avoid stale data
            taskCache.remove(task.getCategoryId());
            List<Task> updatedTasks = taskDao.getTasksByCategory(task.getCategoryId()).getValue();
            taskCache.put(task.getCategoryId(), updatedTasks != null ? updatedTasks : new ArrayList<>());
        });
    }

    public interface OnCategoryFoundListener {
        void onCategoryFound(Category category);
    }

    public void updateTask(Task task) {
        databaseWriteExecutor.execute(() -> {
            taskDao.update(task);
        });
    }

    public void deleteTask(Task task) {
        databaseWriteExecutor.execute(() -> {
            taskDao.delete(task);
        });
    }

    public void updateTaskStatus(int taskId, boolean isCompleted, String completedDate) {
        databaseWriteExecutor.execute(() -> {
            taskDao.updateTaskStatus(taskId, isCompleted, completedDate);
        });
    }

    public void cleanup() {
        if (databaseWriteExecutor != null && !databaseWriteExecutor.isShutdown()) {
            databaseWriteExecutor.shutdown();
        }
    }

}
