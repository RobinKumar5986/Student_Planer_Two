package com.example.studentplanertwo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.studentplanertwo.Models.uploadPDFs;
import com.example.studentplanertwo.databinding.ActivitySelectPdfBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class select_PDF_Activity extends AppCompatActivity {
    private static final int REQUEST_PDF = 1;
    ActivitySelectPdfBinding binding;
    FirebaseAuth mAuth;
    DatabaseReference database;
    StorageReference storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_select_pdf);
        binding=ActivitySelectPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //-----initialising the firebase references------//
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance().getReference("PDF Database");;
        storage=FirebaseStorage.getInstance().getReference();

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPdfFile();
                
            }
        });
    }

    private void getPdfFile() {
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select The PDF File"),REQUEST_PDF);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_PDF && resultCode == Activity.RESULT_OK &&
                    data != null && data.getData() != null) {
                uploadPDF(data.getData());
            }
        }

    private void uploadPDF(Uri data) {

        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();

        String crUser=mAuth.getCurrentUser().getUid();
        StorageReference storageReference=storage.child(crUser+System.currentTimeMillis()+".pdf");
        storageReference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uri=taskSnapshot.getStorage().getDownloadUrl();
                        while(!uri.isComplete());

                        Uri url=uri.getResult();
                        String name=binding.editTextTextPersonName.getText()+"";
                        if(name.isEmpty()){
                            name="defaultName";
                        }
                        uploadPDFs uploadPDFs=new uploadPDFs(name,url.toString());
                        database.child(crUser).child(database.push().getKey()).setValue(uploadPDFs);
                        Toast.makeText(select_PDF_Activity.this, "File Uploaded", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress=(100 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded :"+(int)progress+"%");
                    }
                });

    }
}