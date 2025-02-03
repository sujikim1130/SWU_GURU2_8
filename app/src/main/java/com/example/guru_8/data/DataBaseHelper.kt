package com.example.guru_8.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "financialApp.db"
        const val DATABASE_VERSION = 4

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
        const val COLUMN_DATE = "date"
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

    fun addExpense(amount: Double, detail: String, transactionType: String, category: String, date: String): Long {        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_DETAIL, detail)
            put(COLUMN_TRANSACTION_TYPE, transactionType)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_DATE, date)

        }
        val newRowId = db.insert(TABLE_NAME, null, values)
        db.close()
        return newRowId
    }

    fun deleteExpense(expenseId: Long): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(expenseId.toString()))
        db.close()
        return result > 0 // 삭제 성공 여부 반환
    }


    fun getAllExpensesForUser(data:String): List<Expense> {
        val db = readableDatabase
        val expenses = mutableListOf<Expense>()

        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_AMOUNT, COLUMN_DETAIL, COLUMN_CATEGORY, COLUMN_TRANSACTION_TYPE, COLUMN_DATE),
            "$COLUMN_DATE = ?",
            arrayOf(data),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))  // getLong() 사용
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)).toDouble()
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

    fun getAllExpenses(): List<Expense> {
        val db = readableDatabase
        val expenses = mutableListOf<Expense>()

        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_AMOUNT, COLUMN_DETAIL, COLUMN_CATEGORY, COLUMN_TRANSACTION_TYPE, COLUMN_DATE),
            null,  // 🔥 날짜 조건 제거
            null,
            null,
            null,
            "$COLUMN_DATE DESC" // 최신 순 정렬
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
    fun getTotalSpending(): Int {
        val db = readableDatabase
        var totalSpending = 0

        val cursor = db.rawQuery(
            "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_NAME WHERE $COLUMN_TRANSACTION_TYPE = ?",
            arrayOf("지출")
        )

        if (cursor.moveToFirst()) {
            totalSpending = cursor.getInt(0) // 첫 번째 컬럼의 값을 가져옴
        }

        cursor.close()
        db.close()
        return totalSpending
    }



}
