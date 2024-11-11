package com.todo.planner.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
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

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        TaskAdapter.TaskActionListener,
        AddEditTaskDialogFragment.TaskDialogListener,
        CategoryDialogFragment.CategoryDialogListener {

    private ActivityMainBinding binding;
    private TodoViewModel viewModel;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        viewModel = new ViewModelProvider(this).get(TodoViewModel.class);
        setupRecyclerView();
        setupCategoryChips();
        setupFab();
        observeData();
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter(this);
        binding.tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.tasksRecyclerView.setAdapter(taskAdapter);
    }

    private void setupCategoryChips() {
        // Add the "All" chip
        binding.chipAll.setOnClickListener(v -> {
            viewModel.setCurrentCategoryId(-1);
            observeTasksByCategory(-1);
        });

        // Observe categories changes
        viewModel.getAllCategories().observe(this, categories -> {
            binding.categoryChipGroup.removeAllViews();
            binding.categoryChipGroup.addView(binding.chipAll);

            for (Category category : categories) {
                Chip chip = new Chip(this);
                chip.setText(category.getName());
                chip.setCheckable(true);
                chip.setOnClickListener(v -> {
                    viewModel.setCurrentCategoryId(category.getId());
                    observeTasksByCategory(category.getId());
                });
                chip.setOnLongClickListener(v -> {
                    showCategoryDialog(category);
                    return true;
                });
                binding.categoryChipGroup.addView(chip);
            }
        });

        // Add category button
        Chip addCategoryChip = new Chip(this);
        addCategoryChip.setText("+ Category");
        addCategoryChip.setOnClickListener(v -> showCategoryDialog(null));
        binding.categoryChipGroup.addView(addCategoryChip);
    }

    private void setupFab() {
        binding.fabAddTask.setOnClickListener(v ->
                showAddEditTaskDialog(null)
        );
    }

    private void observeData() {
        viewModel.getAllTasks().observe(this, tasks -> {
            updateEmptyState(tasks);
            taskAdapter.submitList(tasks);
        });
    }

    private void observeTasksByCategory(int categoryId) {
        if (categoryId == -1) {
            viewModel.getAllTasks().observe(this, tasks -> {
                updateEmptyState(tasks);
                taskAdapter.submitList(tasks);
            });
        } else {
            viewModel.getTasksByCategory(categoryId).observe(this, tasks -> {
                updateEmptyState(tasks);
                taskAdapter.submitList(tasks);
            });
        }
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
        AddEditTaskDialogFragment dialog = AddEditTaskDialogFragment.newInstance(task);
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
        if (task.getId() == 0) {
            viewModel.insertTask(task.getTitle(), task.getDescription(),
                    task.getDueDate(), task.getCategoryId());
        } else {
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
