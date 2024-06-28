package com.gautam.expensereporter;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ExpenseItemDialog extends DialogFragment {
    public interface ExpenseItemDialogResultListener {
        void onViewInvoice(DialogFragment d);
        void onDeleteInvoice(DialogFragment d);
    }

    private final ExpenseItemDialogResultListener resultListener;
    public ExpenseItemDialog(ExpenseItemDialogResultListener resultListener) {
        this.resultListener = resultListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_expense_dialog, container, false);
        Button btnViewInvoice, btnDeleteExpense;

        btnViewInvoice = view.findViewById(R.id.btnViewInvoice);
        btnDeleteExpense = view.findViewById(R.id.btnDeleteExpense);

        btnViewInvoice.setOnClickListener(v -> this.resultListener.onViewInvoice(this));
        btnDeleteExpense.setOnClickListener(v -> this.resultListener.onDeleteInvoice(this));
        return view;
    }
}
