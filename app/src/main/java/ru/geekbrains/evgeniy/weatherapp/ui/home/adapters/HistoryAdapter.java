package ru.geekbrains.evgeniy.weatherapp.ui.home.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.evgeniy.weatherapp.R;
import ru.geekbrains.evgeniy.weatherapp.data.SupportingLib;
import ru.geekbrains.evgeniy.weatherapp.model.HistoryModel;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    List<HistoryModel> historyModelList;

    public HistoryAdapter(List<HistoryModel> historyModelList) {
        this.historyModelList = historyModelList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);

        return new HistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryModel hm = historyModelList.get(position);
        if(hm != null)
            holder.bind(hm);
    }

    @Override
    public int getItemCount() {
        return historyModelList.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder  {

        private TextView textViewLastUpdate;
        private TextView textViewTemp;
        public HistoryViewHolder(View itemView) {
            super(itemView);

            initViews(itemView);
        }

        private void initViews(View itemView) {
            textViewLastUpdate = itemView.findViewById(R.id.tvLastUpdate);
            textViewTemp = itemView.findViewById(R.id.tvTemp);
        }


        public void bind(HistoryModel hm) {
            textViewTemp.setText(hm.main.getTempC());
            textViewLastUpdate.setText(SupportingLib.getLastUpdate(hm.dt));
        }
    }
}
