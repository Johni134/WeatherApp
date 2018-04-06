package ru.geekbrains.evgeniy.weatherapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.evgeniy.weatherapp.model.CityModel;

interface OnCustomAdapterClickListener{
    void removeView(int position);
    void editView(int position);
}

public class CustomElementsAdapter extends RecyclerView.Adapter<CustomElementsAdapter.CustomViewHolder> implements OnCustomAdapterClickListener{

    private List<CityModel> dataSet;

    public CustomElementsAdapter(List<CityModel> dataSet) {
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public CustomElementsAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city, parent, false);

        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomElementsAdapter.CustomViewHolder holder, int position) {
        holder.bind(dataSet.get(position), this);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public void removeView(int position) {
        dataSet.remove(position);
        notifyItemRemoved(position);
    }

    public List<CityModel> getList() {
        return dataSet;
    }

    @Override
    public void editView(int position) {
        CityModel cm = dataSet.get(position);
        cm.setName("Edited value");
        notifyItemChanged(position);
    }

    public void addView(CityModel cityModel) {
        dataSet.add(cityModel);
        notifyItemInserted(dataSet.size() - 1);
    }

    public void clear() {
        dataSet.clear();
        notifyDataSetChanged();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener,
                PopupMenu.OnMenuItemClickListener,
                View.OnLongClickListener {
        private TextView textViewTitle;
        private TextView textViewOption;
        private TextView textViewTemp;

        private OnCustomAdapterClickListener callbacks;

        CustomViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.tvTitle);
            textViewTemp = itemView.findViewById(R.id.tvTemp);
            textViewOption = itemView.findViewById(R.id.tvOptionDigit);
            textViewOption.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void bind(CityModel cityModel, OnCustomAdapterClickListener callbacks) {
            this.callbacks = callbacks;
            textViewTitle.setText(cityModel.getName());
            textViewTemp.setText(cityModel.getTempC());
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_edit:
                    if (callbacks != null) callbacks.editView(getAdapterPosition());
                    return true;
                case R.id.menu_delete:
                    if (callbacks != null) callbacks.removeView(getAdapterPosition());
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tvOptionDigit: {
                    showPopupMenu(v);
                    break;
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            showPopupMenu(v);
            return false;
        }

        private void showPopupMenu(View v) {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenuInflater().inflate(R.menu.context_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(this);
            popup.show();
        }
    }
}
