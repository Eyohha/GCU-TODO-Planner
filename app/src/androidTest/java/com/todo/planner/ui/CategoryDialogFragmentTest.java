package com.todo.planner.ui;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.todo.planner.R;
import com.todo.planner.data.entity.Category;
import com.todo.planner.ui.dialogs.CategoryDialogFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class CategoryDialogFragmentTest {

    @Test
    public void testNewCategoryDialog() {
        FragmentScenario.launchInContainer(CategoryDialogFragment.class);

        onView(withId(R.id.categoryNameInput)).check(matches(isDisplayed()));
        onView(withId(R.id.saveButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testEditCategoryDialog() {
        Category category = new Category("Test Category");
        FragmentScenario.launchInContainer(
                CategoryDialogFragment.class,
                CategoryDialogFragment.newInstance(category).getArguments()
        );

        onView(withId(R.id.categoryNameInput))
                .check(matches(withText("Test Category")));
    }
}
