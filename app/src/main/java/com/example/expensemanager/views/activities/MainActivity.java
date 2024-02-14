package com.example.expensemanager.views.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.expensemanager.R;
import com.example.expensemanager.adapters.TransactionsAdapter;
import com.example.expensemanager.databinding.ActivityMainBinding;
import com.example.expensemanager.models.Transaction;
import com.example.expensemanager.utils.Constants;
import com.example.expensemanager.utils.Helper;
import com.example.expensemanager.viewmodels.MainViewModel;
import com.example.expensemanager.views.fragments.AddTransactionFragment;
import com.example.expensemanager.views.fragments.StatsFragment;
import com.example.expensemanager.views.fragments.TransactionsFragment;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    com.example.expensemanager.databinding.ActivityMainBinding binding;

    Calendar calendar;
    public MainViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.expensemanager.databinding.ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Transaction");

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        Constants.setCategories();

        calendar = Calendar.getInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new TransactionsFragment());
        transaction.commit();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();

            if (item.getItemId() == R.id.transactions) {
                    getSupportFragmentManager().popBackStack();
            } else if (item.getItemId() == R.id.stats) {
                transaction1.replace(R.id.content, new StatsFragment());
                transaction1.addToBackStack(null);
            }
            transaction1.commit();

            return true;
        });

    }

    public void getTransactions() {
        viewModel.getTransactions(calendar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}