package com.todo.planner.ui.activities;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.todo.planner.R;
import com.todo.planner.data.entity.Category;
import com.todo.planner.data.entity.Task;
import com.todo.planner.databinding.ActivityMainBinding;
import com.todo.planner.ui.adapters.TaskAdapter;
import com.todo.planner.ui.dialogs.AddEditTaskDialogFragment;
import com.todo.planner.ui.dialogs.CategoryDialogFragment;
import com.todo.planner.ui.viewmodels.TodoViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        TaskAdapter.TaskActionListener,
        AddEditTaskDialogFragment.TaskDialogListener,
        CategoryDialogFragment.CategoryDialogListener {

    private ActivityMainBinding binding;
    private TodoViewModel viewModel;
    private TaskAdapter taskAdapter;
    private Category selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        setSupportActionBar(binding.toolbar);
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        viewModel = new ViewModelProvider(this).get(TodoViewModel.class);
        setupRecyclerView();
        setupCategoryChips();
        setupFab();
        observeData();
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter(this);
        taskAdapter.setHasStableIds(true);
        binding.tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.tasksRecyclerView.setAdapter(taskAdapter);
        // Add item decoration for spacing
        binding.tasksRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );
    }

    private void setupCategoryChips() {
        // Create color state list for chips
        ColorStateList chipColorStates = new ColorStateList(
                new int[][] {
                        new int[] { android.R.attr.state_checked },  // Checked state
                        new int[] { -android.R.attr.state_checked }  // Unchecked state
                },
                new int[] {
                        getResources().getColor(R.color.primary_blue, null),  // Color for checked state
                        getResources().getColor(R.color.medium_gray, null)     // Color for unchecked state
                }
        );

        // Add the "All" chip
        binding.chipAll.setChipBackgroundColor(chipColorStates);
        binding.chipAll.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white, null)));
        binding.chipAll.setChecked(true);
        binding.chipAll.setMinHeight(getDimensionInPixel(48)); // Ensure touch target size
        binding.chipAll.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            viewModel.setCurrentCategoryId(-1);
            observeTasksByCategory(-1);
            updateChipsState(binding.chipAll);
        });

        // Observe categories changes
        viewModel.getAllCategories().observe(this, categories -> {
            binding.categoryChipGroup.removeAllViews();
            binding.categoryChipGroup.addView(binding.chipAll);

            for (Category category : categories) {
                Chip chip = new Chip(this);
                chip.setText(category.getName());
                chip.setCheckable(true);
                chip.setClickable(true);
                chip.setChipBackgroundColor(chipColorStates);
                chip.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white, null)));
                chip.setMinHeight(getDimensionInPixel(48)); // Ensures touch target size
                chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                chip.setChecked(viewModel.getCurrentCategoryId() == category.getId());

                // Enhanced touch handling
                chip.setOnClickListener(v -> {
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    viewModel.setCurrentCategoryId(category.getId());
                    observeTasksByCategory(category.getId());
                    updateChipsState(chip);
                    selectedCategory = category;
                });

                chip.setOnLongClickListener(v -> {
                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    showCategoryDialog(category);
                    return true;
                });

                binding.categoryChipGroup.addView(chip);
            }

            // Add the "+ Category" chip
            Chip addCategoryChip = new Chip(this);
            addCategoryChip.setText("+ Category");
            addCategoryChip.setCheckable(false);
            addCategoryChip.setClickable(true);
            addCategoryChip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.primary_blue, null)));
            addCategoryChip.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white, null)));
            addCategoryChip.setMinHeight(getDimensionInPixel(48));
            addCategoryChip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            addCategoryChip.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                showCategoryDialog(null);
            });
            binding.categoryChipGroup.addView(addCategoryChip);
        });

        // Set single selection mode for the chip group
        binding.categoryChipGroup.setSingleSelection(true);
    }

    // Helper method to convert dp to pixels
    private int getDimensionInPixel(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    private void setupFab() {
        binding.fabAddTask.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            showAddEditTaskDialog(null);
        });
    }

    private void observeData() {
        viewModel.getAllTasks().observe(this, tasks -> {
            updateEmptyState(tasks);
            taskAdapter.submitList(tasks);
        });
    }

    private void updateChipsState(Chip selectedChip) {
        for (int i = 0; i < binding.categoryChipGroup.getChildCount(); i++) {
            View child = binding.categoryChipGroup.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                if (chip.isCheckable()) {
                    chip.setChecked(chip == selectedChip);
                }
            }
        }
    }

    private void observeTasksByCategory(int categoryId) {
        // Remove previous observer if exists
        if (viewModel.getAllTasks().hasObservers()) {
            viewModel.getAllTasks().removeObservers(this);
        }

        LiveData<List<Task>> tasksLiveData;
        if (categoryId == -1) {
            tasksLiveData = viewModel.getAllTasks();
        } else {
            tasksLiveData = viewModel.getTasksByCategory(categoryId);
        }

        tasksLiveData.observe(this, tasks -> {
            updateEmptyState(tasks);
            taskAdapter.submitList(tasks != null ? new ArrayList<>(tasks) : null);
        });
    }

    private void updateEmptyState(List<?> items) {
        if (items == null || items.isEmpty()) {
            binding.emptyStateText.setVisibility(View.VISIBLE);
            binding.tasksRecyclerView.setVisibility(View.GONE);
        } else {
            binding.emptyStateText.setVisibility(View.GONE);
            binding.tasksRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showAddEditTaskDialog(Task task) {
        int categoryId = selectedCategory != null ? selectedCategory.getId() : 0;
        AddEditTaskDialogFragment dialog = AddEditTaskDialogFragment.newInstance(task, categoryId);
        dialog.show(getSupportFragmentManager(), "task_dialog");
    }

    private void showCategoryDialog(Category category) {
        CategoryDialogFragment dialog = CategoryDialogFragment.newInstance(category);
        dialog.show(getSupportFragmentManager(), "category_dialog");
    }

    private void showDeleteTaskConfirmation(Task task) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteTask(task);
                })
                .setNegativeButton("Keep", null)
                .show();
    }

    // TaskAdapter.TaskActionListener implementations
    @Override
    public void onTaskCheckedChanged(Task task, boolean isChecked) {
        viewModel.toggleTaskCompletion(task);
    }

    @Override
    public void onTaskEdit(Task task) {
        showAddEditTaskDialog(task);
    }

    @Override
    public void onTaskDelete(Task task) {
        showDeleteTaskConfirmation(task);
    }

    @Override
    public void onAddSubtask(Task task) {
        // Show add task dialog with parent task reference
        showAddEditTaskDialog(null);
    }

    @Override
    public void onMarkTaskUndone(Task task) {
        task.setCompleted(false);
        task.setCompletedDate(null);
        viewModel.updateTask(task);
    }

    @Override
    public void onRemoveCompletedTask(Task task) {
        viewModel.deleteTask(task);
    }

    // AddEditTaskDialogFragment.TaskDialogListener implementations
    @Override
    public void onTaskSaved(Task task) {
        int categoryId = selectedCategory != null ? selectedCategory.getId() : 0;
        if (task.getId() == 0) {
            // New task
            viewModel.insertTask(
                    task.getTitle(),
                    task.getDescription(),
                    task.getDueDate(),
                    categoryId
            );
        } else {
            // Existing task
            task.setCategoryId(categoryId);
            viewModel.updateTask(task);
        }
    }

    // CategoryDialogFragment.CategoryDialogListener implementations
    @Override
    public void onCategorySaved(Category category) {
        if (category.getId() == 0) {
            viewModel.insertCategory(category.getName());
        } else {
            viewModel.updateCategory(category);
        }
    }

    @Override
    public void onCategoryDeleted(Category category) {
        viewModel.deleteCategory(category);
    }
}
