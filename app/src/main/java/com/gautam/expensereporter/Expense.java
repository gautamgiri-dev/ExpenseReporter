package com.gautam.expensereporter;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Expense {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "note")
    public String note;

    @ColumnInfo(name = "amount")
    public double amount;

    @ColumnInfo(name = "type")
    public ExpenseType type;

    @ColumnInfo(name = "date")
    public Date date;

    @ColumnInfo(name = "invoice_path")
    public String invoicePath;

    @ColumnInfo(name = "invoice_type")
    public InvoiceType invoiceType;
}
