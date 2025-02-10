package com.example.e_comm;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;


import com.example.e_comm.Fragments.InProcessOrdersFragment;
import com.example.e_comm.Fragments.InShippedOrdersFragment;


import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class UsersOrdersActivity extends com.example.e_comm.BaseActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_users_orders);



        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Your Orders");
        }

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);

        setupViewPager(viewPager, tabLayout);
    }

    private void setupViewPager(ViewPager2 viewPager, TabLayout tabLayout) {
        OrdersPagerAdapter adapter = new OrdersPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("In Process");
            } else {
                tab.setText("Shipped");
            }
        }).attach();
    }

    private static class OrdersPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();

        public OrdersPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
            fragmentList.add(new InProcessOrdersFragment());
            fragmentList.add(new InShippedOrdersFragment());
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }
    }
}
