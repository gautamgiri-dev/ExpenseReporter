package com.gautam.expensereporter;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Query("SELECT * FROM expense order by date desc")
    Expense[] getAll();

    @Query("SELECT * FROM expense where id = :id")
    Expense get(int id);

    @Insert
    void insert(Expense... expenses);

    @Delete
    void delete(Expense expense);

    @Update
    void update(Expense expense);
}
