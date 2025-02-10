package com.example.e_comm;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import com.example.e_comm.Fragments.CanceledOrdersFragment;
import com.example.e_comm.Fragments.InProcessSellerFragment;
import com.example.e_comm.Fragments.InShippedOrdersFragment;
import com.example.e_comm.databinding.ActivitySellerOrdersBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SellerOrdersActivity extends AppCompatActivity {
    private ActivitySellerOrdersBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_seller_orders);

        // Set up Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mes Commandes");
        }

        // Set up ViewPager2 with Adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        binding.viewpager.setAdapter(adapter);

        // Attach TabLayout with ViewPager2 using TabLayoutMediator
        new TabLayoutMediator(binding.tabs, binding.viewpager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("In Process");
                    break;
                case 1:
                    tab.setText("Canceled");
                    break;
                case 2:
                    tab.setText("Shipped");
                    break;
            }
        }).attach();
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragments = new ArrayList<>();

        public ViewPagerAdapter(FragmentActivity activity) {
            super(activity);
            fragments.add(new InProcessSellerFragment());
            fragments.add(new CanceledOrdersFragment());
            fragments.add(new InShippedOrdersFragment());
        }

        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }
}
