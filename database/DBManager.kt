package com.example.financialledgerapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "expenses.db"  // 데이터베이스 이름
        private const val DATABASE_VERSION = 2           // 데이터베이스 버전
        private const val TABLE_NAME = "expenses"        // 테이블 이름
        private const val COLUMN_ID = "id"              // ID 컬럼
        private const val COLUMN_AMOUNT = "amount"      // 금액 컬럼
        private const val COLUMN_DETAIL = "detail"      // 내역 컬럼
        private const val COLUMN_TRANSACTION_TYPE = "transaction_type"  // 거래 유형 컬럼 추가
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_AMOUNT REAL,
                $COLUMN_DETAIL TEXT,
                $COLUMN_TRANSACTION_TYPE TEXT  
            )
        """
        db.execSQL(createTableQuery)  // 테이블 생성
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // 기존 테이블 삭제
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")

            // 새 테이블 생성
            onCreate(db)
        }
    }

    fun addExpense(amount: Double, detail: String, transactionType: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_DETAIL, detail)
            put(COLUMN_TRANSACTION_TYPE, transactionType)  // 거래 유형 추가
        }
        db.insert(TABLE_NAME, null, values)  // 데이터를 테이블에 삽입
        db.close()
    }

    fun getAllExpenses(): List<Expense> {
        val db = readableDatabase
        val expenses = mutableListOf<Expense>()

        // 모든 컬럼을 포함하는 쿼리
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_AMOUNT, COLUMN_DETAIL, COLUMN_TRANSACTION_TYPE), // 컬럼들을 모두 나열
            null,
            null,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                // 컬럼 인덱스를 안전하게 가져오기 전에 존재하는지 확인
                val idIndex = cursor.getColumnIndex(COLUMN_ID)
                val amountIndex = cursor.getColumnIndex(COLUMN_AMOUNT)
                val detailIndex = cursor.getColumnIndex(COLUMN_DETAIL)
                val transactionTypeIndex = cursor.getColumnIndex(COLUMN_TRANSACTION_TYPE)

                // 컬럼 인덱스가 유효한지 확인
                if (idIndex >= 0 && amountIndex >= 0 && detailIndex >= 0 && transactionTypeIndex >= 0) {
                    val id = cursor.getLong(idIndex)
                    val amount = cursor.getDouble(amountIndex)
                    val detail = cursor.getString(detailIndex)
                    val transactionType = cursor.getString(transactionTypeIndex)

                    // Expense 객체에 추가
                    expenses.add(Expense(id, amount, detail, transactionType))
                } else {
                    // 컬럼 인덱스가 잘못된 경우 처리 (예: 오류 로그 출력)
                    Log.e("DBManager", "컬럼 인덱스 오류: 일부 컬럼이 존재하지 않음")
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return expenses
    }


    fun updateExpense(id: Long, amount: Double, detail: String, transactionType: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_DETAIL, detail)
            put(COLUMN_TRANSACTION_TYPE, transactionType)  // 거래 유형 수정
        }
        db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))  // 특정 ID의 항목을 수정
        db.close()
    }

    fun deleteExpense(id: Long) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))  // 특정 ID의 항목을 삭제
        db.close()
    }
}
