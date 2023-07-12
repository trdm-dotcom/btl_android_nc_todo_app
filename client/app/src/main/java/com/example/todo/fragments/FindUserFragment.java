package com.example.todo.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.adapter.ChooseUserAdapter;
import com.example.todo.adapter.UserAdapter;
import com.example.todo.common.GeneralException;
import com.example.todo.custom.CustomToast;
import com.example.todo.model.dto.UserData;
import com.example.todo.model.request.AssigneeRequest;
import com.example.todo.model.request.OrganizationMemberRequest;
import com.example.todo.model.response.ListUserResponse;
import com.example.todo.utils.HttpClientHelper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FindUserFragment extends BottomSheetDialogFragment {
    private UserAdapter listAddAdapter, listRemoveAdapter;
    private ChooseUserAdapter chooseAddAdapter, chooseRemoveAdapter;
    private List<UserData> listUserAdd = new ArrayList<>();
    private List<UserData> listUserRemove = new ArrayList<>();
    private List<UserData> listUserChooseAdd = new ArrayList<>();
    private List<UserData> listUserChooseRemove = new ArrayList<>();
    private RecyclerView rvListUserRemove, rvChooseUserRemove, rvListUserAdd, rvChooseUserAdd;
    private HttpClientHelper httpClientHelper;
    private static final String TAG = HomeFragment.class.getSimpleName();
    private Long organizationId;
    private Long taskId;
    private ObjectMapper objectMapper;
    private AppCompatButton btnRemove, btnAdd;

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
        this.rvListUserRemove = rootView.findViewById(R.id.rvListUserRemove);
        this.rvChooseUserRemove = rootView.findViewById(R.id.rvChooseUserRemove);
        this.rvListUserAdd = rootView.findViewById(R.id.rvListUserAdd);
        this.rvChooseUserAdd = rootView.findViewById(R.id.rvChooseUserAdd);
        this.btnAdd = rootView.findViewById(R.id.btn_add);
        this.btnRemove = rootView.findViewById(R.id.btn_remove);
        this.setUpAdapter();
        this.setupChooseAdapter();
        this.getListUser();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listUserChooseAdd == null || listUserChooseAdd.isEmpty()) {
                    CustomToast.makeText(getActivity(), "Please choose user", CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                    return;
                }
                if (taskId != null) {
                    addAssignee();
                } else {
                    addMember();
                }
            }
        });
        this.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listUserChooseRemove == null || listUserChooseRemove.isEmpty()) {
                    CustomToast.makeText(getActivity(), "Please choose user", CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                    return;
                }
                if (taskId != null) {
                    removeAssignee();
                } else {
                    removeMember();
                }
            }
        });
    }

    public void setId(Long organizationId, Long taskId) {
        this.organizationId = organizationId;
        this.taskId = taskId;
    }

    private void getListUser() {
        class GetListUser extends AsyncTask<Void, Void, Object> {
            @Override
            protected Object doInBackground(Void... voids) {
                try {
                    Map<String, Object> map = new HashMap<>();
                    if (organizationId != null) {
                        map.put("organization", organizationId);
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
                    listUserAdd = listUserResponse.getListUser();
                    listUserRemove = listUserResponse.getChooseUser();
                    setUpAdapter();
                }
            }
        }

        GetListUser getListUser = new GetListUser();
        getListUser.execute();
    }

    private void addAssignee() {
        class addAssignee extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    AssigneeRequest assigneeRequest = new AssigneeRequest();
                    assigneeRequest.setAssignee(listUserChooseAdd.stream().map(UserData::getId).collect(Collectors.toList()));
                    assigneeRequest.setTask(taskId);
                    httpClientHelper.put(httpClientHelper.buildUrl("/task/assignee", null), assigneeRequest, Object.class);
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
                if (result == null) {
                    dismiss();
                } else {
                    CustomToast.makeText(getActivity(), result, CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                }
            }
        }

        addAssignee addAssignee = new addAssignee();
        addAssignee.execute();
    }

    private void removeAssignee() {
        class RemoveAssignee extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    AssigneeRequest assigneeRequest = new AssigneeRequest();
                    assigneeRequest.setAssignee(listUserChooseAdd.stream().map(UserData::getId).collect(Collectors.toList()));
                    assigneeRequest.setTask(taskId);
                    httpClientHelper.post(httpClientHelper.buildUrl("/task/assignee/remove", null), assigneeRequest, Object.class);
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
                if (result == null) {
                    dismiss();
                } else {
                    CustomToast.makeText(getActivity(), result, CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                }
            }
        }

        RemoveAssignee removeAssignee = new RemoveAssignee();
        removeAssignee.execute();
    }

    private void addMember() {
        class AddMember extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    OrganizationMemberRequest memberRequest = new OrganizationMemberRequest();
                    memberRequest.setUserId(listUserChooseAdd.stream().map(UserData::getId).collect(Collectors.toList()));
                    memberRequest.setOrganizationId(organizationId);
                    httpClientHelper.put(httpClientHelper.buildUrl("/organization/member", null), memberRequest, Object.class);
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
                if (result == null) {
                    dismiss();
                } else {
                    CustomToast.makeText(getActivity(), result, CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                }
            }
        }

        AddMember addMember = new AddMember();
        addMember.execute();
    }

    private void removeMember() {
        class RemoveMember extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    OrganizationMemberRequest memberRequest = new OrganizationMemberRequest();
                    memberRequest.setUserId(listUserChooseAdd.stream().map(UserData::getId).collect(Collectors.toList()));
                    memberRequest.setOrganizationId(organizationId);
                    httpClientHelper.post(httpClientHelper.buildUrl("/organization/member/remove", null), memberRequest, Object.class);
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
                if (result == null) {
                    dismiss();
                } else {
                    CustomToast.makeText(getActivity(), result, CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                }
            }
        }

        RemoveMember removeMember = new RemoveMember();
        removeMember.execute();
    }

    private void setUpAdapter() {
        this.listAddAdapter = new UserAdapter(getActivity(), this.listUserAdd);
        this.rvListUserAdd.setAdapter(this.listAddAdapter);
        this.rvListUserAdd.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        this.listAddAdapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserData item, int position) {
                Log.i(TAG, "onItemClick: " + item.getId());
                listUserChooseAdd.add(item);
                chooseAddAdapter.notifyDataSetChanged();
            }
        });

        this.listRemoveAdapter = new UserAdapter(getActivity(), this.listUserRemove);
        this.rvListUserRemove.setAdapter(this.listRemoveAdapter);
        this.rvListUserRemove.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        this.listRemoveAdapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserData item, int position) {
                Log.i(TAG, "onItemClick: " + item.getId());
                listUserChooseRemove.add(item);
                Log.i(TAG, "onItemClick: " + listUserChooseRemove);
                chooseRemoveAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setupChooseAdapter() {
        this.chooseAddAdapter = new ChooseUserAdapter(getActivity(), this.listUserChooseAdd);
        this.rvChooseUserAdd.setAdapter(this.chooseAddAdapter);
        this.rvChooseUserAdd.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        this.chooseAddAdapter.setOnItemClickListener(new ChooseUserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserData item, int position) {
                listUserChooseAdd.remove(position);
                Log.i(TAG, "onItemClick: " + listUserChooseAdd);
                chooseAddAdapter.notifyDataSetChanged();
            }
        });

        this.chooseRemoveAdapter = new ChooseUserAdapter(getActivity(), this.listUserChooseRemove);
        this.rvChooseUserRemove.setAdapter(this.chooseRemoveAdapter);
        this.rvChooseUserRemove.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        this.chooseRemoveAdapter.setOnItemClickListener(new ChooseUserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserData item, int position) {
                listUserChooseRemove.remove(position);
                chooseRemoveAdapter.notifyDataSetChanged();
            }
        });
    }
}
