package com.example.todo.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.custom.CustomToast;
import com.example.todo.model.dto.TaskDto;
import com.example.todo.model.dto.UserData;
import com.example.todo.utils.HttpClientHelper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TaskDetailFragment extends Fragment {
    private NavController navController;
    private TextView tvTaskTitle, tvTaskDescription, tvTaskDate, tvTaskPriority;
    private ImageView back;
    private RecyclerView rvTaskAssignee;
    private List<UserData> assignees = new ArrayList<>();
    private ChooseUserAdapter assigneeAdapter;
    private HttpClientHelper httpClientHelper;
    private ObjectMapper objectMapper;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.httpClientHelper = HttpClientHelper.getInstance(getActivity());
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.coercionConfigFor(LogicalType.Enum).setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_detail, container, false);
        this.tvTaskTitle = rootView.findViewById(R.id.tvTaskTitle);
        this.tvTaskDescription = rootView.findViewById(R.id.tvTaskDescription);
        this.tvTaskDate = rootView.findViewById(R.id.tvTaskDate);
        this.tvTaskPriority = rootView.findViewById(R.id.tvTaskPriority);
        this.rvTaskAssignee = rootView.findViewById(R.id.rvTaskAssignee);
        this.back = rootView.findViewById(R.id.back);
        this.setUpAdapter();
        this.getTaskDetail(getArguments().getLong("task"));
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.init(view);
        this.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.popBackStack();
            }
        });
    }

    private void getTaskDetail(Long task) {
        class GetTaskDetail extends AsyncTask<Long, Void, Object> {

            @Override
            protected Object doInBackground(Long... longs) {
                return httpClientHelper.get(httpClientHelper.buildUrl(String.format("task/%d", longs[0]), null), TaskDto.class);
            }

            @Override
            protected void onPostExecute(Object result) {
                super.onPostExecute(result);
                if (result instanceof String) {
                    CustomToast.makeText(getActivity(), result.toString(), CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                } else {
                    TaskDto taskDto = objectMapper.convertValue(result, TaskDto.class);
                    tvTaskTitle.setText(taskDto.getTitle());
                    tvTaskDescription.setText(taskDto.getDescription());
                    tvTaskPriority.setText(taskDto.getPriority());
                    tvTaskDate.setText(String.format("%s - %s", dateFormat.format(taskDto.getStartDate()), dateFormat.format(taskDto.getEndDate())));
                    assignees = new ArrayList<>(taskDto.getAssignees());
                    setUpAdapter();
                }
            }
        }
        GetTaskDetail getTaskDetail = new GetTaskDetail();
        getTaskDetail.execute(task);
    }

    private void setUpAdapter() {
        this.assigneeAdapter = new ChooseUserAdapter(getActivity(), this.assignees);
        this.rvTaskAssignee.setAdapter(this.assigneeAdapter);
        this.rvTaskAssignee.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void init(View view) {
        this.navController = Navigation.findNavController(view);
    }
}
