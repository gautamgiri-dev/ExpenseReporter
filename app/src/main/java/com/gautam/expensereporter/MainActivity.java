package com.gautam.expensereporter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    AppDatabase database;
    RecyclerView lstExpenses;
    FloatingActionButton btnAddNew;
    ExpenseAdapter adapter;
    TextView lblTotalExpenses;
    double totalAmount = 0;
    Button btnExportCsv, btnExportInvoices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        handleSharedData();

        database = AppDatabase.getInstance(this);
        lstExpenses = findViewById(R.id.lstExpenses);
        lblTotalExpenses = findViewById(R.id.lblTotalExpenses);
        btnExportCsv = findViewById(R.id.btnExportCsv);
        btnExportInvoices = findViewById(R.id.btnExportInvoices);

        Expense[] expenses = database.expenseDao().getAll();

        for (Expense expense : expenses)
            totalAmount += expense.amount;

        lblTotalExpenses.setText("₹" + String.format("%.2f", totalAmount));

        adapter = new ExpenseAdapter(this, expenses, new AdapterListener<Expense>() {
            @Override
            public void onDeleteItem(Expense item) {
                File invoiceFile = new File(getFilesDir(), item.invoicePath);
                invoiceFile.delete();
                database.expenseDao().delete(item);
                refreshRecyclerView();
            }

            @Override
            public void showDialog(DialogFragment dialog) {
                dialog.show(getSupportFragmentManager(), "VIEW_EXPENSE");
            }
        });
        lstExpenses.setAdapter(adapter);
        lstExpenses.setLayoutManager(new LinearLayoutManager(this));

        btnAddNew = findViewById(R.id.btnAddNew);
        btnAddNew.setOnClickListener(v -> openNewExpenseActivity());

        btnExportCsv.setOnClickListener(v -> {
            Expense[] data = database.expenseDao().getAll();
            ExportService.ExportExpensesToCsv(this, data);
        });

        btnExportInvoices.setOnClickListener(v -> {
            Expense[] data = database.expenseDao().getAll();
            ExportService.ExportInvoices(this, data);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRecyclerView();
    }

    void refreshRecyclerView() {
        adapter.expenses = database.expenseDao().getAll();
        totalAmount = 0;
        for (Expense expense : adapter.expenses)
            totalAmount += expense.amount;
        lblTotalExpenses.setText("₹" + String.format("%.2f", totalAmount));
        adapter.notifyDataSetChanged();
    }

    void openNewExpenseActivity() {
        Intent intent = new Intent(MainActivity.this, NewExpenseActivity.class);
        startActivity(intent);
    }

    void handleSharedData() {
        Intent intent = getIntent();
        if(Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            Intent sharedIntent = new Intent(MainActivity.this, NewExpenseActivity.class);
            sharedIntent.setAction(intent.getAction());
            sharedIntent.putExtra(Intent.EXTRA_STREAM,
                    (Parcelable) intent.getParcelableExtra(Intent.EXTRA_STREAM));
            sharedIntent.setType(intent.getType());
            startActivity(sharedIntent);
        }
    }
}