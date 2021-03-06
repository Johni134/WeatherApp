package ru.geekbrains.evgeniy.weatherapp.ui.home;


import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import io.realm.Realm;
import ru.geekbrains.evgeniy.weatherapp.R;
import ru.geekbrains.evgeniy.weatherapp.WeatherApplication;
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
import ru.geekbrains.evgeniy.weatherapp.ui.fragments.SettingsFragment;

import static ru.geekbrains.evgeniy.weatherapp.data.SupportingLib.EXTRA_CITY_ID;
import static ru.geekbrains.evgeniy.weatherapp.data.SupportingLib.EXTRA_CITY_TITLE;
import static ru.geekbrains.evgeniy.weatherapp.data.SupportingLib.EXTRA_LAT;
import static ru.geekbrains.evgeniy.weatherapp.data.SupportingLib.EXTRA_LON;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AddCityListener, CityWeatherListener, DeleteEditCityListener {

    // for nav drawable
    private int DRAWABLE_NAV_WIDTH = 450;
    private int DRAWABLE_NAV_HEIGHT = 450;

    // views
    private NavigationView navigationView;
    private DrawerLayout drawer;

    // fragments
    private MainContentFragment mainContentFragment = null;
    private AboutFragment aboutFragment = null;
    private CityWeatherFragment cityWeatherFragment = null;
    private SettingsFragment settingsFragment = null;
    private Fragment curFragment = null;
    FragmentManager fragmentManager = getSupportFragmentManager();

    // const
    private static final String EXTRA_CURRENT_CHECKED_NAV_ITEM = "current_nav_item";
    private static final String EXTRA_MAIN_ARRAYLIST = "main_arraylist";
    private static final String EXTRA_CITY_MODEL_KEY = "city_model_key";

    private static final String FILENAME = "avatar.png";

    private int navCheckedItem = R.id.nav_cities;

    // favorite city
    private CityModel favorite;

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

        // init drawer
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // init views
        initViews();

        /*
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
        */

        Drawable drawable = getResources().getDrawable(R.drawable.ic_location_city_black_24dp);
        View header = navigationView.getHeaderView(0);
        ImageView iv = header.findViewById(R.id.imageView);
        iv.setImageDrawable(drawable);
        iv.setMinimumWidth(DRAWABLE_NAV_WIDTH);
        iv.setMinimumHeight(DRAWABLE_NAV_HEIGHT);

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

        // set favorite
        setFavoriteCityFromRealm();


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
            case R.id.nav_favorite:
                if (favorite != null) {
                    showCityWeather(favorite);
                }
                else {
                    Toast.makeText(this, R.string.choose_your_favorite_city, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_settings:
                if(settingsFragment == null) {
                    settingsFragment = new SettingsFragment();
                }
                setNewScreen(settingsFragment);
                break;
            default:
                if (mainContentFragment == null)
                    mainContentFragment = new MainContentFragment();
                setNewScreen(mainContentFragment);
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    // setting new fragment
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
        else if(navCheckedItem != R.id.nav_cities) {
            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_cities));
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
        fragmentTransaction.commit();
    }

    @Override
    public void showForecast(CityModel cityModel) {
        if (cityModel != null) {
            Intent intent = new Intent(this, CityForecastActivity.class);
            intent.putExtra(EXTRA_CITY_TITLE, cityModel.getNameWithCountry());
            intent.putExtra(EXTRA_CITY_ID, cityModel.id);
            startActivity(intent);
        }
    }

    @Override
    public void showAirPollution(CityModel cityModel) {
        if (cityModel != null) {
            Intent intent = new Intent(this, AirPollutionActivity.class);
            intent.putExtra(EXTRA_CITY_TITLE, cityModel.getNameWithCountry());
            intent.putExtra(EXTRA_CITY_ID, cityModel.id);
            intent.putExtra(EXTRA_LAT, cityModel.coord.lat);
            intent.putExtra(EXTRA_LON, cityModel.coord.lon);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    private void setFavoriteNavMenu(CityModel favoriteModel) {
        this.favorite = favoriteModel;
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_favorite);
        if (menuItem != null)
            menuItem.setTitle(favoriteModel.getName());
    }

    @Override
    public void setFavoriteCityFromRealm() {
        CityModel cm = DataHelper.getFavoriteCitySync(realm);
        if (cm != null) {
            setFavoriteNavMenu(cm);
        }
    }
}
