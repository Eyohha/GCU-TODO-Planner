package com.todo.planner.repository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.todo.planner.data.dao.TaskDao;
import com.todo.planner.data.dao.CategoryDao;
import com.todo.planner.data.database.TodoDatabase;
import com.todo.planner.data.entity.Task;
import com.todo.planner.data.entity.Category;
import com.todo.planner.data.repository.TodoRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

public class TodoRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Application application;

    @Mock
    private TodoDatabase database;

    @Mock
    private TaskDao taskDao;

    @Mock
    private CategoryDao categoryDao;

    private TodoRepository repository;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(database.taskDao()).thenReturn(taskDao);
        when(database.categoryDao()).thenReturn(categoryDao);
        repository = new TodoRepository(application);
    }

    @Test
    public void insertTask_ShouldCallDaoInsert() {
        // Arrange
        Task task = new Task("Test", "Desc", "2024-11-17", 1);

        // Act
        repository.insertTask(task);

        // Assert
        verify(taskDao).insert(task);
    }

    @Test
    public void getCategoryByName_ShouldReturnCorrectCategory() {
        // Arrange
        String categoryName = "PERSONAL";
        Category expectedCategory = new Category(categoryName);
        when(categoryDao.getCategoryByName(categoryName)).thenReturn(expectedCategory);

        // Act
        repository.getCategoryByName(categoryName, category -> {
            // Assert
            assertEquals(expectedCategory, category);
        });
    }

    @Test
    public void getTasksByCategory_ShouldReturnFilteredTasks() {
        // Arrange
        int categoryId = 1;
        MutableLiveData<List<Task>> expectedTasks = new MutableLiveData<>();
        when(taskDao.getTasksByCategory(categoryId)).thenReturn(expectedTasks);

        // Act
        LiveData<List<Task>> result = repository.getTasksByCategory(categoryId);

        // Assert
        assertEquals(expectedTasks, result);
        verify(taskDao).getTasksByCategory(categoryId);
    }
}
