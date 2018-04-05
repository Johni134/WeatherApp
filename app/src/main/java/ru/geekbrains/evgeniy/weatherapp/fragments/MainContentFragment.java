package ru.geekbrains.evgeniy.weatherapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.evgeniy.weatherapp.CustomElementsAdapter;
import ru.geekbrains.evgeniy.weatherapp.R;
import ru.geekbrains.evgeniy.weatherapp.model.CityModel;

public class MainContentFragment extends Fragment implements View.OnClickListener {

    private FloatingActionButton addView;
    private RecyclerView recycleView;
    private CustomElementsAdapter adapter;
    private Button customButton;

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // show options menu in fragment
        setHasOptionsMenu(true);
        // init view
        View view = inflater.inflate(R.layout.main_content, container, false);
        initViews(view);
        addItemTouchCallback();
        return view;
    }

    private void initViews(View view) {
        addView = view.findViewById(R.id.fab_add);
        addView.setOnClickListener(this);
        recycleView = view.findViewById(R.id.rv_main);
        recycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CustomElementsAdapter(getData());
        recycleView.setAdapter(adapter);
        customButton = view.findViewById(R.id.btn_custom);
        customButton.setOnClickListener(this);
    }

    private void addItemTouchCallback() {
        ItemTouchHelper.SimpleCallback  simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.removeView(viewHolder.getAdapterPosition());
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recycleView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add: {
                adapter.addView(new CityModel("New"));
                break;
            }
            case R.id.btn_custom: {
                Snackbar.make(v, getString(R.string.test_snackbar), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null)
                        .show();
                break;
            }
        }
    }

    public List<CityModel> getData() {
        List<CityModel> result = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            result.add(new CityModel("Element " + i));
        }
        return result;
    }
}
