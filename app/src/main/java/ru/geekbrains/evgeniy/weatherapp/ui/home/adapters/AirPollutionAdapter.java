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
import ru.geekbrains.evgeniy.weatherapp.model.PollutionModel;

public class AirPollutionAdapter extends RecyclerView.Adapter<AirPollutionAdapter.AirPollutionViewHolder> {

    List<PollutionModel> pollutionModelList = new ArrayList<>();

    public AirPollutionAdapter(List<PollutionModel> pollutionModelList) {
        this.pollutionModelList = pollutionModelList;
    }

    @NonNull
    @Override
    public AirPollutionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_air_pollution, parent, false);

        return new AirPollutionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AirPollutionViewHolder holder, int position) {
        PollutionModel pm = pollutionModelList.get(position);
        if (pm != null) {
            holder.bind(pm);
        }
    }

    @Override
    public int getItemCount() {
        return pollutionModelList.size();
    }

    public class AirPollutionViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewPrecision;
        private TextView textViewPressure;
        private TextView textViewValue;

        public AirPollutionViewHolder(View itemView) {
            super(itemView);

            initViews(itemView);
        }

        private void initViews(View itemView) {
            textViewPrecision = itemView.findViewById(R.id.tv_air_precision);
            textViewPressure = itemView.findViewById(R.id.tv_air_pressure);
            textViewValue = itemView.findViewById(R.id.tv_air_value);
        }

        public void bind(PollutionModel pollutionModel) {
            textViewValue.setText(pollutionModel.value.toString());
            textViewPressure.setText(pollutionModel.getPressureString());
            textViewPrecision.setText(pollutionModel.precision.toString());
        }
    }
}
