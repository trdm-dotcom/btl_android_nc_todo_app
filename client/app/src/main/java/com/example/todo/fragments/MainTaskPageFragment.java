package com.example.todo.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.common.GeneralException;
import com.example.todo.custom.CustomToast;
import com.example.todo.model.dto.TaskDto;
import com.example.todo.utils.HttpClientHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainTaskPageFragment extends Fragment implements OrganizationFormFragment.setRefreshListener {
    private NavController navController;
    private HttpClientHelper httpClientHelper;
    private ImageView calendar, back;
    private TaskAdapter taskAdapter;
    private List<TaskDto> tasks = new ArrayList<>();
    private RecyclerView taskRecycler;
    private TextView addTask;
    private Long organization;
    private static final String TAG = MainTaskPageFragment.class.getSimpleName();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private MaterialDatePicker materialDatePicker;
    private Date startDate, endDate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.httpClientHelper = HttpClientHelper.getInstance(getActivity());
        this.organization = (getArguments().getLong("organization"));
        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        this.materialDatePicker = materialDateBuilder.build();
        Calendar calendar = Calendar.getInstance();
        this.startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        this.endDate = calendar.getTime();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_task_page, container, false);
        this.calendar = rootView.findViewById(R.id.calendar);
        this.taskRecycler = rootView.findViewById(R.id.mainRecyclerView);
        this.addTask = rootView.findViewById(R.id.addTask);
        this.back = rootView.findViewById(R.id.back);
        this.getSavedTasks(this.startDate, this.endDate);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.init(view);
        this.calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                if (selection != null) {
                    Pair<Long, Long> selectionDate = (Pair<Long, Long>) selection;
                    startDate = new Date(selectionDate.first);
                    if (selectionDate.second != null) {
                        endDate = new Date(selectionDate.second);
                    } else {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(startDate);
                        calendar.add(Calendar.DAY_OF_MONTH, 7);
                        endDate = calendar.getTime();
                    }
                    Log.i(TAG, "onPositiveButtonClick: " + startDate + " " + endDate);
                    getSavedTasks(startDate, endDate);
                }
            }
        });
        this.addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskFormFragment taskFormFragment = new TaskFormFragment();
                taskFormFragment.setTaskId(0L, organization, false, new TaskFormFragment.setRefreshListener() {
                    @Override
                    public void refresh() {
                        getSavedTasks(startDate, endDate);
                    }
                }, getActivity());
                taskFormFragment.show(getFragmentManager(), taskFormFragment.getTag());
            }
        });
        this.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_mainTaskPageFragment_to_homeFragment);
            }
        });
    }

    private void setUpAdapter() {
        this.taskAdapter = new TaskAdapter(getActivity(), tasks, this::refresh);
        this.taskAdapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TaskDto item) {
            }
        });
        this.taskAdapter.setOnItemLongClickListener(new TaskAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(TaskDto item, int position) {
                TaskOptionsFragment taskOptionsFragment = new TaskOptionsFragment();
                taskOptionsFragment.setTaskId(item.getId(), organization, position, new TaskOptionsFragment.setRefreshListener() {
                    @Override
                    public void refresh() {
                        getSavedTasks(startDate, endDate);
                    }
                }, getActivity());
                taskOptionsFragment.show(getFragmentManager(), taskOptionsFragment.getTag());
            }
        });
        this.taskRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.taskRecycler.setAdapter(this.taskAdapter);
    }

    private void getSavedTasks(Date startDate, Date endDate) {
        class GetSavedTasks extends AsyncTask<Date, Void, Object> {
            ProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = ProgressDialog.show(getActivity(), "", "");
            }

            @Override
            protected Object doInBackground(Date... dates) {
                try {
                    Map<String, Object> queryParams = new HashMap<>();
                    queryParams.put("start", dateFormat.format(dates[0]));
                    if (dates[1] != null) {
                        queryParams.put("end", dateFormat.format(dates[1]));
                    }
                    queryParams.put("organization", organization);
                    return httpClientHelper.get(httpClientHelper.buildUrl("/task", queryParams), new TypeReference<List<TaskDto>>() {
                    });
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
                if (result instanceof String) {
                    CustomToast.makeText(getActivity(), result.toString(), CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                } else {
                    tasks = (List<TaskDto>) result;
                }
                setUpAdapter();
            }
        }
        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute(startDate, endDate);
    }

    private void init(View view) {
        this.navController = Navigation.findNavController(view);
    }

    @Override
    public void refresh() {
        getSavedTasks(this.startDate, this.endDate);
    }
}
