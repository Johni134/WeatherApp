package ru.geekbrains.evgeniy.weatherapp.ui.home;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import io.realm.Realm;
import ru.geekbrains.evgeniy.weatherapp.R;
import ru.geekbrains.evgeniy.weatherapp.data.DataHelper;
import ru.geekbrains.evgeniy.weatherapp.data.WorkWithFiles;
import ru.geekbrains.evgeniy.weatherapp.data.WorkWithSharedPreferences;
import ru.geekbrains.evgeniy.weatherapp.services.UpdateService;
import ru.geekbrains.evgeniy.weatherapp.ui.fragments.AboutFragment;
import ru.geekbrains.evgeniy.weatherapp.ui.fragments.CityWeatherFragment;
import ru.geekbrains.evgeniy.weatherapp.ui.fragments.CityWeatherListener;
import ru.geekbrains.evgeniy.weatherapp.ui.fragments.DeleteEditCityListener;
import ru.geekbrains.evgeniy.weatherapp.ui.fragments.MainContentFragment;
import ru.geekbrains.evgeniy.weatherapp.model.CityModel;
import ru.geekbrains.evgeniy.weatherapp.ui.fragments.AddCityListener;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AddCityListener, CityWeatherListener, DeleteEditCityListener {

    // service
    private boolean shouldUnbind = false;
    private ServiceConnection serviceConnection;
    private UpdateService updateService;

    // views
    private NavigationView navigationView;
    private DrawerLayout drawer;

    // fragments
    private MainContentFragment mainContentFragment = null;
    private AboutFragment aboutFragment = null;
    private CityWeatherFragment cityWeatherFragment = null;
    private Fragment curFragment = null;
    FragmentManager fragmentManager = getSupportFragmentManager();

    // const
    private final String EXTRA_CURRENT_CHECKED_NAV_ITEM = "current_nav_item";
    private final String EXTRA_MAIN_ARRAYLIST = "main_arraylist";
    private final String EXTRA_CITY_MODEL_KEY = "city_model_key";

    private final String FILENAME = "avatar.png";

    private int navCheckedItem = R.id.nav_cities;

    // realm
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get realm instance
        realm = Realm.getDefaultInstance();

        // update existing fields
        updateExistingFields();

        // init service
        initService();

        // init drawer
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // init views
        initViews();

        // work with files
        if (!WorkWithFiles.fileExist(this, FILENAME)) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.fallout);
            WorkWithFiles.saveBitmapToFile(this, FILENAME, bm);
        }
        Bitmap bmFromFile = WorkWithFiles.loadBitmapFromFile(this, FILENAME);
        if (bmFromFile != null) {
            View header = navigationView.getHeaderView(0);
            ImageView iv = header.findViewById(R.id.imageView);
            iv.setImageBitmap(bmFromFile);


        }

        // init shared pref key
        WorkWithSharedPreferences.initSharedPreferences(this);

        // saved instance
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(EXTRA_CITY_MODEL_KEY)) {
                cityWeatherFragment = new CityWeatherFragment();
                cityWeatherFragment.setCityModel((CityModel) savedInstanceState.getParcelable(EXTRA_CITY_MODEL_KEY));
            }
            navCheckedItem = savedInstanceState.getInt(EXTRA_CURRENT_CHECKED_NAV_ITEM);
        }
        onNavigationItemSelected(navigationView.getMenu().findItem(navCheckedItem));
        if (cityWeatherFragment != null) {
            showCityWeather(cityWeatherFragment.getCityModel());
        }
    }

    private void initService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                updateService = ((UpdateService.UpdateWeathersBinder) service).getService();
                shouldUnbind = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                shouldUnbind = false;
            }
        };
        onBindService();
    }

    public void onBindService() {
        Intent intent = new Intent(getBaseContext(), UpdateService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }
    public void onUnbindService() {
        if(shouldUnbind) {
            unbindService(serviceConnection);
        }
        shouldUnbind = false;
    }

    public void updateWeathers() {
        if(!shouldUnbind) {
            Toast.makeText(this, getString(R.string.place_not_found), Toast.LENGTH_SHORT).show();
            onBindService();
            return;
        }

        updateService.updateWeathers();
    }

    private void initViews() {
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void updateExistingFields() {
        DataHelper.updateSortIDs(realm);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        item.setChecked(true);
        navCheckedItem = item.getItemId();
        // main content is default fragment
        switch (navCheckedItem) {
            case R.id.nav_about:
                if (aboutFragment == null)
                    aboutFragment = new AboutFragment();
                setNewScreen(aboutFragment);
                break;
            case R.id.nav_cities:
                if (mainContentFragment == null) {
                    mainContentFragment = new MainContentFragment();
                    mainContentFragment.setRealm(realm);
                }
                setNewScreen(mainContentFragment);
                break;
            case R.id.nav_favorites:
            case R.id.nav_feedback:
            default:
                if (mainContentFragment == null)
                    mainContentFragment = new MainContentFragment();
                setNewScreen(mainContentFragment);
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    // setting new screen by hiding old fragment and adding/showing new one
    //
    private void setNewScreen(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (cityWeatherFragment != null && cityWeatherFragment.isVisible()) {
            outState.putParcelable(EXTRA_CITY_MODEL_KEY, cityWeatherFragment.getCityModel());
        }
        outState.putInt(EXTRA_CURRENT_CHECKED_NAV_ITEM, navCheckedItem);
    }

    @Override
    public void onAddCity(String city) {
        if (mainContentFragment != null) {
            mainContentFragment.onAddCity(city);
        }
    }

    @Override
    public void showCityWeather(CityModel cityModel) {
        if (cityWeatherFragment == null)
            cityWeatherFragment = new CityWeatherFragment();
        cityWeatherFragment.setCityModel(cityModel);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, cityWeatherFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void showHistory(CityModel cityModel) {
        if (cityModel != null)
        {
            Intent intent = new Intent(this, CityHistoryActivity.class);
            intent.putExtra(CityHistoryActivity.EXTRA_CITY_TITLE, cityModel.getNameWithCountry());
            intent.putExtra(CityHistoryActivity.EXTRA_CITY_ID, cityModel.id);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onUnbindService();
        realm.close();
    }

    @Override
    public void onDeleteCity(CityModel cityModel) {
        if (mainContentFragment != null) {
            mainContentFragment.onDeleteCity(cityModel);
        }
    }

    @Override
    public void onEditCity(Long id, String name) {
        if (mainContentFragment != null) {
            mainContentFragment.onEditCity(id, name);
        }
    }

    @Override
    public void setFavorite(CityModel cityModel) {
        if (mainContentFragment != null) {
            mainContentFragment.setFavorite(cityModel);
        }
    }

}
