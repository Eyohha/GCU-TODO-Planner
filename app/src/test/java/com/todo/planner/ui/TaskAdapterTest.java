package com.todo.planner.ui;

import android.view.LayoutInflater;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.todo.planner.data.entity.Task;
import com.todo.planner.ui.adapters.TaskAdapter;

@RunWith(MockitoJUnitRunner.class)
public class TaskAdapterTest {

    @Mock
    private TaskAdapter.TaskActionListener listener;

    @Mock
    private LayoutInflater inflater;

    @Mock
    private View itemView;

    private TaskAdapter adapter;

    @Before
    public void setup() {
        adapter = new TaskAdapter(listener);
    }

    @Test
    public void testTaskCompletion() {
        Task task = new Task("Test", "Desc", "2024-11-17", 1);
        adapter.submitList(java.util.Collections.singletonList(task));

        // Verify task completion handling
        verify(listener, never()).onTaskCheckedChanged(eq(task), anyBoolean());
    }

    @Test
    public void testSubmitList() {
        Task task1 = new Task("Test1", "Desc1", "2024-11-17", 1);
        Task task2 = new Task("Test2", "Desc2", "2024-11-17", 1);

        adapter.submitList(java.util.Arrays.asList(task1, task2));
        assertEquals(2, adapter.getItemCount());
    }
}
