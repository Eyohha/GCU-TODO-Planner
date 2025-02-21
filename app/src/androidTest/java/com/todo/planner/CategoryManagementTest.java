package com.todo.planner;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.todo.planner.ui.activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class CategoryManagementTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testAddNewCategory() {
        // Click "+ Category" chip
        onView(withText("+ Category")).perform(click());

        // Verify category dialog is shown
        onView(withId(R.id.categoryNameInput)).check(matches(isDisplayed()));

        // Type new category name
        onView(withId(R.id.categoryNameInput)).perform(typeText("HOME"));

        // Click Save
        onView(withId(R.id.saveButton)).perform(click());

        // Verify new category appears
        onView(withText("HOME")).check(matches(isDisplayed()));
    }

    @Test
    public void testEditCategory() {
        // Long click on WORK category
        onView(withText("WORK")).perform(longClick());

        // Verify category dialog is shown
        onView(withId(R.id.categoryNameInput)).check(matches(isDisplayed()));

        // Type new name
        onView(withId(R.id.categoryNameInput)).perform(typeText("OFFICE"));

        // Click Save
        onView(withId(R.id.saveButton)).perform(click());

        // Verify category name is updated
        onView(withText("OFFICE")).check(matches(isDisplayed()));
    }
}
