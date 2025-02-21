package com.todo.planner.ui;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.todo.planner.R;
import com.todo.planner.ui.activities.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Before
    public void setup() {
        ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void testInitialUIElements() {
        // Verify default categories are present
        onView(withText("All")).check(matches(isDisplayed()));
        onView(withText("WORK")).check(matches(isDisplayed()));
        onView(withText("PERSONAL")).check(matches(isDisplayed()));
        onView(withText("+ Category")).check(matches(isDisplayed()));

        // Verify empty state is shown initially
        onView(withId(R.id.emptyStateText)).check(matches(isDisplayed()));

        // Verify FAB is present
        onView(withId(R.id.fabAddTask)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddTaskFlow() {
        // Click FAB
        onView(withId(R.id.fabAddTask)).perform(click());

        // Verify AddEditTaskDialog appears
        onView(withId(R.id.taskTitleInput)).check(matches(isDisplayed()));
        onView(withId(R.id.categorySpinner)).check(matches(isDisplayed()));
    }

    @Test
    public void testCategorySelection() {
        // Click WORK category
        onView(withText("WORK")).perform(click());

        // Verify empty state for category
        onView(withId(R.id.emptyStateText)).check(matches(isDisplayed()));

        // Return to All
        onView(withText("All")).perform(click());
    }
}