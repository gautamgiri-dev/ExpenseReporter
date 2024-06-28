package com.gautam.expensereporter;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Query("SELECT * FROM expense")
    Expense[] getAll();

    @Insert
    void insert(Expense... expenses);

    @Delete
    void delete(Expense expense);
}
