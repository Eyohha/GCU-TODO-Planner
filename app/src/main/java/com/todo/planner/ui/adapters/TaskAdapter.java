package com.todo.planner.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.todo.planner.R;
import com.todo.planner.data.entity.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    private final TaskActionListener listener;

    public interface TaskActionListener {
        void onTaskCheckedChanged(Task task, boolean isChecked);
        void onTaskEdit(Task task);
        void onTaskDelete(Task task);
        void onAddSubtask(Task task);
        void onMarkTaskUndone(Task task);
        void onRemoveCompletedTask(Task task);
    }

    public TaskAdapter(TaskActionListener listener) {
        super(new DiffUtil.ItemCallback<Task>() {
            @Override
            public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                return oldItem.getTitle().equals(newItem.getTitle()) &&
                        oldItem.getDescription().equals(newItem.getDescription()) &&
                        oldItem.isCompleted() == newItem.isCompleted() &&
                        oldItem.getCategoryId() == newItem.getCategoryId();
            }
        });
        this.listener = listener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        Task task = getItem(position);
        return task.getId();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = getItem(position);
        holder.bind(task);
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView descriptionText;
        private final TextView dateText;
        private final CheckBox checkBox;
        private final ImageButton menuButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.taskTitle);
            descriptionText = itemView.findViewById(R.id.taskDescription);
            dateText = itemView.findViewById(R.id.taskDate);
            checkBox = itemView.findViewById(R.id.taskCheckbox);
            menuButton = itemView.findViewById(R.id.taskMenu);
        }

        void bind(Task task) {
            // Update the UI immediately
            updateTaskUI(task);
            titleText.setText(task.getTitle());
//            if (task.getDescription() != null && !task.getDescription().isEmpty()) {
//                descriptionText.setVisibility(View.VISIBLE);
//                descriptionText.setText(task.getDescription());
//            } else {
//                descriptionText.setVisibility(View.GONE);
//            }
            Log.d("TaskAdapter", "Title visibility: " + titleText.getVisibility());
            // Prevent duplicate listener invocation
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(task.isCompleted());

            // Handle checkbox clicks
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Log.d("CheckBox", "Checkbox clicked for task: " + task.getTitle() + ", isChecked: " + isChecked);

                // Optimistic UI update
                updateTaskUI(task);
                listener.onTaskCheckedChanged(task, isChecked);
            });

            // Setup menu actions
            menuButton.setOnClickListener(v -> setupTaskMenu(task));
        }

        private void updateTaskUI(Task task) {
            if (task.isCompleted()) {
                titleText.setAlpha(0.5f);
                dateText.setText("Completed: " + task.getCompletedDate());
            } else {
                titleText.setAlpha(1.0f);
                dateText.setText("Created: " + task.getDueDate());
            }
        }

        private void setupTaskMenu(Task task) {
            PopupMenu popup = new PopupMenu(itemView.getContext(), menuButton);

            if (task.isCompleted()) {
                popup.inflate(R.menu.menu_completed_task);
                popup.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.action_mark_undone) {
                        listener.onMarkTaskUndone(task);
                        return true;
                    } else if (itemId == R.id.action_remove) {
                        listener.onRemoveCompletedTask(task);
                        return true;
                    }
                    return false;
                });
            } else {
                popup.inflate(R.menu.menu_pending_task);
                popup.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.action_edit) {
                        listener.onTaskEdit(task);
                        return true;
                    } else if (itemId == R.id.action_delete) {
                        listener.onTaskDelete(task);
                        return true;
                    }
                    return false;
                });
            }

            popup.show();
        }
    }
}
