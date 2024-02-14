package com.example.expensemanager.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.R;
import com.example.expensemanager.databinding.RowTransactionBinding;
import com.example.expensemanager.models.Category;
import com.example.expensemanager.models.Transaction;
import com.example.expensemanager.utils.Constants;
import com.example.expensemanager.utils.Helper;
import com.example.expensemanager.views.activities.MainActivity;

import java.util.ArrayList;

import io.realm.RealmResults;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder> {

    Context context;
    RealmResults<Transaction> transactions;

    public TransactionsAdapter(Context context, RealmResults<Transaction> transactionArrayList) {
        this.context = context;
        this.transactions = transactionArrayList;
    }

    @NonNull
    @Override
    public TransactionsAdapter.TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionViewHolder(LayoutInflater.from(context).inflate(R.layout.row_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsAdapter.TransactionViewHolder holder, int position) {

        Transaction transaction = transactions.get(position);

        holder.binding.transactionAmount.setText(String.valueOf(transaction.getAmount()));
        holder.binding.accountLable.setText(transaction.getAccount());
        holder.binding.transactionDate.setText(Helper.formatDate(transaction.getDate()));
        holder.binding.transactionCategory.setText(transaction.getCategory());

        Category transactionCategory = Constants.getCategoryDetails(transaction.getCategory());

        holder.binding.categoryIcon.setImageResource(transactionCategory.getCategoryImage());
        holder.binding.categoryIcon.setBackgroundTintList(context.getColorStateList(transactionCategory.getCategoryColor()));
        holder.binding.accountLable.setBackgroundTintList(context.getColorStateList(Constants.getAccountsColor(transaction.getAccount())));

        if (transaction.getType().equals(Constants.INCOME)) {
            holder.binding.transactionAmount.setTextColor(context.getColor(R.color.green));
        } else if (transaction.getType().equals(Constants.EXPENSE)) {
            holder.binding.transactionAmount.setTextColor(context.getColor(R.color.red));
        }

        holder.itemView.setOnLongClickListener(v ->
        {
            AlertDialog deleteDialog = new AlertDialog.Builder(context)
                    .create();
            deleteDialog.setTitle("Delete Transaction");
            deleteDialog.setMessage("Are you sure to delete this transaction?");

            deleteDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Delete", (dialog, which) -> {
                ((MainActivity) context).viewModel.deleteTransaction(transaction);
            });
            deleteDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", (dialog, which) -> deleteDialog.dismiss());

            deleteDialog.show();
            return false;
        });

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        RowTransactionBinding binding;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = RowTransactionBinding.bind(itemView);
        }
    }
}
