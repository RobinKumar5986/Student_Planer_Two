package com.example.studentplanertwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.studentplanertwo.databinding.ActivityCalanderBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class calanderActivity extends AppCompatActivity {
    ActivityCalanderBinding binding;
    FirebaseAuth mAuth;
    DatabaseReference database;
    static String  strSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCalanderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //-----firebase Initiation------//
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance().getReference("Calender Data");

        binding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                strSelectedDate=year+"-"+(month+1)+"-"+dayOfMonth;
//                Toast.makeText(calanderActivity.this, strSelectedDate, Toast.LENGTH_SHORT).show();
                calendarClicked();

            }
        });
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String crUser= Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                String value=binding.editTextTextPersonName2.getText().toString();
                if(!value.isEmpty()){
                    database.child(crUser).child(strSelectedDate).setValue(value);
                    Toast.makeText(calanderActivity.this, "Event Saved", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(calanderActivity.this, "Nothing to save", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void calendarClicked() {
        String crUser= Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        database.child(crUser).child(strSelectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    binding.editTextTextPersonName2.setText(snapshot.getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}