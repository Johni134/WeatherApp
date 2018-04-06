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

import java.util.List;

import ru.geekbrains.evgeniy.weatherapp.fragments.AboutFragment;
import ru.geekbrains.evgeniy.weatherapp.fragments.MainContentFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private MainContentFragment mainContentFragment = null;
    private AboutFragment aboutFragment = null;
    private final String EXTRA_MAIN_FRAGMENT = "main_fragment";
    private final String EXTRA_ABOUT_FRAGMENT = "about_fragment";
    private final String EXTRA_CURRENT_FRAGMENT = "current_fragment";
    private final String EXTRA_CURRENT_CHECKED_NAV_ITEM = "current_nav_item";
    private int navCheckedItem = R.id.nav_cities;
    private Fragment curFragment = null;
    FragmentManager fragmentManager = getSupportFragmentManager();

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

        // saved instance
        if(savedInstanceState != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            aboutFragment = (AboutFragment) getSupportFragmentManager().getFragment(savedInstanceState, EXTRA_ABOUT_FRAGMENT);
            if(aboutFragment != null) {
                fragmentTransaction.add(R.id.content, aboutFragment, aboutFragment.getTag());
                fragmentTransaction.hide(aboutFragment);
            }
            mainContentFragment = (MainContentFragment) getSupportFragmentManager().getFragment(savedInstanceState, EXTRA_MAIN_FRAGMENT);
            if(mainContentFragment != null) {
                fragmentTransaction.add(R.id.content, mainContentFragment, mainContentFragment.getTag());
                fragmentTransaction.hide(mainContentFragment);
            }
            setNewScreen(getSupportFragmentManager().getFragment(savedInstanceState, EXTRA_CURRENT_FRAGMENT));
            navCheckedItem = savedInstanceState.getInt(EXTRA_CURRENT_CHECKED_NAV_ITEM);
        }
        else {
            // main content by default (can use shared properties in future)
            mainContentFragment = new MainContentFragment();
            setNewScreen(mainContentFragment);
        }
        navigationView.setCheckedItem(navCheckedItem);
    }

    private void initViews() {
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        item.setChecked(true);
        navCheckedItem = item.getItemId();
        // main content is default fragment
        switch (navCheckedItem) {
            case R.id.nav_about: {
                if (aboutFragment == null)
                    aboutFragment = new AboutFragment();
                setNewScreen(aboutFragment);
                break;
            }
            case R.id.nav_cities: {
                if (mainContentFragment == null)
                    mainContentFragment = new MainContentFragment();
                setNewScreen(mainContentFragment);
                break;
            }
            case R.id.nav_favorites: {

            }
            case R.id.nav_feedback: {

            }
            default:
                if (mainContentFragment == null)
                    mainContentFragment = new MainContentFragment();
                setNewScreen(mainContentFragment);
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    // setting new screen by replacing content
    private void setNewScreen(Fragment fragment) {
        curFragment = fragment;
        List<Fragment> fragmentList = fragmentManager.getFragments();
        Fragment primaryFragment = null;
        for (Fragment f: fragmentList) {
            if(f.isVisible()) {
                primaryFragment = f;
                break;
            }
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(primaryFragment != null) {
            fragmentTransaction.hide(primaryFragment);
        }
        Fragment nextFragment = fragmentManager.findFragmentByTag(fragment.getTag());
        if(nextFragment != null)
            fragmentTransaction.show(nextFragment);
        else
            fragmentTransaction.add(R.id.content, fragment, fragment.toString());

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment f: fragmentList) {
            if(f.getClass() == MainContentFragment.class) {
                fragmentManager.putFragment(outState, EXTRA_MAIN_FRAGMENT, f);
            }
            if(f.getClass() == AboutFragment.class) {
                fragmentManager.putFragment(outState, EXTRA_ABOUT_FRAGMENT, f);
            }
        }
        fragmentManager.putFragment(outState, EXTRA_CURRENT_FRAGMENT, curFragment);

        outState.putInt(EXTRA_CURRENT_CHECKED_NAV_ITEM, navCheckedItem);
    }
}
