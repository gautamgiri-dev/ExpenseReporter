package com.gautam.expensereporter;

import androidx.fragment.app.DialogFragment;

public interface AdapterListener<T> {
    void onDeleteItem(T item);
    void showDialog(DialogFragment dialog);
}
