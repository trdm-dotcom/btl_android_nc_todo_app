package com.example.todo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todo.R;
import com.example.todo.model.dto.CommentDto;

import java.text.SimpleDateFormat;
import java.util.List;

public class CommentAdapter extends ArrayAdapter {
    private Context context;
    private List<CommentDto> list;
    private int resource;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private LayoutInflater inflater;

    public CommentAdapter(@NonNull Context context, int resource, List<CommentDto> list) {
        super(context, resource, list);
        this.context = context;
        this.list = list;
        this.resource = resource;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(this.resource, parent, false);
        TextView tvNameUser = view.findViewById(R.id.tvNameUser);
        TextView tvCreatedAt = view.findViewById(R.id.tvCreatedAt);
        TextView tvContent = view.findViewById(R.id.tvComment);
        CommentDto commentDto = list.get(position);
        tvNameUser.setText(commentDto.getName());
        tvCreatedAt.setText(dateFormat.format(commentDto.getCreatedAt()));
        tvContent.setText(commentDto.getContent());
        return view;
    }
}
