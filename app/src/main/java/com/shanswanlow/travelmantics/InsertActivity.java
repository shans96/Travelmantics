package com.shanswanlow.travelmantics;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsertActivity extends AppCompatActivity
{
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    EditText textTitle;
    EditText textDescription;
    EditText textPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        FirebaseUtil.openFirebaseReference("traveldeals");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        textTitle = findViewById(R.id.textTitle);
        textDescription = findViewById(R.id.textDescription);
        textPrice = findViewById(R.id.textPrice);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        if (menuItem.getItemId() == R.id.save_menu)
        {
            saveDeal();
            Toast.makeText(this, "Deal Saved.", Toast.LENGTH_LONG).show();
            clearInputs();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    private void saveDeal()
    {
        String title = textTitle.getText().toString();
        String description = textDescription.getText().toString();
        String price = textPrice.getText().toString();
        TravelDeal deal = new TravelDeal(title, description, price, "");
        mDatabaseReference.push().setValue(deal);
    }

    private void clearInputs()
    {
        textTitle.setText("");
        textDescription.setText("");
        textPrice.setText("");
        textTitle.requestFocus();
    }
}
