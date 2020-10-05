package com.example.sy925.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class BoardActivity extends AppCompatActivity {

    final String TAG = this.getClass().getSimpleName();

    private RecyclerView recyclerView;
    private List<ImageDTO> imageDTOs = new ArrayList<>();
    private List<String> uidLists = new ArrayList<>();

    FirebaseDatabase database;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager((new LinearLayoutManager(this)));
        final BoardRecyclerViewAdapter adapter = new BoardRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();



                database.getReference().child("images").addValueEventListener(new ValueEventListener() {  //
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageDTOs.clear();

                int index = 0; // for test

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    ImageDTO imageDTO = snapshot.getValue(ImageDTO.class);
                    imageDTOs.add(imageDTO);
                    Log.d(TAG, "key=" + snapshot.getKey());
                    Log.d(TAG, "onDataChange +" + index++ + "=>>" + imageDTO.toString());
                    uidLists.add(0, snapshot.getKey());

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button btnNew = findViewById(R.id.btnNew);
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), InsertActivity.class));
                finish();
            }
        });
    }

    class BoardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_board, viewGroup, false);

            return new CustomViewHolder(view, i);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ImageDTO imageDto = imageDTOs.get(i);

            ((CustomViewHolder) viewHolder).txtvItem.setText(imageDto.title);
            ((CustomViewHolder) viewHolder).txtvItem2.setText(imageDto.description);
            Glide.with(viewHolder.itemView.getContext()).load(imageDto.imageUrl).into(((CustomViewHolder)viewHolder).imgvItem);
            Log.d(TAG, "onBindViewHolder +" + i + "=>>" + imageDto.toString());
        }

        @Override
        public int getItemCount() {
            return imageDTOs.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView imgvItem;
            TextView txtvItem;
            TextView txtvItem2;

            public CustomViewHolder(View view, final int i) {
                super(view);
                imgvItem = view.findViewById(R.id.imgvItem);
                txtvItem = view.findViewById(R.id.txtvItem);
                txtvItem2 = view.findViewById(R.id.txtvItem2);

                Button btnRemove = view.findViewById(R.id.btnRemove);
                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StorageReference ref = storage.getReference().child("images").child(imageDTOs.get(i).imageName);
                        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("images").child(imageDTOs.get(i).id).removeValue();
                                notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "삭제 실패", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

                Button btnModify = view.findViewById(R.id.btnModify);
                btnModify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.d(TAG, "_____________________________________---");
                        Intent intent = new Intent(getApplicationContext(), InsertActivity.class);
                        intent.putExtra("MODE", InsertActivity.MODE_MODIFY);
                        intent.putExtra("ITEM", imageDTOs.get(i));
                        startActivity(intent);
                        finish();

                    }
                });
            }
        }
    }
}

