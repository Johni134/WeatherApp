package ru.geekbrains.evgeniy.weatherapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.geekbrains.evgeniy.weatherapp.model.CityModel;

interface OnCustomAdapterClickListener{
    void removeView(int position);
    void editView(int position);
}

class CustomElementsAdapter extends RecyclerView.Adapter<CustomElementsAdapter.CustomViewHolder> implements OnCustomAdapterClickListener{

    private List<CityModel> dataSet;

    @Override
    public CustomElementsAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city, parent, false);

        CustomViewHolder vh = new CustomViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomElementsAdapter.CustomViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void removeView(int position) {

    }

    @Override
    public void editView(int position) {

    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        TextView textViewTitle;
        TextView textViewOption;

        private OnCustomAdapterClickListener callbacks;

        public CustomViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.txtTitle);
            textViewOption = itemView.findViewById(R.id.txtOptionDigit);
            textViewOption.setOnClickListener(this);
        }

        void bind(String text, OnCustomAdapterClickListener callbacks) {
            this.callbacks = callbacks;
            textViewTitle.setText(text);
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
                case R.id.txtOptionDigit: {
                    PopupMenu popup = new PopupMenu(v.getContext(), v);
                    popup.getMenuInflater().inflate(R.menu.context_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(this);
                    popup.show();
                    break;
                }
            }
        }
    }
}
