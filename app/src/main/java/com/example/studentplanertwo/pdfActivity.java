package com.example.studentplanertwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.studentplanertwo.Models.uploadPDFs;
import com.example.studentplanertwo.databinding.ActivityMainBinding;
import com.example.studentplanertwo.databinding.ActivityPdfBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class pdfActivity extends AppCompatActivity {
    ActivityPdfBinding binding;
    FirebaseAuth mAuth;
    DatabaseReference database;
    List<uploadPDFs> uploadPDFS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //----mAuth---------//
        mAuth=FirebaseAuth.getInstance();

        uploadPDFS=new ArrayList<>();

        viewAllFiles();

        binding.lstPdf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(pdfActivity.this, "pdf clicked", Toast.LENGTH_SHORT).show();
                uploadPDFs uploadPDFs=uploadPDFS.get(position);
                Intent intent=new Intent();
                String pdfUrl=Uri.parse(uploadPDFs.getUrl()).toString();
                Uri uri=Uri.parse(pdfUrl);
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // Handle PDF viewer app not found error
                    Toast.makeText(getApplicationContext(), "No PDF viewer app installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
//--------pdf----------//
    private void viewAllFiles() {
        String crUser=mAuth.getCurrentUser().getUid();
        database= FirebaseDatabase.getInstance().getReference("PDF Database").child(crUser);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot: snapshot.getChildren()){
                    uploadPDFs uploadPDFs=postSnapshot.getValue(com.example.studentplanertwo.Models.uploadPDFs.class);
                    uploadPDFS.add(uploadPDFs);
                }
                String[] uploads=new String[uploadPDFS.size()];

                for(int i=0;i<uploads.length;i++){
                    uploads[i]=uploadPDFS.get(i).getName();
                }
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1,uploads);
                binding.lstPdf.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
//-------menu bar----------//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.pdfmenue,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addPDF) {
                Intent intent=new Intent(pdfActivity.this,select_PDF_Activity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}