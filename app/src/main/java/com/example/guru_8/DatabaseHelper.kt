package com.example.guru_8

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "BudgetApp.db"
        const val DATABASE_VERSION = 2

        // Users 테이블
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_USERNAME = "name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"

        // Expenses 테이블
        private const val TABLE_EXPENSES = "expenses"
        private const val COLUMN_EXPENSE_ID = "id"
        private const val COLUMN_USER_ID_FK = "user_id"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Users 테이블 생성
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

        // Expenses 테이블 생성
        val createExpensesTable = """
            CREATE TABLE $TABLE_EXPENSES (
                $COLUMN_EXPENSE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_ID_FK INTEGER NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_AMOUNT REAL NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                FOREIGN KEY($COLUMN_USER_ID_FK) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createExpensesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }
}
