package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatsapp.Adapter.TabsAccessorAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    DatabaseReference mDatabase;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabsAccessorAdapter tabsAccessorAdapter;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    String currentUserID;
    Date currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        currentUserID = currentUser == null ? "" : mAuth.getCurrentUser().getUid();
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
        switch (id) {
            case R.id.logout:

                mAuth.signOut();
                SendUserToLoginActivity();

                break;
            case R.id.settings:
                SendUserToSettingActivity();
                break;
            case R.id.main_find_friend_option:
                SendUserToFindFriendActivity();
                break;
            case R.id.creatGroup:
                RequestNewGroup();
                break;

        }


        return true;
    }

    private void SendUserToFindFriendActivity() {
        Intent iFindFriend = new Intent(MainActivity.this, FindFriendActivity.class);
        startActivity(iFindFriend);
    }

    private void RequestNewGroup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Nhập tên nhóm");
        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("e.g Coding Cafe");
        builder.setView(groupNameField);
        builder.setPositiveButton("Tạo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupNameField.getText().toString().trim();
                if (groupName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập tên nhóm", Toast.LENGTH_SHORT).show();

                } else {
                    CreatNewGroup(groupName);
                }

            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void CreatNewGroup(final String groupName) {
        currentTime = Calendar.getInstance().getTime();
        HashMap<String, String> profileMap = new HashMap<>();
        profileMap.put("name", groupName);
        profileMap.put("userID", currentUserID);
        profileMap.put("time", currentTime.toString());
        mDatabase.child("Group").child(groupName).setValue(profileMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, groupName + " được tạo thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, " Tạo thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            SendUserToLoginActivity();
        } else {
            updateUserState("online");

            VerifyUserExistance();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (currentUser != null) {
            updateUserState("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentUser != null) {
            updateUserState("offline");
        }
    }

    private void VerifyUserExistance() {
        String currentUserID = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists()) {

                } else {
                    SendUserToSettingActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToLoginActivity() {
        Intent iLogin = new Intent(MainActivity.this, LoginActivity.class);
        iLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(iLogin);
        finish();

    }

    private void SendUserToSettingActivity() {
        Intent iSetting = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(iSetting);


    }

    private void init() {
        toolbar = findViewById(R.id.main_app_bar);
        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewPager);
    }

    private void updateUserState(String state) {
        String saveCurrentTime, saveCurrentDay;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDay = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDay = currentDay.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("day", saveCurrentDay);
        onlineStateMap.put("state", state);
        mDatabase.child("users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }
}
