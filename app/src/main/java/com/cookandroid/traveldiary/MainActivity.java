package com.cookandroid.traveldiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private Diary diary;
    private Expenditure expenditure;
    private Schedule schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        expenditure = new Expenditure();
        schedule = new Schedule();
        diary = new Diary();

        getSupportFragmentManager().beginTransaction().replace(R.id.containers, diary).commit();

        NavigationBarView navigationBarView = findViewById(R.id.bottom_navMenu);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.item_diary:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, diary).commit();
                        return true;
                    case R.id.item_schedule:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, schedule).commit();
                        return true;
                    case R.id.item_expenditure:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, expenditure).commit();
                        return true;
                    case R.id.item_test:
                        return true;
                }
                return false;
            }
        });
    }
}