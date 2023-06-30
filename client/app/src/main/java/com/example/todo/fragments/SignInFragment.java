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
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.todo.R;
import com.example.todo.common.GeneralException;
import com.example.todo.custom.CustomToast;
import com.example.todo.model.dto.UserData;
import com.example.todo.model.request.LoginRequest;
import com.example.todo.model.response.AuthenticationResponse;
import com.example.todo.utils.HttpClientHelper;
import com.google.android.material.textfield.TextInputEditText;

public class SignInFragment extends Fragment {
    private NavController navController;
    private TextView textViewSignup;
    private ImageView nextBtn;
    private TextInputEditText mailEdt, passEdt;
    private HttpClientHelper httpClientHelper;
    private static final String TAG = SignInFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.httpClientHelper = HttpClientHelper.getInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_in, container, false);
        this.textViewSignup = rootView.findViewById(R.id.textViewSignUp);
        this.mailEdt = rootView.findViewById(R.id.mailEdt);
        this.passEdt = rootView.findViewById(R.id.passEdt);
        this.nextBtn = rootView.findViewById(R.id.nextBtn);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.init(view);
        this.textViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_signInFragment_to_signUpFragment);
            }
        });
        this.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    signIn();
                }
            }
        });
    }

    private void signIn() {
        class SignInBackend extends AsyncTask<Void, Void, Object> {
            ProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = ProgressDialog.show(getActivity(), "", "");
            }

            @Override
            protected Object doInBackground(Void... voids) {
                try {
                    LoginRequest loginRequest = new LoginRequest();
                    loginRequest.setClientSecret("8VT9s8b0vX");
                    loginRequest.setEmail(mailEdt.getText().toString().trim());
                    loginRequest.setPassword(passEdt.getText().toString().trim());
                    return httpClientHelper.post(httpClientHelper.buildUrl("/auth/login", null),
                            loginRequest,
                            AuthenticationResponse.class);
                } catch (Exception e) {
                    Log.e(TAG, "error: ", e);
                    if (e instanceof GeneralException) {
                        return ((GeneralException) e).getCode();
                    }
                    return "ERROR";
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                mProgressDialog.dismiss();
                if (o instanceof AuthenticationResponse) {
                    AuthenticationResponse response = (AuthenticationResponse) o;
                    httpClientHelper.setAccessToken(response.getAccessToken());
                    httpClientHelper.setRefreshToken(response.getRefreshToken());
                    UserData userData = response.getUserData();
                    navController.navigate(R.id.action_signInFragment_to_homeFragment);
                } else if (o instanceof String) {
                    CustomToast.makeText(getActivity(), o.toString(), CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                }
            }
        }

        SignInBackend signInBackend = new SignInBackend();
        signInBackend.execute();
    }

    private boolean validateForm() {
        boolean isValid = true;
        String password = this.passEdt.getText().toString().trim();
        String mail = this.mailEdt.getText().toString().trim();
        if (mail.isEmpty()) {
            this.mailEdt.setError("Please enter your mail");
            isValid = false;
        }
        if (password.isEmpty()) {
            this.passEdt.setError("Please enter your password");
            isValid = false;
        }
        return isValid;
    }

    private void init(View view) {
        this.navController = Navigation.findNavController(view);
    }
}
