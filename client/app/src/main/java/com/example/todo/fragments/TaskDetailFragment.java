package com.example.todo.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.adapter.ChooseUserAdapter;
import com.example.todo.adapter.CommentAdapter;
import com.example.todo.custom.CustomToast;
import com.example.todo.model.dto.CommentDto;
import com.example.todo.model.dto.TaskDto;
import com.example.todo.model.dto.UserData;
import com.example.todo.model.request.CommentRequest;
import com.example.todo.utils.HttpClientHelper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskDetailFragment extends Fragment {
    private NavController navController;
    private TextView tvTaskTitle, tvTaskDescription, tvTaskDate, tvTaskPriority;
    private ImageView back, imgSend;
    private RecyclerView rvTaskAssignee;
    private List<UserData> assignees = new ArrayList<>();
    private List<CommentDto> comments = new ArrayList<>();
    private ChooseUserAdapter assigneeAdapter;
    private HttpClientHelper httpClientHelper;
    private ObjectMapper objectMapper;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private ListView lvComment;
    private EditText edtContentMessage;
    private CommentAdapter commentAdapter;
    private Long task;
    private Long organization;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.httpClientHelper = HttpClientHelper.getInstance(getActivity());
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.coercionConfigFor(LogicalType.Enum).setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);
        this.task = getArguments().getLong("task");
        this.organization = getArguments().getLong("organization");
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
        this.back = rootView.findViewById(R.id.detailBack);
        this.lvComment = rootView.findViewById(R.id.lvComment);
        this.edtContentMessage = rootView.findViewById(R.id.edtContentMessage);
        this.imgSend = rootView.findViewById(R.id.imgSend);
        this.setUpAdapter();
        this.getTaskDetail(this.task);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.init(view);
        this.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putLong("organization", organization);
                navController.navigate(R.id.action_taskDetailsFragment_to_mainTaskPageFragment, bundle);
            }
        });
        this.imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = edtContentMessage.getText().toString().trim();
                if (!content.isEmpty()) {
                    class CreateComment extends AsyncTask<CommentRequest, Void, Object> {

                        @Override
                        protected Object doInBackground(CommentRequest... body) {
                            return httpClientHelper.post(httpClientHelper.buildUrl("/task/comment", null), body[0], CommentDto.class);
                        }

                        @Override
                        protected void onPostExecute(Object result) {
                            super.onPostExecute(result);
                            if (result instanceof String) {
                                CustomToast.makeText(getActivity(), result.toString(), CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                            } else {
                                edtContentMessage.setText("");
                                CommentDto commentDto = new CommentDto();
                                commentDto.setContent(content);
                                commentDto.setCreatedAt(new Date());
                                commentDto.setName("You");
                                comments.add(commentDto);
                                setUpAdapter();
                            }
                        }
                    }
                    CreateComment createComment = new CreateComment();
                    createComment.execute(new CommentRequest(content, task));
                }
            }
        });
    }

    private void getTaskDetail(Long task) {
        class GetTaskDetail extends AsyncTask<Long, Void, Object> {

            @Override
            protected Object doInBackground(Long... longs) {
                return httpClientHelper.get(httpClientHelper.buildUrl(String.format("/task/%d", longs[0]), null), TaskDto.class);
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
                    comments = new ArrayList<>(taskDto.getComments());
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

        this.commentAdapter = new CommentAdapter(getActivity(), R.layout.item_comment, this.comments);
        this.lvComment.setAdapter(this.commentAdapter);
    }

    private void init(View view) {
        this.navController = Navigation.findNavController(view);
    }
}
