package com.gautam.expensereporter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    Expense[] expenses;
    Context context;
    AdapterListener<Expense> listener;
    public ExpenseAdapter(Context context, Expense[] expenses, AdapterListener<Expense> listener) {
        this.context = context;
        this.expenses = expenses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.expense_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense expense = expenses[position];
        holder.setValues(context, expense);
        holder.itemView.setOnClickListener(v -> {
            ExpenseItemDialog dialog = new ExpenseItemDialog(new ExpenseItemDialog.ExpenseItemDialogResultListener() {
                @Override
                public void onViewInvoice(DialogFragment d) {
                    viewInvoice(expense);
                    d.dismiss();
                }

                @Override
                public void onDeleteInvoice(DialogFragment d) {
                    deleteExpense(expense);
                    d.dismiss();
                }
            });

            listener.showDialog(dialog);
        });
    }

    void deleteExpense(Expense expense) {
        listener.onDeleteItem(expense);
    }

    void viewInvoice(Expense expense) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        File invoiceFile = new File(context.getFilesDir(), expense.invoicePath);

        Log.d("invoice file path", "onBindViewHolder: " + invoiceFile.getAbsolutePath());

        Uri contentUri = InvoiceFileProvider.getUriFromFile(context, invoiceFile);

        String contentType = expense.invoiceType == InvoiceType.IMAGE ? "image/*" : "application/pdf";
        intent.setDataAndType(contentUri, contentType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return expenses.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imgExpenseType;
        final TextView txtTitle, txtAmount, txtDate, txtType;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgExpenseType = itemView.findViewById(R.id.imgExpenseType);
            txtTitle = itemView.findViewById(R.id.lblExpenseTitle);
            txtAmount = itemView.findViewById(R.id.lblExpenseAmount);
            txtDate = itemView.findViewById(R.id.lblExpenseDate);
            txtType = itemView.findViewById(R.id.lblExpenseType);
        }

        public void setValues(Context context, Expense expense) {
            int expenseType = ExpenseTypeMapper.MapToVector(expense.type);
            imgExpenseType.setImageDrawable(ContextCompat.getDrawable(context, expenseType));

            txtTitle.setText(expense.title);
            txtAmount.setText("₹" + String.format("%.2f", expense.amount));
            txtType.setText(expense.type.name());

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
            txtDate.setText(sdf.format(expense.date));
        }
    }
}
