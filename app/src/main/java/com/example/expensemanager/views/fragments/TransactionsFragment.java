package com.example.expensemanager.views.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.expensemanager.R;
import com.example.expensemanager.adapters.TransactionsAdapter;
import com.example.expensemanager.databinding.FragmentAddTransactionBinding;
import com.example.expensemanager.databinding.FragmentTransactionsBinding;
import com.example.expensemanager.models.Transaction;
import com.example.expensemanager.utils.Constants;
import com.example.expensemanager.utils.Helper;
import com.example.expensemanager.viewmodels.MainViewModel;
import com.example.expensemanager.views.activities.MainActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

import io.realm.RealmResults;

public class TransactionsFragment extends Fragment {

    public TransactionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    FragmentTransactionsBinding binding;

    Calendar calendar;
    public MainViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTransactionsBinding.inflate(inflater);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        calendar = Calendar.getInstance();
        updateDate();

        binding.nextDateBtn.setOnClickListener(v -> {
            if (Constants.SELECTED_TAB == Constants.DAILY) {
                calendar.add(Calendar.DATE, 1);
            } else if (Constants.SELECTED_TAB == Constants.MONTHLY) {
                calendar.add(Calendar.MONTH, 1);
            }
            updateDate();
        });
        binding.previousDateBtn.setOnClickListener(v -> {
            if (Constants.SELECTED_TAB == Constants.DAILY) {
                calendar.add(Calendar.DATE, -1);
            } else if (Constants.SELECTED_TAB == Constants.MONTHLY) {
                calendar.add(Calendar.MONTH, -1);
            }
            updateDate();
        });

        binding.floatingActionButton.setOnClickListener(c -> {
            new AddTransactionFragment().show(getParentFragmentManager(), null);
        });


        binding.transactionsList.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel.transaction.observe(getViewLifecycleOwner(), new Observer<RealmResults<Transaction>>() {
            @Override
            public void onChanged(RealmResults<Transaction> transactions) {
                TransactionsAdapter transactionsAdapter = new TransactionsAdapter(getActivity(), transactions);
                binding.transactionsList.setAdapter(transactionsAdapter);
                if (transactions.size() > 0) {
                    binding.emptyState.setVisibility(View.GONE);
                } else {
                    binding.emptyState.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.totalIncome.observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                binding.incomeLbl.setText(String.valueOf(aDouble));
            }
        });

        viewModel.totalExpense.observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                binding.expenseLbl.setText(String.valueOf(aDouble));
            }
        });

        viewModel.totalAmount.observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                binding.totalLbl.setText(String.valueOf(aDouble));
            }
        });

        viewModel.getTransactions(calendar);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("Monthly")) {
                    Constants.SELECTED_TAB = 1;
                    updateDate();
                } else if (tab.getText().equals("Daily")) {
                    Constants.SELECTED_TAB = 0;
                    updateDate();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return binding.getRoot();
    }

    void updateDate() {
        if (Constants.SELECTED_TAB == Constants.DAILY) {
            binding.currentDate.setText(Helper.formatDate(calendar.getTime()));
        } else if (Constants.SELECTED_TAB == Constants.MONTHLY) {
            binding.currentDate.setText(Helper.formatDateByMonth(calendar.getTime()));
        }
        viewModel.getTransactions(calendar);
    }

}