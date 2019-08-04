package com.shanswanlow.travelmantics;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil
{
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;
    public static ArrayList<TravelDeal> mDeals;
    private static ListActivity caller;
    public static final int RC_SIGN_IN = 1;
    public static boolean isAdmin;

    private FirebaseUtil(){};

    public static void openFirebaseReference(String pathReference, final ListActivity callerActivity)
    {
        if (firebaseUtil == null)
        {
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;
            mAuthStateListener = new FirebaseAuth.AuthStateListener()
            {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
                {
                    if (firebaseAuth.getCurrentUser() == null)
                    {
                        signIn();
                        Toast.makeText(callerActivity.getBaseContext(), "Welcome back!", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        String userId = firebaseAuth.getUid();
                        checkIfAdmin(userId);
                    }
                }
            };
        }
        mDeals = new ArrayList<>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(pathReference);
    }

    private static void checkIfAdmin(String userId)
    {
        FirebaseUtil.isAdmin = false;
        DatabaseReference reference = mFirebaseDatabase.getReference().child("administrators").child(userId);
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                FirebaseUtil.isAdmin = true;
                caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addChildEventListener(listener);
    }

    public static void signIn()
    {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void attachAuthListener()
    {
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public static void detatchAuthListener()
    {
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }
}
