package com.shanswanlow.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ListActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.list_activity_menu, menu);
        MenuItem insertMenu = menu.findItem(R.id.insert_deal_menu);
        if (FirebaseUtil.isAdmin == true)
        {
            insertMenu.setVisible(true);
        }
        else
        {
            insertMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.insert_deal_menu:
                Intent createDealIntent = new Intent(this, DealActivity.class);
                startActivity(createDealIntent);
                return true;
            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                Toast.makeText(ListActivity.this, "Successfully signed out.", Toast.LENGTH_SHORT).show();
                                FirebaseUtil.attachAuthListener();
                            }
                        });
                FirebaseUtil.detatchAuthListener();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showMenu()
    {
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        FirebaseUtil.detatchAuthListener();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        FirebaseUtil.openFirebaseReference("traveldeals", this);
        RecyclerView dealsRecyclerView = findViewById(R.id.recyclerDeals);
        final DealAdapter dealAdapter = new DealAdapter();
        dealsRecyclerView.setAdapter(dealAdapter);
        LinearLayoutManager dealsLayoutManager =
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        dealsRecyclerView.setLayoutManager(dealsLayoutManager);
        FirebaseUtil.attachAuthListener();
    }
}
