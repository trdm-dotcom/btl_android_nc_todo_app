package com.example.todo.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.common.GeneralException;
import com.example.todo.custom.CustomToast;
import com.example.todo.model.dto.UserData;
import com.example.todo.model.response.ListUserResponse;
import com.example.todo.utils.HttpClientHelper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindUserFragment extends BottomSheetDialogFragment {
    private UserAdapter userAdapter, chooseUserAdapter;
    private List<UserData> userList, chooseUserList;
    private RecyclerView listUser, chooseUser;
    private HttpClientHelper httpClientHelper;
    private static final String TAG = HomeFragment.class.getSimpleName();
    private Long organizationId;
    private Long taskId;
    private Context context;
    private ObjectMapper objectMapper;

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
        View rootView = inflater.inflate(R.layout.fragment_find_user, container, false);
        this.listUser = rootView.findViewById(R.id.listUser);
        this.chooseUser = rootView.findViewById(R.id.chooseUser);
        this.getListUser();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setId(Long organizationId, Long taskId, Context context) {
        this.organizationId = organizationId;
        this.taskId = taskId;
        this.context = context;
    }

    private void getListUser() {
        class GetListUser extends AsyncTask<Void, Void, Object> {
            @Override
            protected Object doInBackground(Void... voids) {
                try {
                    Map<String, Object> map = new HashMap<>();
                    if (organizationId != null) {
                        map.put("organizationId", organizationId);
                    } else if (taskId != null) {
                        map.put("taskId", taskId);
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
                    CustomToast.makeText(context, result.toString(), CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                } else {
                    ListUserResponse listUserResponse = objectMapper.convertValue(result, ListUserResponse.class);
                    userList = listUserResponse.getListUser();
                    chooseUserList = listUserResponse.getChooseUser();
                }
                setUpAdapter();
            }
        }

        GetListUser getListUser = new GetListUser();
        getListUser.execute();
    }

    private void setUpAdapter() {
        this.userAdapter = new UserAdapter(getActivity(), userList);
        this.listUser.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.listUser.setAdapter(this.userAdapter);

        this.chooseUserAdapter = new UserAdapter(getActivity(), chooseUserList);
        this.chooseUser.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.chooseUser.setAdapter(this.chooseUserAdapter);
    }
}
