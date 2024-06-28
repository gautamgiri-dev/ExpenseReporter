package com.gautam.expensereporter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.BufferedInputStream;
import java.io.File;

public class AttachInvoiceDialog extends DialogFragment {
    final ActivityResultLauncher<Intent> chooseFileLauncher, takePictureLauncher;
    public Uri invoicePath = null;
    public Bitmap invoiceImage = null;
    final Context context;
    public InvoiceType invoiceType = InvoiceType.NONE;
    public boolean cameraUsed = false;
    private DialogFragmentDismissListener<AttachInvoiceDialog> dismissListener = null;
    public AttachInvoiceDialog(Context context) {
        this.context = context;
        chooseFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if(intent != null) {
                            cameraUsed = false;
                            invoicePath = intent.getData();
                            invoiceType = getInvoiceType(invoicePath);
                        }
                    }
                    this.dismiss();
                }
        );

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if(intent != null && intent.getExtras() != null) {
                            cameraUsed = true;
                            invoiceImage = (Bitmap) intent.getExtras().get("data");
                            invoiceType = InvoiceType.IMAGE;
                        }
                        this.dismiss();
                    }
                }
        );
    }

    InvoiceType getInvoiceType(Uri uri) {
        ContentResolver cr = context.getContentResolver();
        String type = cr.getType(uri);

        if(type == null) return InvoiceType.NONE;

        if(type.toLowerCase().contains("image")) return InvoiceType.IMAGE;
        else if(type.toLowerCase().contains("pdf")) return InvoiceType.PDF;
        else return InvoiceType.NONE;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attach_invoice_dialog, container, false);
        Button btnChooseFromStorage = view.findViewById(R.id.btnChooseFile);
        btnChooseFromStorage.setOnClickListener(v -> launchChooseFileIntent());

        Button btnTakePicture = view.findViewById(R.id.btnTakePicture);
        btnTakePicture.setOnClickListener(v -> launchCameraIntent());
        return view;
    }

    void launchChooseFileIntent() {
        Intent chooseFileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFileIntent.setType("image/*");
        chooseFileIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "application/pdf"});
        chooseFileLauncher.launch(chooseFileIntent);
    }

    void launchCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureLauncher.launch(takePictureIntent);
    }

    void setOnDismissListener(DialogFragmentDismissListener<AttachInvoiceDialog> dismissListener) {
        this.dismissListener = dismissListener;
    }

    public boolean isInvoiceAttached() {
        return invoicePath != null || invoiceImage != null;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if(this.dismissListener != null)
            this.dismissListener.onDismiss(this);
        super.onDismiss(dialog);
    }
}
