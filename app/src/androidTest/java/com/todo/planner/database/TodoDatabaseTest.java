package com.todo.planner.database;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.todo.planner.data.dao.CategoryDao;
import com.todo.planner.data.dao.TaskDao;
import com.todo.planner.data.database.TodoDatabase;
import com.todo.planner.data.entity.Category;
import com.todo.planner.data.entity.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class TodoDatabaseTest {
    private TodoDatabase db;
    private TaskDao taskDao;
    private CategoryDao categoryDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, TodoDatabase.class)
                .allowMainThreadQueries()
                .build();
        taskDao = db.taskDao();
        categoryDao = db.categoryDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void testDefaultCategories() {
        List<Category> categories = categoryDao.getAllCategories().getValue();
        assertNotNull(categories);
        assertEquals(2, categories.size()); // WORK and PERSONAL
    }

    @Test
    public void testTaskInsertion() {
        Category category = new Category("TEST");
        long categoryId = categoryDao.insert(category);

        Task task = new Task("Test Task", "Description", "2024-11-17", (int)categoryId);
        taskDao.insert(task);

        List<Task> tasks = taskDao.getTasksByCategory((int)categoryId).getValue();
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
    }
}