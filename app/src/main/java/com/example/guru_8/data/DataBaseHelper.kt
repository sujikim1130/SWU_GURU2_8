package com.example.guru_8.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "financialApp.db"
        const val DATABASE_VERSION = 4  // ✅ 버전 업데이트 (date 추가)

        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USERNAME = "name"
        const val COLUMN_NICKNAME = "nickname"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"

        const val TABLE_NAME = "expenses"
        const val COLUMN_ID = "id"
        const val COLUMN_DETAIL = "detail"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_TRANSACTION_TYPE = "transaction_type"
        const val COLUMN_DATE = "date"  // ✅ 날짜 컬럼 추가
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL,
                $COLUMN_NICKNAME TEXT NOT NULL,
                $COLUMN_PHONE TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

        val createExpensesTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DETAIL TEXT NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_AMOUNT REAL NOT NULL,
                $COLUMN_TRANSACTION_TYPE TEXT,
                $COLUMN_DATE TEXT NOT NULL  
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createExpensesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 4) {  // ✅ 버전 4 이상에서 날짜 컬럼 추가
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_DATE TEXT DEFAULT ''")
        }
    }

    fun addExpense(amount: Double, detail: String, transactionType: String, category: String, date: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_DETAIL, detail)
            put(COLUMN_TRANSACTION_TYPE, transactionType)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_DATE, date)  // ✅ 날짜 저장
        }
        val newRowId = db.insert(TABLE_NAME, null, values)
        db.close()
        return newRowId
    }

    fun getExpensesByDate(date: String): List<Expense> {
        val db = readableDatabase
        val expenses = mutableListOf<Expense>()

        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_AMOUNT, COLUMN_DETAIL, COLUMN_CATEGORY, COLUMN_TRANSACTION_TYPE, COLUMN_DATE),
            "$COLUMN_DATE = ?",
            arrayOf(date),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
                val detail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DETAIL))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                val transactionType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_TYPE))
                val expenseDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))

                expenses.add(Expense(id, amount, detail, transactionType, category, expenseDate))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return expenses
    }
}