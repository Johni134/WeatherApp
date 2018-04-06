package ru.geekbrains.evgeniy.weatherapp;

import android.os.Bundle;
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

import ru.geekbrains.evgeniy.weatherapp.fragments.AboutFragment;
import ru.geekbrains.evgeniy.weatherapp.fragments.MainContentFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private MainContentFragment mainContentFragment = null;
    private AboutFragment aboutFragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // init drawer
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // init views
        initViews();

        // main content by default (can use shared properties in future)
        mainContentFragment = new MainContentFragment();
        setNewScreen(mainContentFragment);
        navigationView.setCheckedItem(R.id.nav_cities);
    }

    private void initViews() {
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        item.setChecked(true);

        // main content is default fragment
        switch (item.getItemId()) {
            case R.id.nav_about: {
                if (aboutFragment == null) {
                    aboutFragment = new AboutFragment();
                }
                setNewScreen(aboutFragment);
                break;
            }
            case R.id.nav_cities: {
                if(mainContentFragment == null) {
                    mainContentFragment = new MainContentFragment();
                }
                setNewScreen(mainContentFragment);
                break;
            }
            case R.id.nav_favorites: {

            }
            case R.id.nav_feedback: {

            }
            default:
                if(mainContentFragment == null) {
                    mainContentFragment = new MainContentFragment();
                }
                setNewScreen(mainContentFragment);
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    // setting new screen by replacing content
    private void setNewScreen(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
