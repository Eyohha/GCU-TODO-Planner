package com.todo.planner.ui;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.todo.planner.R;
import com.todo.planner.data.entity.Task;
import com.todo.planner.ui.dialogs.AddEditTaskDialogFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class AddEditTaskDialogFragmentTest {

    @Test
    public void testNewTaskDialog() {
        FragmentScenario.launchInContainer(AddEditTaskDialogFragment.class);

        onView(withId(R.id.taskTitleInput)).check(matches(isDisplayed()));
        onView(withId(R.id.taskDescriptionInput)).check(matches(isDisplayed()));
        onView(withId(R.id.categorySpinner)).check(matches(isDisplayed()));
    }

    @Test
    public void testEditTaskDialog() {
        Task task = new Task("Test Task", "Description", "2024-11-17", 1);
        FragmentScenario.launchInContainer(
                AddEditTaskDialogFragment.class,
                AddEditTaskDialogFragment.newInstance(task).getArguments()
        );

        onView(withId(R.id.taskTitleInput))
                .check(matches(withText("Test Task")));
    }
}
