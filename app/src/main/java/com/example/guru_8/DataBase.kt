package com.example.financialledgerapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DataBase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "financialApp.db"  // 데이터베이스 이름
        const val DATABASE_VERSION = 3               // 데이터베이스 버전

        // Users 테이블
        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USERNAME = "name"
        const val COLUMN_NICKNAME = "nickname"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"

        // Expenses 테이블
        const val TABLE_EXPENSES = "expenses"
        const val COLUMN_EXPENSE_ID = "id"
        const val COLUMN_USER_ID_FK = "user_id"
        const val COLUMN_DATE = "date"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Users 테이블 생성
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
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE $TABLE_EXPENSES ADD COLUMN $COLUMN_CATEGORY TEXT DEFAULT '기타'")
        }
    }

    // Users 테이블 관리 메서드
    fun addUser(name: String, nickname: String, phone: String, email: String, password: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, name)
            put(COLUMN_NICKNAME, nickname)
            put(COLUMN_PHONE, phone)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
        }
        val newRowId = db.insert(TABLE_USERS, null, values)
        db.close()
        return newRowId
    }

    fun getAllUsers(): List<Map<String, String>> {
        val db = readableDatabase
        val users = mutableListOf<Map<String, String>>()

        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID, COLUMN_USERNAME, COLUMN_NICKNAME, COLUMN_PHONE, COLUMN_EMAIL),
            null,
            null,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val user = mapOf(
                    "id" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                    "name" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                    "nickname" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME)),
                    "phone" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                    "email" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                )
                users.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return users
    }

    // Expenses 테이블 관리 메서드
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

    fun getAllExpenses(): List<Map<String, Any>> {
        val db = readableDatabase
        val expenses = mutableListOf<Map<String, Any>>()

        val cursor = db.query(
            TABLE_EXPENSES,
            arrayOf(COLUMN_EXPENSE_ID, COLUMN_USER_ID_FK, COLUMN_AMOUNT, COLUMN_DATE, COLUMN_CATEGORY, COLUMN_DESCRIPTION),
            null,
            null,
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

    fun updateExpense(id: Long, amount: Double, date: String, category: String, description: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_DATE, date)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_DESCRIPTION, description)
        }
        db.update(TABLE_EXPENSES, values, "$COLUMN_EXPENSE_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteExpense(id: Long) {
        val db = writableDatabase
        db.delete(TABLE_EXPENSES, "$COLUMN_EXPENSE_ID = ?", arrayOf(id.toString()))
        db.close()
    }
}
