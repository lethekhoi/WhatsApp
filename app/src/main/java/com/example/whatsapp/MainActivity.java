package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.whatsapp.Adapter.TabsAccessorAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabsAccessorAdapter tabsAccessorAdapter;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("WhatsApp");

        tabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsAccessorAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.logout:

                mAuth.signOut();
                SendUserToLoginActivity();

                break;
            case R.id.settings:
                SendUserToSettingActivity();
                break;
            case R.id.main_find_friend_option:

                break;
        }


        return true;
    }

    private void SendUserToSettingActivity() {
        Intent iSetting= new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(iSetting);
    }

    @Override
    protected void onStart() {
        super.onStart();
         if (currentUser==null){
             SendUserToLoginActivity();
         }else{
             VerifyUserExistance();
         }
    }

    private void VerifyUserExistance() {

    }

    private void SendUserToLoginActivity() {
        Intent iLogin= new Intent(MainActivity.this, LoginActivity.class);
        iLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(iLogin);
        finish();

    }

    private void init() {
        toolbar = findViewById(R.id.main_app_bar);
        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewPager);
    }
}
