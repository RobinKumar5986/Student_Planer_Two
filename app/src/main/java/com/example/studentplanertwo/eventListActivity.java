package com.example.studentplanertwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.example.studentplanertwo.databinding.ActivityEventListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class eventListActivity extends AppCompatActivity {
    ActivityEventListBinding binding;
    FirebaseAuth mAuth;
    DatabaseReference database;

    private List<String> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEventListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //-----Firebase Initialisation-------//
        mAuth=FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance().getReference("Calender Data");
        //----------------------------------//

        list=new ArrayList<>();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(eventListActivity.this, android.R.layout.simple_expandable_list_item_1,list);

        binding.lstAllEvents.setAdapter(adapter);

        String crUser= Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        database.child(crUser)
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot snap: snapshot.getChildren()){
                    list.add(Objects.requireNonNull(snap.getValue()).toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}