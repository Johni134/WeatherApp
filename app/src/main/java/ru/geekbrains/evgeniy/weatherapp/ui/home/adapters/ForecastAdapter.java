package ru.geekbrains.evgeniy.weatherapp.ui.home.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.geekbrains.evgeniy.weatherapp.R;
import ru.geekbrains.evgeniy.weatherapp.data.SupportingLib;
import ru.geekbrains.evgeniy.weatherapp.model.ForecastModel;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    List<ForecastModel> forecastModelList;

    public ForecastAdapter(List<ForecastModel> forecastModelList) {
        this.forecastModelList = forecastModelList;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);

        return new ForecastViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        ForecastModel hm = forecastModelList.get(position);
        if(hm != null)
            holder.bind(hm);
    }

    @Override
    public int getItemCount() {
        return forecastModelList.size();
    }

    class ForecastViewHolder extends RecyclerView.ViewHolder  {

        private TextView textViewLastUpdate;
        private TextView textViewTemp;
        public ForecastViewHolder(View itemView) {
            super(itemView);

            initViews(itemView);
        }

        private void initViews(View itemView) {
            textViewLastUpdate = itemView.findViewById(R.id.tvLastUpdate);
            textViewTemp = itemView.findViewById(R.id.tvTemp);
        }


        public void bind(ForecastModel hm) {
            textViewTemp.setText(hm.main.getTempC());
            textViewLastUpdate.setText(SupportingLib.getLastUpdate(hm.dt));
        }
    }
}
