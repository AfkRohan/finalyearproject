package com.example.chatapplication.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chatapplication.Fragments.CallsFragment;
import com.example.chatapplication.Fragments.ChatsFragment;
import com.example.chatapplication.Fragments.GroupFragment;
import com.example.chatapplication.Fragments.StatusFragment;

public class FragmentsAdapter extends FragmentPagerAdapter {
    public FragmentsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0: return new ChatsFragment();
            case 1: return new GroupFragment();
            case 2: return new CallsFragment();
            case 3: return new StatusFragment();
            default: return new ChatsFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            title ="CHATS";
        }
        if (position == 1) {
            title ="GROUPS";
        }
        if (position == 2) {
            title ="CALLS";
        }
        if(position==3){
            title="NEWS";
        }
        return title;
    }
}
