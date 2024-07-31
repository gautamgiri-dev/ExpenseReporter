package com.gautam.expensereporter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewExpenseActivity extends AppCompatActivity {
    Spinner expenseTypeSpinner;
    Button btnAttachInvoice, btnSave, btnCancel;
    AppDatabase database;
    Expense expense;
    LinearLayout dateTimePicker;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    TextView txtExpenseDate;
    Calendar selectedDateTime;
    AttachInvoiceDialog dialog;
    ImageView imgInvoice;
    EditText txtTitle, txtNote, txtAmount;
    final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
    boolean isEditMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_expense);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = AppDatabase.getInstance(this);

        int editInvoiceIndex = getIntent().getIntExtra("invoiceId",-1);
        isEditMode = editInvoiceIndex != -1;
        if(!isEditMode)
            expense = new Expense();
        else
            expense = database.expenseDao().get(editInvoiceIndex);

        btnAttachInvoice = findViewById(R.id.btnAttachInvoice);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        expenseTypeSpinner = findViewById(R.id.cboExpenseType);
        dateTimePicker = findViewById(R.id.dateTimePicker);
        txtExpenseDate = findViewById(R.id.txtExpenseDate);
        txtTitle = findViewById(R.id.txtExpenseTitle);
        txtNote = findViewById(R.id.txtExpenseNote);
        txtAmount = findViewById(R.id.txtExpenseAmount);

        imgInvoice = findViewById(R.id.imgInvoicePreview);

        dialog = new AttachInvoiceDialog(this);

        btnAttachInvoice.setOnClickListener(v -> initAttachFileDialog());
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> SaveExpense());

        initExpenseTypeSpinner();
        initDatePickerDialog();


        if(isEditMode && !isSharedInvoice()) handleEditInvoice();
        else handleNewInvoice();

        if(isSharedInvoice()) handleSharedInvoice();
        dateTimePicker.setOnClickListener(v -> datePickerDialog.show());
    }

    void handleEditInvoice() {
        Log.d("Edit Invoice", "handleEditInvoice: Editing invoice " + expense.invoicePath);
        btnSave.setText("Update");
        txtTitle.setText(expense.title);
        txtNote.setText(expense.note);
        txtAmount.setText(String.valueOf(expense.amount));
        if(expense.invoiceType == InvoiceType.IMAGE){
            File f = new File(getFilesDir(), expense.invoicePath);
            Uri uri = Uri.fromFile(f);
            imgInvoice.setImageURI(uri);
        }else{
            imgInvoice.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mdi_pdf));
        }
        imgInvoice.setVisibility(View.VISIBLE);
        txtExpenseDate.setText(sdf.format(expense.date));
    }

    void handleNewInvoice() {
        selectedDateTime = Calendar.getInstance();
        expense.date = selectedDateTime.getTime();
        txtExpenseDate.setText(sdf.format(selectedDateTime.getTime()));
    }

    boolean isSharedInvoice() {
        String action = getIntent().getAction();
        return action != null && action.equals(Intent.ACTION_SEND);
    }

    void handleSharedInvoice() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        Log.d("Shared Invoice", "handleSharedInvoice: " + action + " " + type);

        if(type == null) return;
        boolean validRequest = type.startsWith("image") || type.equals("application/pdf");
        if(Intent.ACTION_SEND.equals(action) && validRequest && dialog != null) {
            dialog.cameraUsed = false;
            dialog.invoicePath = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if(type.startsWith("image")) dialog.invoiceType = InvoiceType.IMAGE;
            else dialog.invoiceType = InvoiceType.PDF;

            if(dialog.invoiceType == InvoiceType.IMAGE)
                imgInvoice.setImageURI(dialog.invoicePath);
            else
                imgInvoice.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mdi_pdf));
            imgInvoice.setVisibility(View.VISIBLE);
        }
    }

    void initAttachFileDialog() {
        dialog.show(getSupportFragmentManager(), "ATTACH_INVOICE");

        dialog.setOnDismissListener(dl -> {
            if (dl.cameraUsed) {
                Bitmap image = dl.invoiceImage;
                imgInvoice.setImageBitmap(image);
            } else {
                if (dl.invoiceType == InvoiceType.IMAGE) {
                    imgInvoice.setImageURI(dl.invoicePath);
                } else {
                    imgInvoice.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mdi_pdf));
                }
            }

            if (dl.invoicePath != null || dl.invoiceImage != null) {
                imgInvoice.setVisibility(View.VISIBLE);
            } else {
                imgInvoice.setVisibility(View.INVISIBLE);
            }
        });
    }

    void initExpenseTypeSpinner() {
        ArrayList<ExpenseType> expenseTypes = new ArrayList<>();
        expenseTypes.add(ExpenseType.FOOD);
        expenseTypes.add(ExpenseType.TRAVEL);
        expenseTypes.add(ExpenseType.STAY);
        expenseTypes.add(ExpenseType.EQUIPMENT);
        expenseTypes.add(ExpenseType.MISCELLANEOUS);
        ExpenseTypeAdapter adapter = new ExpenseTypeAdapter(this, expenseTypes);

        expenseTypeSpinner.setAdapter(adapter);
        expenseTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                expense.type = expenseTypes.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                expense.type = ExpenseType.MISCELLANEOUS;
            }
        });
    }

    void initDatePickerDialog() {
        Calendar now = Calendar.getInstance();
        if(isEditMode)
        {
            now.setTime(expense.date);
            selectedDateTime = now;
        }

        datePickerDialog = new DatePickerDialog(this);
        datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            selectedDateTime.set(Calendar.YEAR, year);
            selectedDateTime.set(Calendar.MONTH, month);
            selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            timePickerDialog.show();
        });

        timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            Log.d("Date Test", "initDatePickerDialog: " + selectedDateTime.get(Calendar.DAY_OF_MONTH));
            selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDateTime.set(Calendar.MINUTE, minute);

            Log.d("Date Test", "initDatePickerDialog: " + selectedDateTime.get(Calendar.DAY_OF_MONTH));
            Date date = selectedDateTime.getTime();
            expense.date = date;
            txtExpenseDate.setText(sdf.format(date));
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
    }

    String copyInvoiceDocumentToApp(Uri uri, String destinationFilename) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            try (OutputStream outputStream = openFileOutput(destinationFilename, Context.MODE_PRIVATE)) {
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                return destinationFilename;
            }
        } catch (IOException e) {
            Log.d("COPY FILE", "copyInvoiceDocumentToApp: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    String writeInvoiceImageToDocument(Bitmap bitmap, String destinationFilename) {
        try (OutputStream outputStream = openFileOutput(destinationFilename, Context.MODE_PRIVATE)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            return destinationFilename;
        } catch (IOException e) {
            Log.d("COPY FILE", "copyInvoiceDocumentToApp: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    void SaveExpense() {
        expense.title = txtTitle.getText().toString();
        expense.note = txtNote.getText().toString();
        try {
            expense.amount = Double.parseDouble(txtAmount.getText().toString());
        } catch (NumberFormatException ne) {
            Toast.makeText(this, "Amount cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (expense.title.isEmpty()) {
            Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (expense.amount <= 0) {
            Toast.makeText(this, "Amount should be non zero", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!dialog.isInvoiceAttached() && !isEditMode) {
            Toast.makeText(this, "No invoice attached", Toast.LENGTH_SHORT).show();
            return;
        }

        // copy invoice to local folder
        if(dialog.isInvoiceAttached()) {
            SimpleDateFormat _sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String destinationFileName = expense.title + "_" + _sdf.format(expense.date);
            String extension = ".jpg";
            if (!dialog.cameraUsed) {
                Uri invoicePath = dialog.invoicePath;
                if (dialog.invoiceType == InvoiceType.IMAGE) {
                    String filename = getFileNameFromUri(this, invoicePath);
                    int index = filename.lastIndexOf('.');
                    if (index != -1) extension = filename.substring(index);
                } else if (dialog.invoiceType == InvoiceType.PDF) {
                    extension = ".pdf";
                } else return;
                expense.invoicePath = copyInvoiceDocumentToApp(invoicePath, destinationFileName + extension);
            } else {
                if (dialog.invoiceImage != null)
                    expense.invoicePath = writeInvoiceImageToDocument(dialog.invoiceImage, destinationFileName + ".jpg");
            }
            expense.invoiceType = dialog.invoiceType;
        }
        String message;
        if(isEditMode){
            database.expenseDao().update(expense);
            message = "Expense updated succesfully!";
        }else{
            database.expenseDao().insert(expense);
            message = "Expense recorded succesfully!";
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    private static String getFileNameFromUri(Context context, Uri uri) {
        String fileName = null;
        try {
            fileName = context.getContentResolver().getType(uri);
            if (fileName != null && fileName.startsWith("text/")) {
                // Extract filename from content type (e.g., text/plain -> plain.txt)
                fileName = fileName.substring(5);
            } else {
                // Fallback to default filename or get filename from the Uri path (less reliable)
                String path = uri.getPath();
                if (path != null) {
                    int index = path.lastIndexOf('/');
                    if (index != -1) {
                        fileName = path.substring(index + 1);
                    }
                }
            }
        } catch (Exception e) {
            // Handle exceptions (e.g., unable to get content type)
        }
        return fileName != null ? fileName : "default.jpg";
    }
}