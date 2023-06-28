package com.example.todo.fragments;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todo.R;
import com.example.todo.common.GeneralException;
import com.example.todo.custom.CustomToast;
import com.example.todo.model.dto.OrganizationDto;
import com.example.todo.model.dto.TaskDto;
import com.example.todo.model.request.TaskRequest;
import com.example.todo.utils.HttpClientHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TaskFormFragment extends BottomSheetDialogFragment {
    private AutoCompleteTextView autoCompleteTextView;
    private TextInputEditText taskStartEdt, taskEndEdt, taskTitleEdt, taskDesEdt;
    private Switch taskReminderSw;
    private Calendar calendarStart, calendarEnd;
    private ImageView nextBtn;
    private HttpClientHelper httpClientHelper;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    OrganizationFormFragment.setRefreshListener setRefreshListener;
    private static final String TAG = SignUpFragment.class.getSimpleName();
    @Override
    public void onCreate(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.httpClientHelper = HttpClientHelper.getInstance(getActivity());
        if(getArguments() != null) {
            getTask(getArguments().getLong("id"));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_form, container, false);
        this.taskStartEdt = rootView.findViewById(R.id.taskEndDateEdt);
        this.taskEndEdt = rootView.findViewById(R.id.taskEndDateEdt);
        this.autoCompleteTextView = rootView.findViewById(R.id.priorityDropdownMenu);
        this.nextBtn = rootView.findViewById(R.id.nextBtn);
        this.taskTitleEdt = rootView.findViewById(R.id.taskTitleEdt);
        this.taskDesEdt = rootView.findViewById(R.id.taskDesEdt);
        this.taskReminderSw = rootView.findViewById(R.id.taskReminderSw);
        this.calendarStart = Calendar.getInstance();
        this.calendarEnd = Calendar.getInstance();
        this.calendarEnd.add(Calendar.DAY_OF_MONTH, 7);
        this.taskStartEdt.setHint(dateFormat.format(this.calendarStart.getTime()));
        this.taskStartEdt.setHint(dateFormat.format(this.calendarEnd.getTime()));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.priority_dropdown_items));
        this.autoCompleteTextView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    saveTask();
                }
            }
        });
        this.taskStartEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                calendarStart.set(Calendar.YEAR, year);
                                calendarStart.set(Calendar.MONTH, monthOfYear);
                                calendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                String selectedDate = dateFormat.format(calendarStart.getTime());
                                taskStartEdt.setText(selectedDate);
                            }
                        },
                        calendarStart.get(Calendar.YEAR),
                        calendarStart.get(Calendar.MONTH),
                        calendarStart.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            }
        });
        this.taskEndEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                calendarEnd.set(Calendar.YEAR, year);
                                calendarEnd.set(Calendar.MONTH, monthOfYear);
                                calendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                String selectedDate = dateFormat.format(calendarEnd.getTime());
                                taskEndEdt.setText(selectedDate);
                            }
                        },
                        calendarEnd.get(Calendar.YEAR),
                        calendarEnd.get(Calendar.MONTH),
                        calendarEnd.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            }
        });
    }

    private void saveTask() {
        class SaveTaskInBackend extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                TaskRequest body = new TaskRequest();
                body.setTitle(taskTitleEdt.getText().toString().trim());
                body.setDescription(taskDesEdt.getText().toString().trim());
                body.setPriority(autoCompleteTextView.getText().toString().trim());
                body.setRemind(taskReminderSw.isChecked());
                body.setStartDate(dateFormat.format(calendarStart.getTime()));
                body.setEndDate(dateFormat.format(calendarEnd.getTime()));
                try {
                    httpClientHelper.post(
                            httpClientHelper.buildUrl("task", null),
                            body,
                            Object.class);
                    return null;
                } catch (GeneralException e) {
                    Log.e(TAG, "error: ", e);
                    if (e instanceof GeneralException) {
                        return ((GeneralException) e).getCode();
                    }
                    return "ERROR";
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result.isEmpty()) {
                    setRefreshListener.refresh();
                    dismiss();
                } else {
                    CustomToast.makeText(getActivity(), result, CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                }
            }
        }
        SaveTaskInBackend saveTaskInBackend = new SaveTaskInBackend();
        saveTaskInBackend.execute();
    }

    private void getTask(Long id) {
        class GetTaskInBackend extends AsyncTask<Long, Void, Object> {
            @Override
            protected Object doInBackground(Long... ids) {
                try {
                    return httpClientHelper.get(
                            httpClientHelper.buildUrl(String.format("task/%d", ids), null),
                            OrganizationDto.class);
                } catch (GeneralException e) {
                    return e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(Object result) {
                super.onPostExecute(result);
                if (result instanceof TaskDto) {
                    taskTitleEdt.setText(((TaskDto) result).getTitle());
                    taskDesEdt.setText(((TaskDto) result).getDescription());
                    taskReminderSw.setChecked(((TaskDto) result).getReminder());
                    taskStartEdt.setText(dateFormat.format(((TaskDto) result).getStartDate()));
                    taskEndEdt.setText(dateFormat.format(((TaskDto) result).getEndDate()));
                    calendarStart.setTime(((TaskDto) result).getStartDate());
                    calendarEnd.setTime(((TaskDto) result).getEndDate());
                } else {
                    CustomToast.makeText(getActivity(), result.toString(), CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                }
            }
        }
        GetTaskInBackend getTaskInBackend = new GetTaskInBackend();
        getTaskInBackend.execute(id);
    }

    private boolean validateForm() {
        boolean isValid = true;
        String title = this.taskTitleEdt.getText().toString().trim();
        Date now = new Date();
        Date startDate = new Date(this.taskStartEdt.getText().toString().trim());
        Date endDate = new Date(this.taskEndEdt.getText().toString().trim());
        if (title.isEmpty()) {
            this.taskTitleEdt.setError("Title is required");
            isValid = false;
        }
        if (startDate.before(now)) {
            this.taskStartEdt.setError("Start date must be after today");
            isValid = false;
        }
        if (endDate.before(startDate)) {
            this.taskEndEdt.setError("End date must be after start date");
            isValid = false;
        }
        if (startDate.after(endDate)) {
            this.taskStartEdt.setError("Start date must be before end date");
            isValid = false;
        }
        return isValid;
    }

    public interface setRefreshListener {
        void refresh();
    }
}
