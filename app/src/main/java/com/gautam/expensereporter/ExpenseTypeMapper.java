package com.gautam.expensereporter;

public class ExpenseTypeMapper {
    public static int MapToVector(ExpenseType expenseType) {
        switch (expenseType) {
            case FOOD:
                return R.drawable.mdi_food;
            case EQUIPMENT:
                return R.drawable.mdi_tools;
            case STAY:
                return R.drawable.mdi_home;
            case TRAVEL:
                return R.drawable.mdi_travel;
            default:
                return R.drawable.mdi_misc;
        }
    }
}
