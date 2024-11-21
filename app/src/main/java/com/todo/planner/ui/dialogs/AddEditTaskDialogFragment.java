package com.todo.planner.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.todo.planner.data.entity.Category;
import com.todo.planner.data.entity.Task;
import com.todo.planner.databinding.DialogAddEditTaskBinding;
import com.todo.planner.ui.viewmodels.TodoViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddEditTaskDialogFragment extends DialogFragment {

    private DialogAddEditTaskBinding binding;
    private TodoViewModel viewModel;
    private Task existingTask;

    public interface TaskDialogListener {
        void onTaskSaved(Task task);
    }

    public static AddEditTaskDialogFragment newInstance(Task task) {
        return newInstance(task, 0); // Default category ID as 0
    }

    public static AddEditTaskDialogFragment newInstance(Task task, int defaultCategoryId) {
        AddEditTaskDialogFragment fragment = new AddEditTaskDialogFragment();
        Bundle args = new Bundle();
        if (task != null) {
            // Add task details to arguments
            args.putInt("taskId", task.getId());
            args.putString("title", task.getTitle());
            args.putString("description", task.getDescription());
            args.putString("dueDate", task.getDueDate());
            args.putInt("categoryId", task.getCategoryId());
        } else {
            args.putInt("defaultCategoryId", defaultCategoryId);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogAddEditTaskBinding.inflate(LayoutInflater.from(getContext()));
        viewModel = new ViewModelProvider(requireActivity()).get(TodoViewModel.class);

        setupViews();
        loadExistingTask();
        setupCategorySpinner();
        setupDatePicker();
        setupButtons();

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(binding.getRoot())
                .create();
    }

    private void setupViews() {
        binding.closeButton.setOnClickListener(v -> dismiss());

        // Set the button text based on whether we're editing or adding
        boolean isEditing = getArguments() != null && getArguments().containsKey("taskId");
        binding.addEditTaskButton.setText(isEditing ? "Update Task" : "Add Task");
    }

    private void loadExistingTask() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("taskId")) {
            binding.taskTitleInput.setText(args.getString("title"));
            binding.taskDescriptionInput.setText(args.getString("description"));
            binding.taskDueDateInput.setText(args.getString("dueDate"));

            existingTask = new Task(
                    args.getString("title"),
                    args.getString("description"),
                    args.getString("dueDate"),
                    args.getInt("categoryId")
            );
            existingTask.setId(args.getInt("taskId"));

            // Focus on the description field for subtasks
            binding.taskDescriptionInput.requestFocus();
        }
    }

    private void setupCategorySpinner() {
        viewModel.getAllCategories().observe(this, categories -> {
            ArrayAdapter<Category> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    categories
            );
            binding.categorySpinner.setAdapter(adapter);

            // Pre-fill the spinner with the existing category or default to PERSONAL
            if (existingTask != null) {
                for (Category category : categories) {
                    if (category.getId() == existingTask.getCategoryId()) {
                        binding.categorySpinner.setText(category.getName(), false);
                        break;
                    }
                }
            } else {
                // Default to "PERSONAL" if adding a new task
                for (Category category : categories) {
                    if ("PERSONAL".equalsIgnoreCase(category.getName())) {
                        binding.categorySpinner.setText(category.getName(), false);
                        break;
                    }
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private Category getSelectedCategory() {
        String selectedCategoryName = binding.categorySpinner.getText().toString().trim();
        ArrayAdapter<Category> adapter = (ArrayAdapter<Category>) binding.categorySpinner.getAdapter();

        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                Category category = adapter.getItem(i);
                if (category != null && category.getName().equals(selectedCategoryName)) {
                    return category;
                }
            }
        }
        return null; // Return null if no matching category is found
    }

    private void setupDatePicker() {
        binding.taskDueDateInput.setOnClickListener(v -> {
            // For MVP, just set current date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            binding.taskDueDateInput.setText(sdf.format(new Date()));
        });

        // Set current date by default if empty
        if (binding.taskDueDateInput.getText().toString().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            binding.taskDueDateInput.setText(sdf.format(new Date()));
        }
    }

    private void setupButtons() {
        binding.addEditTaskButton.setOnClickListener(v -> {
            String title = binding.taskTitleInput.getText().toString().trim();
            String description = binding.taskDescriptionInput.getText().toString().trim();
            String dueDate = binding.taskDueDateInput.getText().toString().trim();

            if (title.isEmpty()) {
                binding.titleInputLayout.setError("Title is required");
                return;
            }

            // Retrieve the selected category
            Category selectedCategory = getSelectedCategory();
            if (selectedCategory == null) {
                binding.categorySpinner.setError("Please select a valid category");
                return;
            }

            // Update the task or create a new one
            if (existingTask != null) {
                existingTask.setTitle(title);
                existingTask.setDescription(description);
                existingTask.setDueDate(dueDate);
                existingTask.setCategoryId(selectedCategory.getId()); // Update categoryId
            } else {
                existingTask = new Task(title, description, dueDate, selectedCategory.getId());
            }

            // Notify the listener
            TaskDialogListener listener = (TaskDialogListener) requireActivity();
            listener.onTaskSaved(existingTask);
            dismiss();
        });

        // Close button logic
        binding.closeButton.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Clean up the binding
        if (viewModel != null) {
            viewModel.getAllCategories().removeObservers(this);
        }
    }
}
