package com.todo.planner.data.repository;

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
        executorService.execute(() -> {
            Category category = categoryDao.getCategoryByName(name);
            new Handler(Looper.getMainLooper()).post(() -> listener.onCategoryFound(category));
        });
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

    private final Map<Integer, List<Task>> taskCache = new ConcurrentHashMap<>();

    public void insertTask(Task task) {
        executorService.execute(() -> {
            taskDao.insert(task);
            // Update cache
            List<Task> cachedTasks = taskCache.getOrDefault(task.getCategoryId(), new ArrayList<>());
            cachedTasks.add(task);
            taskCache.put(task.getCategoryId(), cachedTasks);
        });
    }

    public interface OnCategoryFoundListener {
        void onCategoryFound(Category category);
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
