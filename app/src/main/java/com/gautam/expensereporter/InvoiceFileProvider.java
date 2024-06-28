package com.gautam.expensereporter;

import android.content.Context;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;

public class InvoiceFileProvider extends FileProvider {
    public InvoiceFileProvider() {
        super(R.xml.invoice_path);
    }
    public static Uri getUriFromFile(Context context, File file) {
        return InvoiceFileProvider.getUriForFile(context, "com.mydomain.fileprovider", file);
    }
}
