package com.example.todo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.example.todo.utils.HttpClientHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TaskpOtionsFragment extends BottomSheetDialogFragment {
    private HttpClientHelper httpClientHelper;
    private AppCompatButton btnEdit, btnCompleted, btnDelete;
    private setRefreshListener setRefreshListener;
    private int taskId;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public interface setRefreshListener {
        void refresh();
    }
}
