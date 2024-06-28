package com.gautam.expensereporter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class ExpenseTypeAdapter extends ArrayAdapter<ExpenseType> {
    public ExpenseTypeAdapter(@NonNull Context context, ArrayList<ExpenseType> expenseTypes) {
        super(context, 0, expenseTypes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.spinner_expense_type, parent, false);

        TextView txtExpenseType = convertView.findViewById(R.id.txtExpenseType);
        ImageView imgExpenseType = convertView.findViewById(R.id.imgExpenseType);

        ExpenseType expenseType = getItem(position);
        if(expenseType != null){
            Drawable expenseImage = ContextCompat.getDrawable(getContext(), ExpenseTypeMapper.MapToVector(expenseType));
            imgExpenseType.setImageDrawable(expenseImage);
            txtExpenseType.setText(expenseType.name());
        }

        return convertView;
    }
}
