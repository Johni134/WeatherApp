package ru.geekbrains.evgeniy.weatherapp.ui.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.geekbrains.evgeniy.weatherapp.data.DataHelper;
import ru.geekbrains.evgeniy.weatherapp.ui.home.adapters.CustomElementsAdapter;
import ru.geekbrains.evgeniy.weatherapp.R;
import ru.geekbrains.evgeniy.weatherapp.data.WeatherDataLoader;
import ru.geekbrains.evgeniy.weatherapp.model.CityModel;
import ru.geekbrains.evgeniy.weatherapp.model.CityModelArray;
import ru.geekbrains.evgeniy.weatherapp.ui.dialogs.AddCityDialog;
import ru.geekbrains.evgeniy.weatherapp.ui.dialogs.AddCityDialogListener;


public class MainContentFragment extends Fragment implements View.OnClickListener, AddCityDialogListener {

    private FloatingActionButton addView;
    private RecyclerView recycleView;
    private CustomElementsAdapter adapter = null;

    private final String EXTRA_LIST = "list_classes";

    public static final String SAVED_CITIES_ID = "SAVED_CITIES_ID";

    private final Handler handler = new Handler();

    private List<CityModel> list = null;

    private Realm realm;

    // options menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.simple_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                adapter.clear();
                return true;
            case R.id.menu_refresh:
                updateWeathers(adapter.getIDs());
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    // on create view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // show options menu in fragment
        setHasOptionsMenu(true);

        // init view
        View view = inflater.inflate(R.layout.main_content, container, false);

        initViews(view);
        addItemTouchCallback();

        // realm ini
        RealmResults<CityModel> realmResults = realm.where(CityModel.class).findAll();
        if(realmResults.size() == 0) {
            updateWeathers(getString(R.string.default_cities));
        }
        else {
            adapter = new CustomElementsAdapter(realmResults);
            recycleView.setAdapter(adapter);
        }

        return view;
    }

    private void updateWeathers(final String ids) {
        new Thread() {

            public void run() {
                final CityModelArray cityModelArray = WeatherDataLoader.getListWeatherByIDs(getContext(), ids);
                if (cityModelArray == null) {
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getContext(), getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            for (CityModel cm: cityModelArray.list) {
                                DataHelper.createOrUpdateFromObject(realm, cm);
                            }
                            RealmResults<CityModel> realmResults = realm.where(CityModel.class).findAll();
                            adapter = new CustomElementsAdapter(realmResults);
                            recycleView.setAdapter(adapter);
                        }
                    });
                }
            }
        }.start();
    }

    private void initViews(View view) {
        addView = view.findViewById(R.id.fab_add);
        addView.setOnClickListener(this);
        recycleView = view.findViewById(R.id.rv_main);
        recycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void addItemTouchCallback() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final CityModel cm = adapter.getItem(viewHolder.getAdapterPosition());
                DataHelper.deleteObject(realm, cm);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recycleView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add: {
                showInputDialog();
                break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public List<CityModel> getList() {
        return adapter.getList();
    }

    public void setList(List<CityModel> list) {
        this.list = list;
    }

    private void showInputDialog() {
        new AddCityDialog().show(this.getFragmentManager(), "branch_filter_mode_dialog");
    }

    @Override
    public void onAddCity(String city) {
        addCity(city);
    }

    private void addCity(final String city) {
        new Thread() {//Отдельный поток для получения новых данных в фоне

            public void run() {
                final CityModel model = WeatherDataLoader.getWeatherByCity(getActivity(), city);
                // Вызов методов напрямую может вызвать runtime error
                // Мы не можем напрямую обновить UI, поэтому используем handler, чтобы обновить интерфейс в главном потоке.
                if (model == null) {
                    handler.post(new Runnable() {

                        public void run() {
                            Toast.makeText(getActivity(), getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    handler.post(new Runnable() {

                        public void run() {
                            if (adapter.alreadyExist(model)) {
                                Toast.makeText(getActivity(), getString(R.string.city_already_exist)
                                                              + " ("
                                                              + model.getName()
                                                              + ")",
                                        Toast.LENGTH_LONG).show();
                            }
                            else {
                                DataHelper.createOrUpdateFromObject(realm, model);
                            }
                        }
                    });
                }
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        recycleView.setAdapter(null);
        super.onDestroyView();
    }
}
