package com.example.financialledgerapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "expenses.db"  // 데이터베이스 이름
        private const val DATABASE_VERSION = 3           // 데이터베이스 버전
        private const val TABLE_NAME = "expenses"        // 테이블 이름
        private const val COLUMN_ID = "id"              // ID 컬럼
        private const val COLUMN_AMOUNT = "amount"      // 금액 컬럼
        private const val COLUMN_DETAIL = "detail"      // 내역 컬럼
        private const val COLUMN_TRANSACTION_TYPE = "transaction_type"  // 거래 유형 컬럼
        private const val COLUMN_CATEGORY = "category"  // 카테고리 컬럼 추가
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_AMOUNT REAL,
                $COLUMN_DETAIL TEXT,
                $COLUMN_TRANSACTION_TYPE TEXT,
                $COLUMN_CATEGORY TEXT  -- 카테고리 컬럼 추가
            )
        """
        db.execSQL(createTableQuery)  // 테이블 생성
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_CATEGORY TEXT DEFAULT '기타'")
        }
    }

    // insert()가 Long을 반환하므로, 해당 값을 반환하도록
    fun addExpense(amount: Double, detail: String, transactionType: String, category: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_DETAIL, detail)
            put(COLUMN_TRANSACTION_TYPE, transactionType)
            put(COLUMN_CATEGORY, category)
        }
        val newRowId = db.insert(TABLE_NAME, null, values)
        db.close()
        return newRowId  // **Long 반환**
    }

    // getLong()을 사용하여 ID를 가져오도록
    fun getAllExpenses(): List<Expense> {
        val db = readableDatabase
        val expenses = mutableListOf<Expense>()

        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_AMOUNT, COLUMN_DETAIL, COLUMN_TRANSACTION_TYPE, COLUMN_CATEGORY),
            null,
            null,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))  // getLong() 사용
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
                val detail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DETAIL))
                val transactionType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_TYPE))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))

                expenses.add(Expense(id, amount, detail, transactionType, category))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return expenses
    }

    fun updateExpense(id: Long, amount: Double, detail: String, transactionType: String, category: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_DETAIL, detail)
            put(COLUMN_TRANSACTION_TYPE, transactionType)
            put(COLUMN_CATEGORY, category)
        }
        db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteExpense(id: Long) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }
}