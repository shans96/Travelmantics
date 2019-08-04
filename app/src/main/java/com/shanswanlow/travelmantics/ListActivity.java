package com.shanswanlow.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        RecyclerView dealsRecyclerView = findViewById(R.id.recyclerDeals);
        final DealAdapter dealAdapter = new DealAdapter();
        dealsRecyclerView.setAdapter(dealAdapter);
        LinearLayoutManager dealsLayoutManager =
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        dealsRecyclerView.setLayoutManager(dealsLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.list_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.insert_deal_menu)
        {
            Intent createDealIntent = new Intent(this, DealActivity.class);
            startActivity(createDealIntent);
        }
        return true;
    }
}
