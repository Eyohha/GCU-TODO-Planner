package com.todo.planner;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.matcher.ViewMatchers;

import com.todo.planner.ui.activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class InitialSetupTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testInitialSetup() {
        // Verify default categories are present
        onView(withText("All")).check(matches(isDisplayed()));
        onView(withText("WORK")).check(matches(isDisplayed()));
        onView(withText("PERSONAL")).check(matches(isDisplayed()));
        onView(withText("+ Category")).check(matches(isDisplayed()));

        // Verify empty state message is shown
        onView(withId(R.id.emptyStateText)).check(matches(isDisplayed()));
        onView(withId(R.id.emptyStateText)).check(matches(withText("No tasks yet")));

        // Verify FAB is present
        onView(withId(R.id.fabAddTask)).check(matches(isDisplayed()));
    }
}
