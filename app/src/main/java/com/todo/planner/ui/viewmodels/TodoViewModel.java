package com.todo.planner.ui.viewmodels;

import android.app.Application;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.todo.planner.data.entity.Category;
import com.todo.planner.data.entity.Task;
import com.todo.planner.data.repository.TodoRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodoViewModel extends AndroidViewModel {
    private TodoRepository repository;
    private LiveData<List<Task>> allTasks;
    private LiveData<List<Category>> allCategories;
    private MutableLiveData<List<Task>> filteredTasks;
    private int currentCategoryId = -1; // -1 represents "All" tasks

    public TodoViewModel(Application application) {
        super(application);
        repository = new TodoRepository(application);
        allTasks = repository.getAllTasks();
        allCategories = repository.getAllCategories();
        filteredTasks = new MutableLiveData<>();
    }

    @VisibleForTesting  // Add this annotation to indicate it's for testing
    public void setRepository(TodoRepository repository) {
        this.repository = repository;
        allTasks = repository.getAllTasks();
        allCategories = repository.getAllCategories();
        filteredTasks = new MutableLiveData<>();
    }

    // Category operations
    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public void insertCategory(String categoryName) {
        Category category = new Category(categoryName);
        repository.insertCategory(category);
    }

    public void updateCategory(Category category) {
        repository.updateCategory(category);
    }

    public void deleteCategory(Category category) {
        repository.deleteCategory(category);
    }

    // Task operations
    public LiveData<List<Task>> getAllTasks() {
        return repository.getAllTasks();
    }

    public LiveData<List<Task>> getTasksByCategory(int categoryId) {
        return repository.getTasksByCategory(categoryId);
    }

    public void insertTask(String title, String description, String dueDate, int categoryId) {
        if (categoryId == 0) {
            // Default to PERSONAL category if categoryId is not provided
            repository.getCategoryByName("PERSONAL", personalCategory -> {
                if (personalCategory != null) {
                    Task task = new Task(title, description, dueDate, personalCategory.getId());
                    repository.insertTask(task);
                }
            });
        } else {
            // Use the specified categoryId
            Task task = new Task(title, description, dueDate, categoryId);
            repository.insertTask(task);
        }
    }

    public void updateTask(Task task) {
        repository.updateTask(task);
    }

    public void deleteTask(Task task) {
        repository.deleteTask(task);
    }

    public void toggleTaskCompletion(Task task) {
        // Toggle the completion status
        boolean newStatus = !task.isCompleted();
        String completedDate = newStatus
                ? new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())
                : null;

        // Update the task fields directly
        task.setCompleted(newStatus);
        task.setCompletedDate(completedDate);

        // Update the task in the repository
        repository.updateTask(task);
    }


    public int getCurrentCategoryId() {
        return currentCategoryId;
    }

    public void setCurrentCategoryId(int categoryId) {
        this.currentCategoryId = categoryId;
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        allTasks = null;
        allCategories = null;
        filteredTasks = null;
    }
    public TodoRepository getRepository() {
        return repository;
    }
}
