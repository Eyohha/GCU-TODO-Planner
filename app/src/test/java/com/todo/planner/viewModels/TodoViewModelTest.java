package com.todo.planner.viewModels;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.app.Application;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.todo.planner.data.entity.Task;
import com.todo.planner.data.entity.Category;
import com.todo.planner.data.repository.TodoRepository;
import com.todo.planner.ui.viewmodels.TodoViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

public class TodoViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Application application;

    @Mock
    private TodoRepository repository;

    private TodoViewModel viewModel;
    private MutableLiveData<List<Task>> tasks;
    private MutableLiveData<List<Category>> categories;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        tasks = new MutableLiveData<>();
        categories = new MutableLiveData<>();

        when(repository.getAllTasks()).thenReturn(tasks);
        when(repository.getAllCategories()).thenReturn(categories);

        viewModel = new TodoViewModel(application);
        viewModel.setRepository(repository); // You'll need to add this setter in ViewModel
    }

    @Test
    public void insertTask_WithValidData_ShouldCallRepository() {
        // Arrange
        String title = "Test Task";
        String description = "Test Description";
        String dueDate = "2024-11-17";
        int categoryId = 1;

        // Act
        viewModel.insertTask(title, description, dueDate, categoryId);

        // Assert
        verify(repository).insertTask(any(Task.class));
    }

    @Test
    public void getTasksByCategory_WithValidCategoryId_ShouldReturnFilteredTasks() {
        // Arrange
        int categoryId = 1;
        MutableLiveData<List<Task>> filteredTasks = new MutableLiveData<>();
        when(repository.getTasksByCategory(categoryId)).thenReturn(filteredTasks);

        // Act
        viewModel.getTasksByCategory(categoryId);

        // Assert
        assertEquals(categoryId, viewModel.getCurrentCategoryId());
        verify(repository).getTasksByCategory(categoryId);
    }

    @Test
    public void toggleTaskCompletion_ShouldUpdateTaskStatus() {
        // Arrange
        Task task = new Task("Test", "Desc", "2024-11-17", 1);
        task.setCompleted(false);

        // Act
        viewModel.toggleTaskCompletion(task);

        // Assert
        verify(repository).updateTask(any(Task.class));
    }
}
