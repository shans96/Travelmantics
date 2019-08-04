package com.shanswanlow.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity
{
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private static final int REQ_SELECT_PICTURE = 5;

    EditText textTitle;
    EditText textDescription;
    EditText textPrice;

    TravelDeal deal;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        textTitle = findViewById(R.id.textTitle);
        textDescription = findViewById(R.id.textDescription);
        textPrice = findViewById(R.id.textPrice);
        imageView = findViewById(R.id.destinationPreview);
        final Intent intent = getIntent();
        TravelDeal travelDeal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (travelDeal == null)
        {
            travelDeal = new TravelDeal();
        }
        this.deal = travelDeal;
        textTitle.setText(deal.getTitle());
        textDescription.setText(deal.getDescription());
        textPrice.setText(deal.getPrice());
        showImage(deal.getImageUrl());
        Button uploadButton = findViewById(R.id.uploadImage);
        uploadButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imageIntent.setType("image/jpeg");
                imageIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(imageIntent.createChooser(imageIntent, "Select Picture"), REQ_SELECT_PICTURE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal Saved.", Toast.LENGTH_LONG).show();
                clearInputs();
                return true;
            case R.id.delete_menu:
                deleteDeal();
                Toast.makeText(this, "Deal deleted.", Toast.LENGTH_LONG).show();
                returnToList();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);
        if (FirebaseUtil.isAdmin == true)
        {
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditTexts(true);
            Log.d("admin", "is admin");
        }
        else
        {
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditTexts(false);
        }
        return true;
    }

    private void saveDeal()
    {
        deal.setTitle(textTitle.getText().toString());
        deal.setDescription(textDescription.getText().toString());
        deal.setPrice(textPrice.getText().toString());

        if (deal.getId() == null)
        {
            mDatabaseReference.push().setValue(deal);
        }
        else
        {
            mDatabaseReference.child(deal.getId()).setValue(deal);
        }
    }

    private void deleteDeal()
    {
        if (deal == null)
        {
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_LONG).show();
            return;
        }
        mDatabaseReference.child(deal.getId()).removeValue();
        if (deal.getImageName() != null && !deal.getImageName().isEmpty())
        {
            StorageReference pictureReference = FirebaseUtil.mFirebaseStorage.getReference().child(deal.getImageName());
            pictureReference.delete().addOnSuccessListener(new OnSuccessListener<Void>()
            {
                @Override
                public void onSuccess(Void aVoid)
                {
                    Log.d("Image deletion", "Success");
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Log.d("Image deletion", "Exception occurred- " + e.toString());
                }
            });
        }
    }

    private void returnToList()
    {
        Intent listIntent = new Intent(this, ListActivity.class);
        startActivity(listIntent);
    }

    private void clearInputs()
    {
        textTitle.setText("");
        textDescription.setText("");
        textPrice.setText("");
        textTitle.requestFocus();
    }

    private void enableEditTexts(boolean isEnabled)
    {
        textTitle.setEnabled(isEnabled);
        textDescription.setEnabled(isEnabled);
        textPrice.setEnabled(isEnabled);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SELECT_PICTURE && resultCode == RESULT_OK)
        {
            final Uri imageUri = data.getData();
            StorageReference reference = FirebaseUtil.mStorageReference.child(imageUri.getLastPathSegment());
            reference.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot)
                {
                    StorageReference taskReference = taskSnapshot.getMetadata().getReference();
                    Task<Uri> uriTask = taskReference.getDownloadUrl();
                    uriTask.addOnCompleteListener(new OnCompleteListener<Uri>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task)
                        {
                            String imageUrl = task.getResult().toString();
                            String pictureName = taskSnapshot.getStorage().getPath();
                            deal.setImageUrl(imageUrl);
                            deal.setImageName(pictureName);
                            showImage(imageUrl);
                        }
                    });
                }
            });
        }
    }

    private void showImage(String url)
    {
        if (url != null && url.isEmpty() == false)
        {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width, width * 2/3)
                    .centerCrop()
                    .into(imageView);
        }
    }
}
