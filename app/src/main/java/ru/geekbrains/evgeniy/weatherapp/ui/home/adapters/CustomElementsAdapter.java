package ru.geekbrains.evgeniy.weatherapp.ui.home.adapters;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import ru.geekbrains.evgeniy.weatherapp.R;
import ru.geekbrains.evgeniy.weatherapp.data.DataHelper;
import ru.geekbrains.evgeniy.weatherapp.data.SupportingLib;
import ru.geekbrains.evgeniy.weatherapp.ui.fragments.CityWeatherListener;
import ru.geekbrains.evgeniy.weatherapp.ui.fragments.DeleteEditCityListener;
import ru.geekbrains.evgeniy.weatherapp.model.CityModel;

interface OnCustomAdapterClickListener{
    void removeView(int position);
    void editView(int position);
    void showDetailView(int position);
    void showForecast(int position);
    void setFavorite(int position);
}

public class CustomElementsAdapter extends RealmRecyclerViewAdapter<CityModel, CustomElementsAdapter.CustomViewHolder> implements OnCustomAdapterClickListener{

    private boolean wasInsertOrUpdate = false;
    private Realm realm;
    private CityWeatherListener fragment;
    private RealmResults<CityModel> dataSet;
    private String updateString = "";

    public void setUpdateString(String updateString) {
        this.updateString = updateString;
    }

    public CustomElementsAdapter(RealmResults<CityModel> dataSet, CityWeatherListener fragment) {
        super(dataSet, true);
        this.dataSet = dataSet;
        this.dataSet.addChangeListener(realmChangeListener);
        this.fragment = fragment;
        setHasStableIds(true);
    }

    public CustomElementsAdapter(RealmResults<CityModel> dataSet, Realm realm) {
        super(dataSet, true);
        this.dataSet = dataSet;
        this.dataSet.addChangeListener(realmChangeListener);
        this.realm = realm;
        setHasStableIds(true);
    }

    @Nullable
    @Override
    public CityModel getItem(int index) {
        return super.getItem(index);
    }

    private RealmChangeListener realmChangeListener = new RealmChangeListener<RealmResults<CityModel>>() {
        @Override
        public void onChange(RealmResults<CityModel> cityModels) {
            if(!wasInsertOrUpdate) {
                notifyDataSetChanged();
                if (fragment != null && fragment instanceof CityWeatherListener) {
                    fragment.setFavoriteCityFromRealm();
                }
            }
            else {
                wasInsertOrUpdate = false;
            }
        }
    };

    public void setWasInsertOrUpdate(boolean wasInsertOrUpdate) {
        this.wasInsertOrUpdate = wasInsertOrUpdate;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city, parent, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            v.setTranslationZ(4);
        }

        return new CustomViewHolder(v);
    }

    public String getIDs() {
        if(getItemCount() == 0) {
            return "";
        } else {
            List<String> ids = new ArrayList<>();
            for (CityModel cm: getData()) {
                ids.add(String.valueOf(cm.id));
            }
            return StringUtils.join(ids, ",");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CustomElementsAdapter.CustomViewHolder holder, int position) {
        final CityModel cm = getItem(position);
        if(cm != null)
            holder.bind(cm, this);
    }

    @Override
    public int getItemCount() {
        return getData().size();
    }

    @Override
    public void removeView(int position) {
        CityModel cm = getItem(position);
        if (fragment != null && fragment instanceof DeleteEditCityListener) {
            wasInsertOrUpdate = true;
            ((DeleteEditCityListener) fragment).onDeleteCity(cm);
        }
        else if(realm != null) {
            wasInsertOrUpdate = true;
            DataHelper.deleteObjectById(realm, cm.id);
        }
    }

    @Override
    public void editView(int position) {
        CityModel cm = getItem(position);
        if (fragment != null && fragment instanceof DeleteEditCityListener) {
            ((DeleteEditCityListener) fragment).onEditCity(cm.id, "Edited");
        }
        else if (realm != null) {
            DataHelper.editNameById(realm, cm.id, "Edited");
        }
    }

    @Override
    public void showDetailView(int position) {
        if (fragment != null)
            fragment.showCityWeather(getItem(position));
    }

    @Override
    public void showForecast(int position) {
        if (fragment != null)
            fragment.showForecast(getItem(position));
    }

    @Override
    public void setFavorite(int position) {
        CityModel cm = getItem(position);
        if (fragment != null && fragment instanceof DeleteEditCityListener) {
            ((DeleteEditCityListener) fragment).setFavorite(cm);
        }
        else if (realm != null) {
            DataHelper.setFavorite(realm, cm);
        }
    }

    public void addView(CityModel cityModel) {
    }

    public boolean alreadyExist(CityModel cityModel) {
        for (CityModel cm: getData()) {
            if(cm.id.equals(cityModel.id))
                return true;
        }
        return false;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener,
                PopupMenu.OnMenuItemClickListener,
                View.OnLongClickListener {
        private TextView textViewTitle;
        private TextView textViewOption;
        private TextView textViewTemp;
        private TextView textViewLastUpdate;
        private ImageView imageViewStar;

        private OnCustomAdapterClickListener callbacks;

        CustomViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.tvTitle);
            textViewTemp = itemView.findViewById(R.id.tvTemp);
            textViewOption = itemView.findViewById(R.id.tvOptionDigit);
            textViewLastUpdate = itemView.findViewById(R.id.tvLastUpdate);
            imageViewStar = itemView.findViewById(R.id.ivFavorite);
            imageViewStar.setOnClickListener(this);
            textViewOption.setOnClickListener(this);
            textViewTitle.setOnClickListener(this);
            textViewTemp.setOnClickListener(this);
            textViewLastUpdate.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void bind(CityModel cityModel, OnCustomAdapterClickListener callbacks) {
            this.callbacks = callbacks;
            textViewTitle.setText(cityModel.getNameWithCountry());
            textViewTemp.setText(cityModel.getTempC());
            textViewLastUpdate.setText((updateString == "" ? "" : updateString + " ") + SupportingLib.getLastUpdate(cityModel.dt));
            if (cityModel.isFavorite())
                imageViewStar.setImageResource(R.mipmap.ic_yellow_star);
            else
                imageViewStar.setImageResource(R.mipmap.ic_yellow_star_empty);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_show_forecast:
                    if(callbacks != null) callbacks.showForecast(getAdapterPosition());
                    return true;
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
                case R.id.ivFavorite:
                    if (callbacks != null) callbacks.setFavorite(getAdapterPosition());
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
