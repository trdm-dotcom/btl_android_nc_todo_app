package com.example.todo.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todo.R;
import com.example.todo.common.GeneralException;
import com.example.todo.custom.CustomToast;
import com.example.todo.model.dto.OrganizationDto;
import com.example.todo.model.request.OrganizationRequest;
import com.example.todo.utils.HttpClientHelper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

public class OrganizationFormFragment extends BottomSheetDialogFragment {
    private TextInputEditText orgNameEdt;
    private ImageView nextBtn;
    private HttpClientHelper httpClientHelper;
    private setRefreshListener setRefreshListener;
    private static final String TAG = SignUpFragment.class.getSimpleName();
    private long orgId;
    private boolean isEdit;
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
        View rootView = inflater.inflate(R.layout.fragment_organization_form, container, false);
        this.orgNameEdt = rootView.findViewById(R.id.orgNameEdt);
        this.nextBtn = rootView.findViewById(R.id.nextBtn);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    saveOrganization();
                }
            }
        });
    }

    public void setOrgId(long orgId, boolean isEdit, OrganizationFormFragment.setRefreshListener setRefreshListener, Context context) {
        this.orgId = orgId;
        this.isEdit = isEdit;
        this.setRefreshListener = setRefreshListener;
        this.context = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void saveOrganization() {
        class SaveOrganizationInBackend extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    OrganizationRequest body = new OrganizationRequest();
                    body.setName(orgNameEdt.getText().toString().trim());
                    httpClientHelper.post(
                            httpClientHelper.buildUrl("/organization/create", null),
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
                if (result == null) {
                    setRefreshListener.refresh();
                    dismiss();
                } else {
                    CustomToast.makeText(getActivity(), result, CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                }
            }
        }
        SaveOrganizationInBackend saveTaskInBackend = new SaveOrganizationInBackend();
        saveTaskInBackend.execute();
    }

    private void getOrganization(Long id) {
        class GetOrganizationInBackend extends AsyncTask<Long, Void, Object> {
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
                            httpClientHelper.buildUrl(String.format("organization/%d", ids), null),
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
                if (result instanceof OrganizationDto) {
                    OrganizationDto organizationDto = objectMapper.convertValue(result, OrganizationDto.class);
                    orgNameEdt.setText(organizationDto.getName());
                } else {
                    CustomToast.makeText(getActivity(), result.toString(), CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                }
            }
        }

        GetOrganizationInBackend getOrganizationInBackend = new GetOrganizationInBackend();
        getOrganizationInBackend.execute(id);
    }

    private boolean validateForm() {
        boolean isValid = true;
        String name = this.orgNameEdt.getText().toString().trim();
        if (name.isEmpty()) {
            this.orgNameEdt.setError("Please enter name");
            isValid = false;
        }
        return isValid;
    }

    public interface setRefreshListener {
        void refresh();
    }
}
