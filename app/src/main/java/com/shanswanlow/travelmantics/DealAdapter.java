package com.shanswanlow.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder>
{
    ArrayList<TravelDeal> deals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    public DealAdapter()
    {
        FirebaseUtil.openFirebaseReference("traveldeals");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        deals = FirebaseUtil.mDeals;
        mChildEventListener = new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                TravelDeal deal = dataSnapshot.getValue(TravelDeal.class);
                deals.add(deal);
                deal.setId(dataSnapshot.getKey());
                notifyItemInserted(deals.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.row_rv, parent, false);
        return new DealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position)
    {
        TravelDeal deal = deals.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener
    {
        TextView title;
        TextView description;
        TextView price;
        public DealViewHolder(@NonNull View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.dealTitle);
            description = itemView.findViewById(R.id.dealDescription);
            price = itemView.findViewById(R.id.dealPrice);
            itemView.setOnClickListener(this);
        }

        public void bind (TravelDeal deal)
        {
            title.setText(deal.getTitle());
            description.setText(deal.getDescription());
            price.setText(deal.getPrice());
        }

        @Override
        public void onClick(View view)
        {
            int itemPosition = getAdapterPosition();
            TravelDeal selectedDeal = deals.get(itemPosition);
            Intent editIntent = new Intent(view.getContext(), DealActivity.class);


        }
    }
}
