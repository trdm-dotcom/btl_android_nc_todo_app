package com.example.todo.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.example.todo.R;
import com.example.todo.common.GeneralException;
import com.example.todo.custom.CustomToast;
import com.example.todo.model.dto.OrganizationDto;
import com.example.todo.model.dto.TaskDto;
import com.example.todo.model.dto.UserData;
import com.example.todo.model.request.TaskRequest;
import com.example.todo.model.response.ListUserResponse;
import com.example.todo.utils.HttpClientHelper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TaskFormFragment extends BottomSheetDialogFragment {
    private AutoCompleteTextView autoCompleteTextView;
    private TextInputEditText taskStartEdt, taskEndEdt, taskTitleEdt, taskDesEdt;
    private Calendar calendarStart, calendarEnd;
    private ImageView nextBtn;
    private HttpClientHelper httpClientHelper;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private setRefreshListener setRefreshListener;
    private static final String TAG = SignUpFragment.class.getSimpleName();
    private Long taskId;
    private boolean isEdit;
    private Context context;
    private Long organization;
    private ObjectMapper objectMapper;
    private List<UserData> list = new ArrayList<>();
    private List<String> assigneeList = new ArrayList<>();
    private ListView lvAssignees;

    @Override
    public void onCreate(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.httpClientHelper = HttpClientHelper.getInstance(this.context);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.coercionConfigFor(LogicalType.Enum).setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_form, container, false);
        this.taskStartEdt = rootView.findViewById(R.id.taskStartDateEdt);
        this.taskEndEdt = rootView.findViewById(R.id.taskEndDateEdt);
        this.autoCompleteTextView = rootView.findViewById(R.id.priorityDropdownMenu);
        this.nextBtn = rootView.findViewById(R.id.nextBtn);
        this.taskTitleEdt = rootView.findViewById(R.id.taskTitleEdt);
        this.taskDesEdt = rootView.findViewById(R.id.taskDesEdt);
        this.calendarStart = Calendar.getInstance();
        this.calendarEnd = Calendar.getInstance();
        this.calendarEnd.add(Calendar.DAY_OF_MONTH, 7);
        this.taskStartEdt.setText(dateFormat.format(this.calendarStart.getTime()));
        this.taskEndEdt.setText(dateFormat.format(this.calendarEnd.getTime()));
        this.lvAssignees = rootView.findViewById(R.id.lvAssignees);
        if (isEdit) {
            this.getTask(taskId);
        }
        this.setUpAdapter();
        this.getListUser();
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
            public void onClick(View view) {
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
            public void onClick(View view) {
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

    public void setTaskId(Long taskId, Long organization, boolean isEdit, setRefreshListener setRefreshListener, Context context) {
        this.taskId = taskId;
        this.isEdit = isEdit;
        this.context = context;
        this.organization = organization;
        this.setRefreshListener = setRefreshListener;
    }

    private void saveTask() {
        class SaveTaskInBackend extends AsyncTask<Void, Void, String> {
            ProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = ProgressDialog.show(getActivity(), "", "");
            }

            @Override
            protected String doInBackground(Void... voids) {
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                SparseBooleanArray checked = lvAssignees.getCheckedItemPositions();
                TaskRequest body = new TaskRequest();
                body.setTitle(taskTitleEdt.getText().toString().trim());
                body.setDescription(taskDesEdt.getText().toString().trim());
                body.setPriority(autoCompleteTextView.getText().toString().trim());
                body.setStartDate(df.format(calendarStart.getTime()));
                body.setEndDate(df.format(calendarEnd.getTime()));
                body.setOrganizationId(organization);
                body.setAssignees(IntStream.range(0, lvAssignees.getCount())
                        .filter(i -> checked.get(i))
                        .mapToObj(i -> list.get(i).getId()).collect(Collectors.toSet()));
                try {
                    httpClientHelper.post(
                            httpClientHelper.buildUrl("/task", null),
                            body,
                            Object.class);
                    return null;
                } catch (Exception e) {
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
                mProgressDialog.dismiss();
                if (result == null) {
                    setRefreshListener.refresh();
                    dismiss();
                } else {
                    CustomToast.makeText(context, result, CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                }
            }
        }
        SaveTaskInBackend saveTaskInBackend = new SaveTaskInBackend();
        saveTaskInBackend.execute();
    }

    private void getTask(Long id) {
        class GetTaskInBackend extends AsyncTask<Long, Void, Object> {
            ProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = ProgressDialog.show(getActivity(), "", "");
            }

            @Override
            protected Object doInBackground(Long... ids) {
                try {
                    return httpClientHelper.get(
                            httpClientHelper.buildUrl(String.format("/task/%d", ids), null),
                            OrganizationDto.class);
                } catch (Exception e) {
                    Log.e(TAG, "error: ", e);
                    if (e instanceof GeneralException) {
                        return ((GeneralException) e).getCode();
                    }
                    return "ERROR";
                }
            }

            @Override
            protected void onPostExecute(Object result) {
                super.onPostExecute(result);
                mProgressDialog.dismiss();
                if (result instanceof TaskDto) {
                    TaskDto taskDto = objectMapper.convertValue(result, TaskDto.class);
                    taskTitleEdt.setText(taskDto.getTitle());
                    taskDesEdt.setText(taskDto.getDescription());
                    taskStartEdt.setText(dateFormat.format(taskDto.getStartDate()));
                    taskEndEdt.setText(dateFormat.format(taskDto.getEndDate()));
                    calendarStart.setTime(taskDto.getStartDate());
                    calendarEnd.setTime(taskDto.getEndDate());
                } else {
                    CustomToast.makeText(context, result.toString(), CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                }
            }
        }
        GetTaskInBackend getTaskInBackend = new GetTaskInBackend();
        getTaskInBackend.execute(id);
    }

    private void getListUser() {
        class GetListUser extends AsyncTask<Void, Void, Object> {
            @Override
            protected Object doInBackground(Void... voids) {
                try {
                    Map<String, Object> map = new HashMap<>();
                    if (organization != null) {
                        map.put("organization", organization);
                    }
                    if (taskId != null) {
                        map.put("task", taskId);
                    }
                    return httpClientHelper.get(httpClientHelper.buildUrl("/user/find-all", map), Object.class);
                } catch (Exception e) {
                    Log.e(TAG, "error: ", e);
                    if (e instanceof GeneralException) {
                        return ((GeneralException) e).getCode();
                    }
                    return "ERROR";
                }
            }

            @Override
            protected void onPostExecute(Object result) {
                super.onPostExecute(result);
                if (result instanceof String) {
                    CustomToast.makeText(getActivity(), result.toString(), CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                } else {
                    ListUserResponse listUserResponse = objectMapper.convertValue(result, ListUserResponse.class);
                    list = listUserResponse.getListUser();
                    assigneeList = list.stream().map(UserData::getName).collect(Collectors.toList());
                    setUpAdapter();
                }
            }
        }

        GetListUser getListUser = new GetListUser();
        getListUser.execute();
    }

    private void setUpAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.context,
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.priority_dropdown_items));
        this.autoCompleteTextView.setAdapter(adapter);

        ArrayAdapter<String> userAdapter = new ArrayAdapter<>(this.context,
                android.R.layout.simple_list_item_multiple_choice,
                this.assigneeList);
        this.lvAssignees.setAdapter(userAdapter);
    }

    private boolean validateForm() {
        boolean isValid = true;
        String title = this.taskTitleEdt.getText().toString().trim();
        Date now = new Date();
        Date startDate = calendarStart.getTime();
        Date endDate = calendarEnd.getTime();
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
