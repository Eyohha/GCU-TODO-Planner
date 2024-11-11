package com.todo.planner.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.todo.planner.data.entity.Category;
import com.todo.planner.databinding.DialogCategoryBinding;

public class CategoryDialogFragment extends DialogFragment {

    private DialogCategoryBinding binding;
    private Category existingCategory;

    public interface CategoryDialogListener {
        void onCategorySaved(Category category);
        void onCategoryDeleted(Category category);
    }

    public static CategoryDialogFragment newInstance(Category category) {
        CategoryDialogFragment fragment = new CategoryDialogFragment();
        if (category != null) {
            Bundle args = new Bundle();
            args.putInt("categoryId", category.getId());
            args.putString("name", category.getName());
            fragment.setArguments(args);
        }
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogCategoryBinding.inflate(LayoutInflater.from(getContext()));

        loadExistingCategory();
        setupButtons();

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(binding.getRoot())
                .create();
    }

    private void loadExistingCategory() {
        Bundle args = getArguments();
        if (args != null) {
            existingCategory = new Category(args.getString("name"));
            existingCategory.setId(args.getInt("categoryId"));
            binding.categoryNameInput.setText(existingCategory.getName());
            binding.deleteButton.setVisibility(View.VISIBLE);
        } else {
            binding.deleteButton.setVisibility(View.GONE);
        }
    }

    private void setupButtons() {
        binding.closeButton.setOnClickListener(v -> dismiss());

        binding.saveButton.setOnClickListener(v -> {
            String name = binding.categoryNameInput.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Category name is required", Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }
}
