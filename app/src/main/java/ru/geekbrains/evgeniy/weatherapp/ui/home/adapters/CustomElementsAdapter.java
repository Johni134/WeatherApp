package ru.geekbrains.evgeniy.weatherapp.ui.home.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import ru.geekbrains.evgeniy.weatherapp.R;
import ru.geekbrains.evgeniy.weatherapp.data.DataHelper;
import ru.geekbrains.evgeniy.weatherapp.data.WorkWithSharedPreferences;
import ru.geekbrains.evgeniy.weatherapp.ui.fragments.CityWeatherListener;
import ru.geekbrains.evgeniy.weatherapp.ui.fragments.DeleteCityListener;
import ru.geekbrains.evgeniy.weatherapp.ui.fragments.MainContentFragment;
import ru.geekbrains.evgeniy.weatherapp.model.CityModel;

interface OnCustomAdapterClickListener{
    void removeView(int position);
    void editView(int position);
    void showDetailView(int position);
}

public class CustomElementsAdapter extends RealmRecyclerViewAdapter<CityModel, CustomElementsAdapter.CustomViewHolder> implements OnCustomAdapterClickListener{

    private OrderedRealmCollection<CityModel> dataSet;
    private ViewGroup viewGroup;

    public CustomElementsAdapter(OrderedRealmCollection<CityModel> dataSet) {
        super(dataSet, true);
        this.dataSet = dataSet;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public CustomElementsAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewGroup = parent;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city, parent, false);

        return new CustomViewHolder(v);
    }

    public String getIDs() {
        if(dataSet == null || dataSet.size() == 0) {
            return "";
        } else {
            List<String> ids = new ArrayList<>();
            for (CityModel cm: dataSet) {
                ids.add(String.valueOf(cm.id));
            }
            return StringUtils.join(ids, ",");
        }
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
        ((DeleteCityListener) viewGroup.getContext()).onDeleteCity(dataSet.get(position));
    }

    public List<CityModel> getList() {
        return dataSet;
    }

    @Override
    public void editView(int position) {
        CityModel cm = dataSet.get(position);
        cm.setName("Edited value");
        // вдруг тут будем менять город
        //WorkWithSharedPreferences.savePropertyWithEncrypt(MainContentFragment.SAVED_CITIES_ID, getIDs());
        notifyItemChanged(position);
    }

    @Override
    public void showDetailView(int position) {
        ((CityWeatherListener) viewGroup.getContext()).showCityWeather(dataSet.get(position));
    }

    public void addView(CityModel cityModel) {
        dataSet.add(cityModel);
        //WorkWithSharedPreferences.savePropertyWithEncrypt(MainContentFragment.SAVED_CITIES_ID, getIDs());
        notifyItemInserted(dataSet.size() - 1);
    }

    public boolean alreadyExist(CityModel cityModel) {
        for (CityModel cm: dataSet) {
            if(cm.id.equals(cityModel.id))
                return true;
        }
        return false;
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
            textViewTitle.setOnClickListener(this);
            textViewTemp.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void bind(CityModel cityModel, OnCustomAdapterClickListener callbacks) {
            this.callbacks = callbacks;
            textViewTitle.setText(cityModel.getName() + ", " + cityModel.sys.country);
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
                case R.id.tvOptionDigit:
                    showPopupMenu(v);
                    break;
                case R.id.tvTemp:
                case R.id.tvTitle:
                    if (callbacks != null) callbacks.showDetailView(getAdapterPosition());
                    break;
                default:
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
