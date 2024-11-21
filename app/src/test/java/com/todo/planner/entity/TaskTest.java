package com.todo.planner.entity;

import org.junit.Test;
import static org.junit.Assert.*;

import com.todo.planner.data.entity.Task;

public class TaskTest {

    @Test
    public void createTask_WithValidData_ShouldSetAllProperties() {
        // Arrange
        String title = "Test Task";
        String description = "Test Description";
        String dueDate = "2024-11-17";
        int categoryId = 1;

        // Act
        Task task = new Task(title, description, dueDate, categoryId);

        // Assert
        assertEquals(title, task.getTitle());
        assertEquals(description, task.getDescription());
        assertEquals(dueDate, task.getDueDate());
        assertEquals(categoryId, task.getCategoryId());
        assertFalse(task.isCompleted());
        assertNull(task.getCompletedDate());
    }

    @Test
    public void toggleCompletion_ShouldUpdateCompletionStatus() {
        // Arrange
        Task task = new Task("Test", "Desc", "2024-11-17", 1);

        // Act
        task.setCompleted(true);

        // Assert
        assertTrue(task.isCompleted());
    }
}
