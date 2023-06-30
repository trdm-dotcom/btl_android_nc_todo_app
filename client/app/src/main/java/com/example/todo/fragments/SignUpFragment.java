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
import com.example.todo.model.request.RegisterRequest;
import com.example.todo.utils.HttpClientHelper;
import com.google.android.material.textfield.TextInputEditText;


import java.util.HashMap;

public class SignUpFragment extends Fragment {
    private NavController navController;
    private TextView textViewSignIn;
    private ImageView nextBtn;
    private TextInputEditText mailEdt, nameEdt, passEdt, verifyPassEdt;
    private static final String NAME_REGEX = "^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂẾưăạảấầẩẫậắằẳẵặẹẻẽềềểếỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ\\s]*$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[\\W,_])[.!-~]{6,}$";
    private HttpClientHelper httpClientHelper;
    private static final String TAG = SignUpFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.httpClientHelper = HttpClientHelper.getInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        this.textViewSignIn = rootView.findViewById(R.id.textViewSignIn);
        this.nextBtn = rootView.findViewById(R.id.nextBtn);
        this.mailEdt = rootView.findViewById(R.id.mailEdt);
        this.nameEdt = rootView.findViewById(R.id.nameEdt);
        this.passEdt = rootView.findViewById(R.id.passEdt);
        this.verifyPassEdt = rootView.findViewById(R.id.verifyPassEdt);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.init(view);
        this.textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_signUpFragment_to_signInFragment);
            }
        });
        this.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    doSignUp();
                }
            }
        });
    }

    private void doSignUp() {
        class SignUpBackend extends AsyncTask<Void, Void, String> {
            ProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = ProgressDialog.show(getActivity(), "", "");
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    RegisterRequest body = new RegisterRequest(passEdt.getText().toString().trim(), nameEdt.getText().toString().trim(), mailEdt.getText().toString().trim());
                    httpClientHelper.post(
                            httpClientHelper.buildUrl("/auth/register", null),
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
                    navController.navigate(R.id.action_signUpFragment_to_signInFragment);
                } else {
                    CustomToast.makeText(getActivity(), result, CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                }
            }
        }
        SignUpBackend signUpBackend = new SignUpBackend();
        signUpBackend.execute();
    }

    private boolean validateForm() {
        boolean isValid = true;
        String password = this.passEdt.getText().toString().trim();
        String mail = this.mailEdt.getText().toString().trim();
        String name = this.nameEdt.getText().toString().trim();
        String retryPass = this.verifyPassEdt.getText().toString().trim();
        if (name.isEmpty()) {
            this.nameEdt.setError("Please enter your name");
            isValid = false;
        } else if (!name.matches(NAME_REGEX)) {
            this.nameEdt.setError("Please enter a valid name");
            isValid = false;
        }
        if (mail.isEmpty()) {
            this.mailEdt.setError("Please enter your mail");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            this.mailEdt.setError("Please enter a valid email");
            isValid = false;
        }
        if (password.isEmpty()) {
            this.passEdt.setError("Please enter a password");
            isValid = false;
        } else if (!password.matches(PASSWORD_REGEX)) {
            this.passEdt.setError("Password should contain:\n" +
                    "- Least 6 characters long\n" +
                    "- Least one digit\n" +
                    "- Least one uppercase letter\n" +
                    "- Least one of these characters");
            isValid = false;
        }
        if (!password.equals(retryPass)) {
            this.verifyPassEdt.setError("Passwords do not match");
            isValid = false;
        }
        return isValid;
    }

    private void init(View view) {
        this.navController = Navigation.findNavController(view);
    }
}
