package com.example.expensemanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.expensemanager.models.Transaction;
import com.example.expensemanager.utils.Constants;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainViewModel extends AndroidViewModel {

    public MutableLiveData<RealmResults<Transaction>> transaction = new MutableLiveData<>();
    public MutableLiveData<RealmResults<Transaction>> categoriesTransaction = new MutableLiveData<>();
    public MutableLiveData<Double> totalIncome = new MutableLiveData<>();
    public MutableLiveData<Double> totalExpense = new MutableLiveData<>();
    public MutableLiveData<Double> totalAmount = new MutableLiveData<>();

    Realm realm;
    Calendar calendar;

    public MainViewModel(@NonNull Application application) {
        super(application);

        Realm.init(application);
        setUpDatabase();
    }


    public void getTransactions(Calendar calendar, String type) {
        this.calendar = calendar;
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        RealmResults<Transaction> newTransactions = null;

        if (Constants.SELECTED_TAB_STATS == Constants.DAILY) {

            newTransactions = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .equalTo("type", type)
                    .findAll();


        } else if (Constants.SELECTED_TAB_STATS == Constants.MONTHLY) {
            calendar.set(Calendar.DAY_OF_MONTH, 0);

            Date startTime = calendar.getTime();

            calendar.add(Calendar.MONTH, 1);
            Date endTime = calendar.getTime();

            newTransactions = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .equalTo("type", type)
                    .findAll();


        }

        categoriesTransaction.setValue(newTransactions);

    }

    public void getTransactions(Calendar calendar) {
        this.calendar = calendar;
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        double income = 0;
        double expense = 0;
        double total = 0;
        RealmResults<Transaction> newTransactions = null;

        if (Constants.SELECTED_TAB == Constants.DAILY) {

            newTransactions = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .findAll();

            income = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .equalTo("type", Constants.INCOME)
                    .sum("amount")
                    .doubleValue();

            expense = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .equalTo("type", Constants.EXPENSE)
                    .sum("amount")
                    .doubleValue();

            total = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .sum("amount")
                    .doubleValue();

        } else if (Constants.SELECTED_TAB == Constants.MONTHLY) {
            calendar.set(Calendar.DAY_OF_MONTH, 0);

            Date startTime = calendar.getTime();

            calendar.add(Calendar.MONTH, 1);
            Date endTime = calendar.getTime();

            newTransactions = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .findAll();

            income = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .equalTo("type", Constants.INCOME)
                    .sum("amount")
                    .doubleValue();

            expense = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .equalTo("type", Constants.EXPENSE)
                    .sum("amount")
                    .doubleValue();

            total = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .sum("amount")
                    .doubleValue();

        }
        totalIncome.setValue(income);
        totalExpense.setValue(expense);
        totalAmount.setValue(total);
        transaction.setValue(newTransactions);

        /*RealmResults<Transaction> newTransactions = realm.where(Transaction.class)
                .equalTo("date", calendar.getTime())
                .findAll();*/

    }

    public void deleteTransaction(Transaction transaction) {
        realm.beginTransaction();
        transaction.deleteFromRealm();
        realm.commitTransaction();
        getTransactions(calendar);
    }

    public void addTransactions(Transaction transaction) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(transaction);
        realm.commitTransaction();
    }

    /*public void addTransactions() {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(new Transaction(Constants.INCOME, "Business", "Cash", "Some note here", new Date(), 1000, new Date().getTime()));
        realm.copyToRealmOrUpdate(new Transaction(Constants.EXPENSE, "Investment", "Bank", "Some note here", new Date(), -500, new Date().getTime()));
        realm.copyToRealmOrUpdate(new Transaction(Constants.INCOME, "Salary", "Paytm", "Some note here", new Date(), 5000, new Date().getTime()));
        realm.copyToRealmOrUpdate(new Transaction(Constants.EXPENSE, "Rent", "GPay", "Some note here", new Date(), -1500, new Date().getTime()));
        realm.copyToRealmOrUpdate(new Transaction(Constants.INCOME, "Loan", "Other", "Some note here", new Date(), 5570, new Date().getTime()));
        realm.commitTransaction();
    }*/

    void setUpDatabase() {
        realm = Realm.getDefaultInstance();
    }

}
