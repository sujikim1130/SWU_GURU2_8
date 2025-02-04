package com.example.guru_8.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * SQLite 데이터베이스를 관리하는 클래스
 */
class DataBaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "financialApp.db" // 데이터베이스 이름
        const val DATABASE_VERSION = 4 // 데이터베이스 버전

        // 사용자 테이블 관련 상수
        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USERNAME = "name"
        const val COLUMN_NICKNAME = "nickname"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"

        // 지출 테이블 관련 상수
        const val TABLE_NAME = "expenses"
        const val COLUMN_ID = "id"
        const val COLUMN_DETAIL = "detail"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_TRANSACTION_TYPE = "transaction_type"
        const val COLUMN_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 사용자 테이블 생성
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

        // 지출 테이블 생성
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
        // 데이터베이스 업그레이드: 버전 4 이상에서 날짜 컬럼 추가
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_DATE TEXT DEFAULT ''")
        }
    }

    /**
     * 지출 내역 추가 함수
     */
    fun addExpense(amount: Double, detail: String, transactionType: String, category: String, date: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_DETAIL, detail)
            put(COLUMN_TRANSACTION_TYPE, transactionType)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_DATE, date)
        }
        return db.insert(TABLE_NAME, null, values).also { db.close() }
    }

    /**
     * 지출 내역 삭제 함수
     */
    fun deleteExpense(expenseId: Long): Boolean {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(expenseId.toString())) > 0
            .also { db.close() }
    }

    /**
     * 특정 날짜의 지출 내역을 가져오는 함수
     */
    fun getAllExpensesForUser(date: String): List<Expense> {
        val db = readableDatabase
        val expenses = mutableListOf<Expense>()
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_AMOUNT, COLUMN_DETAIL, COLUMN_CATEGORY, COLUMN_TRANSACTION_TYPE, COLUMN_DATE),
            "$COLUMN_DATE = ?",
            arrayOf(date),
            null, null, "$COLUMN_DATE DESC"
        )

        while (cursor.moveToNext()) {
            expenses.add(
                Expense(
                    cursor.getLong(0),
                    cursor.getDouble(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5)
                )
            )
        }
        cursor.close()
        db.close()
        return expenses
    }

    /**
     * 전체 지출 내역을 가져오는 함수
     */
    fun getAllExpenses(): List<Expense> {
        val db = readableDatabase
        val expenses = mutableListOf<Expense>()
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_AMOUNT, COLUMN_DETAIL, COLUMN_CATEGORY, COLUMN_TRANSACTION_TYPE, COLUMN_DATE),
            null, null, null, null, "$COLUMN_DATE DESC"
        )

        while (cursor.moveToNext()) {
            expenses.add(
                Expense(
                    cursor.getLong(0),
                    cursor.getDouble(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5)
                )
            )
        }
        cursor.close()
        db.close()
        return expenses
    }

    /**
     * 전체 지출 합계를 계산하는 함수
     */
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
