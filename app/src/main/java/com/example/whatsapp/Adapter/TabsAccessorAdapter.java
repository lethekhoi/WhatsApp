package com.example.whatsapp.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.whatsapp.ChatFragment;
import com.example.whatsapp.ContactsFragment;
import com.example.whatsapp.GroupsFragment;
import com.example.whatsapp.RequestFragment;

import java.util.ArrayList;
import java.util.List;

public class TabsAccessorAdapter extends FragmentPagerAdapter {
    List<Fragment> listFragment = new ArrayList<Fragment>();
    List<String> titleFragment = new ArrayList<String>();

    public TabsAccessorAdapter(@NonNull FragmentManager fm) {
        super(fm);
        listFragment.add(new ChatFragment());
        listFragment.add(new ContactsFragment());
        listFragment.add(new GroupsFragment());
        listFragment.add(new RequestFragment());
        titleFragment.add("Chat");
        titleFragment.add("Contacts");
        titleFragment.add("Groups");
        titleFragment.add("Request");
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return listFragment.get(position);
    }

    @Override
    public int getCount() {
        return listFragment.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleFragment.get(position);
    }
}
