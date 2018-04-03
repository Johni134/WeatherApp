package ru.geekbrains.evgeniy.weatherapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.evgeniy.weatherapp.model.CityModel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton addView;
    private RecyclerView recycleView;
    private CustomElementsAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.simple_menu, menu);
        return super.onCreateOptionsMenu(menu);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        addItemTouchCallback();
    }

    private void initViews() {
        addView = findViewById(R.id.fab_add);
        addView.setOnClickListener(this);
        recycleView = findViewById(R.id.rv_main);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomElementsAdapter(getData());
        recycleView.setAdapter(adapter);
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
