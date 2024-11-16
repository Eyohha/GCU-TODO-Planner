package com.todo.planner.ui.viewmodels;

import android.app.Application;

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
        return allTasks;
    }

    public LiveData<List<Task>> getTasksByCategory(int categoryId) {
        if (categoryId == -1) {
            return allTasks;
        }

        // Create a filtered list
        repository.getTasksByCategory(categoryId).observeForever(tasks -> {
            filteredTasks.setValue(tasks);
        });
        return filteredTasks;
    }

    public void insertTask(String title, String description, String dueDate, int categoryId) {
        // If no category selected, get PERSONAL category ID
        if (categoryId == 0) {
            repository.getCategoryByName("PERSONAL", personalCategory -> {
                if (personalCategory != null) {
                    Task task = new Task(title, description, dueDate, personalCategory.getId());
                    repository.insertTask(task);
                }
            });
        } else {
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
        boolean newStatus = !task.isCompleted();
        String completedDate = null;

        if (newStatus) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            completedDate = sdf.format(new Date());
        }

        task.setCompleted(newStatus);
        task.setCompletedDate(completedDate);
        repository.updateTask(task);
    }

    public int getCurrentCategoryId() {
        return currentCategoryId;
    }

    public void setCurrentCategoryId(int categoryId) {
        this.currentCategoryId = categoryId;
    }
}
