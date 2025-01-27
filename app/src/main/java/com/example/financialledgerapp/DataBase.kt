package com.example.financialledgerapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "financialApp.db"
        const val DATABASE_VERSION = 3

        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USERNAME = "name"
        const val COLUMN_NICKNAME = "nickname"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"

        const val TABLE_EXPENSES = "expenses"
        const val COLUMN_EXPENSE_ID = "id"
        const val COLUMN_USER_ID_FK = "user_id"
        const val COLUMN_DATE = "date"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_DESCRIPTION = "description"
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
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE $TABLE_EXPENSES ADD COLUMN $COLUMN_CATEGORY TEXT DEFAULT '기타'")
        }
    }

    fun addExpense(userId: Long, amount: Double, date: String, category: String, description: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID_FK, userId)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_DATE, date)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_DESCRIPTION, description)
        }
        val newRowId = db.insert(TABLE_EXPENSES, null, values)
        db.close()
        return newRowId
    }

    fun getAllExpensesForUser(userId: Long): List<Map<String, Any>> {
        val db = readableDatabase
        val expenses = mutableListOf<Map<String, Any>>()

        val cursor = db.query(
            TABLE_EXPENSES,
            arrayOf(COLUMN_EXPENSE_ID, COLUMN_USER_ID_FK, COLUMN_AMOUNT, COLUMN_DATE, COLUMN_CATEGORY, COLUMN_DESCRIPTION),
            "$COLUMN_USER_ID_FK = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val expense = mapOf(
                    "id" to cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_ID)),
                    "userId" to cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID_FK)),
                    "amount" to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                    "date" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                    "category" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                    "description" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                )
                expenses.add(expense)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return expenses
    }

}
