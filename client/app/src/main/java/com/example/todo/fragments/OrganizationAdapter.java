package com.example.todo.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.model.dto.OrganizationDto;

import java.util.List;

public class OrganizationAdapter extends RecyclerView.Adapter<OrganizationAdapter.OrganizationHolder> {
    private List<OrganizationDto> organizationList;
    private Context context;
    private LayoutInflater inflater;
    private OnItemClickListener listener;
    private OrganizationFormFragment.setRefreshListener setRefreshListener;

    public OrganizationAdapter(Context context, List<OrganizationDto> organizationList, OrganizationFormFragment.setRefreshListener setRefreshListener) {
        this.context = context;
        this.organizationList = organizationList;
        this.setRefreshListener = setRefreshListener;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public OrganizationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_organization, parent, false);
        return new OrganizationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizationHolder holder, int position) {
        OrganizationDto organization = organizationList.get(position);
        holder.name.setText(organization.getName());
        holder.left.setText(String.format("%d Left", organization.getLeft()));
        holder.done.setText(String.format("%d Done", organization.getDone()));
    }

    @Override
    public int getItemCount() {
        if (this.organizationList == null) {
            return 0;
        }
        return this.organizationList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(OrganizationDto item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class OrganizationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, done, left;

        public OrganizationHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameOrganization);
            done = itemView.findViewById(R.id.done);
            left = itemView.findViewById(R.id.left);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position > RecyclerView.NO_POSITION) {
                    OrganizationDto item = organizationList.get(position);
                    listener.onItemClick(item);
                }
            }
        }
    }
}
