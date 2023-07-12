package com.example.todo.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.adapter.OrganizationAdapter;
import com.example.todo.common.GeneralException;
import com.example.todo.custom.CustomToast;
import com.example.todo.model.dto.OrganizationDto;
import com.example.todo.model.dto.UserData;
import com.example.todo.utils.HttpClientHelper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OrganizationFormFragment.setRefreshListener {
    private NavController navController;
    private OrganizationAdapter organizationAdapter;
    private List<OrganizationDto> list = new ArrayList<>();
    private RecyclerView taskRecycler;
    private HttpClientHelper httpClientHelper;
    private TextView name;
    private static final String TAG = HomeFragment.class.getSimpleName();
    private FloatingActionButton btnAdd;
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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        this.taskRecycler = rootView.findViewById(R.id.mainRecyclerView);
        this.name = rootView.findViewById(R.id.name);
        this.btnAdd = rootView.findViewById(R.id.addOrg);
        this.setUpAdapter();
        this.getUserInfo();
        this.getSavedOrganizations();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.init(view);
        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrganizationFormFragment organizationFormFragment = new OrganizationFormFragment();
                organizationFormFragment.setOrgId(0L, false, new OrganizationFormFragment.setRefreshListener() {
                    @Override
                    public void refresh() {
                        getSavedOrganizations();
                    }
                }, getActivity());
                organizationFormFragment.show(getParentFragmentManager(), organizationFormFragment.getTag());
            }
        });
    }

    private void setUpAdapter() {
        Log.i(TAG, "list: " + list);
        this.organizationAdapter = new OrganizationAdapter(getActivity(), list, this::refresh);
        this.organizationAdapter.setOnItemClickListener(new OrganizationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(OrganizationDto item) {
                Bundle bundle = new Bundle();
                bundle.putLong("organization", item.getId());
                navController.navigate(R.id.action_homeFragment_to_mainTaskPageFragment, bundle);
            }
        });
        this.taskRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        this.taskRecycler.setAdapter(organizationAdapter);
    }

    private void getSavedOrganizations() {
        class GetSavedOrganizations extends AsyncTask<Void, Void, Object> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Object doInBackground(Void... voids) {
                try {
                    return httpClientHelper.get(httpClientHelper.buildUrl("/organization", null),
                            new TypeReference<List<OrganizationDto>>() {
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
                if (result instanceof String) {
                    CustomToast.makeText(getActivity(), result.toString(), CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                } else {
                    list = objectMapper.convertValue(result, new TypeReference<List<OrganizationDto>>() {
                    });
                    setUpAdapter();
                }
            }
        }
        GetSavedOrganizations getSavedOrganizations = new GetSavedOrganizations();
        getSavedOrganizations.execute();
    }

    private void getUserInfo() {
        class GetUserInfo extends AsyncTask<Void, Void, Object> {

            @Override
            protected Object doInBackground(Void... voids) {
                try {
                    return httpClientHelper.get(httpClientHelper.buildUrl("/user", null),
                            UserData.class);
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
                    UserData userData = objectMapper.convertValue(result, UserData.class);
                    name.setText(userData.getName());
                }
            }
        }
        GetUserInfo getUserInfo = new GetUserInfo();
        getUserInfo.execute();
    }

    private void init(View view) {
        this.navController = Navigation.findNavController(view);
    }

    @Override
    public void refresh() {
        this.getSavedOrganizations();
    }
}
