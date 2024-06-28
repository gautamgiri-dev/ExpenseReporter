package com.gautam.expensereporter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExportService {
    public static void ExportExpensesToCsv(Context context, Expense[] expenses) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        String filename = "expense-report_" + sdf.format(Calendar.getInstance().getTime()) + ".csv";

        File csvFile = new File(context.getFilesDir(), filename);
        try (FileWriter fw = new FileWriter(csvFile)) {
            fw.write("date,type,title,note,invoice_file,amount\n");
            for (Expense expense :
                    expenses) {
                StringBuilder sb = new StringBuilder();
                sb.append(dateFormat.format(expense.date));
                sb.append(',');
                sb.append(expense.type.name());
                sb.append(',');
                if(expense.title.contains(",")) sb.append("\"");
                sb.append(expense.title);
                if(expense.title.contains(",")) sb.append("\"");
                sb.append(',');
                if(expense.note.contains(",")) sb.append("\"");
                sb.append(expense.note);
                if(expense.note.contains(",")) sb.append("\"");
                sb.append(',');
                sb.append(expense.invoicePath);
                sb.append(',');
                sb.append(expense.amount);
                sb.append('\n');

                fw.write(sb.toString());
            }
            SendFile(context, csvFile, "text/csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ExportInvoices(Context context, Expense[] expenses) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String filename = "expense-invoices_" + sdf.format(Calendar.getInstance().getTime()) + ".zip";

        File zipFile = new File(context.getFilesDir(), filename);
        try  {
            FileOutputStream dest = new FileOutputStream(zipFile);

            ZipOutputStream out = new ZipOutputStream(dest);

            byte[] data = new byte[1024];

            for (Expense expense : expenses) {
                Log.d("add:", expense.invoicePath);
                Log.v("Compress", "Adding: " + expense.invoicePath);
                File inFile = new File(context.getFilesDir(), expense.invoicePath);
                FileInputStream fi = new FileInputStream(inFile);

                ZipEntry entry = new ZipEntry(expense.invoicePath);
                out.putNextEntry(entry);
                int count;
                while ((count = fi.read(data, 0, 1024)) != -1) {
                    out.write(data, 0, count);
                }
                fi.close();
            }
            out.close();
            dest.close();
            SendFile(context, zipFile, "application/zip");
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public static void SendFile(Context context, File file, String mimeType) {
        Uri fileUri = InvoiceFileProvider.getUriFromFile(context, file);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        sendIntent.setType(mimeType);
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent shareIntent = Intent.createChooser(sendIntent, "Share invoice csv");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(shareIntent);
    }
}
