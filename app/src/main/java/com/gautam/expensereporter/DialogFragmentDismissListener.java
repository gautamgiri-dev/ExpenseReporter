package com.gautam.expensereporter;

import androidx.fragment.app.DialogFragment;

public interface DialogFragmentDismissListener<T extends DialogFragment> {
    void onDismiss(T dialog);
}
