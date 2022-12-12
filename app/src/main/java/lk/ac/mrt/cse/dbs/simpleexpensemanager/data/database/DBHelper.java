package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        SQLiteDatabase sqLiteDB = getWritableDatabase();
    }

    private static final String DATABASE_NAME = "200346T";
    private static final String ACCOUNT_TABLE = "account";
    private static final String TRANSACTION_TABLE = "transactions";
    private static final int VERSION = 2;
    @SuppressLint("SimpleDateFormat")
    DateFormat format = new SimpleDateFormat("dd-MM-yyyy");




    @Override
    public void onCreate(SQLiteDatabase db) {

        String createAccountTable = "CREATE TABLE account (accountNumber TEXT(50) PRIMARY KEY, bankName TEXT(50), accountHolderName TEXT(50),balance DECIMAL(12,2));";
        String createTransactionTable = "CREATE TABLE transactions (accountNumber TEXT(50), date TEXT(20), transactionType TEXT(50), amount DECIMAL(12,2), FOREIGN KEY (accountNumber) REFERENCES account(accountNumber));";
        db.execSQL(createAccountTable);
        db.execSQL(createTransactionTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        String dropAccountTable = "DROP TABLE IF EXISTS account;";
        String dropTransactionTable = "DROP TABLE IF EXISTS transactions;";
        db.execSQL(dropAccountTable);
        db.execSQL(dropTransactionTable);
        onCreate(db);
    }

    public void addAccount( Account account) {
        SQLiteDatabase sqLiteDB = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNumber", account.getAccountNo());
        contentValues.put("bankName", account.getBankName());
        contentValues.put("accountHolderName", account.getAccountHolderName());
        contentValues.put("balance", account.getBalance());
        sqLiteDB.insert(ACCOUNT_TABLE, null, contentValues);
        sqLiteDB.close();
    }

    public List<Account> getAccountsList() {
        SQLiteDatabase sqLiteDB = getReadableDatabase();
        List<Account> accountList = new ArrayList<>();
        String getAccountQuery = "SELECT * FROM account";

        Cursor cursor = sqLiteDB.rawQuery(getAccountQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Account account = new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3));
                accountList.add(account);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDB.close();
        return accountList;
    }

    public void removeAccount(String accountNo) {
        SQLiteDatabase sqLiteDB = getWritableDatabase();
        sqLiteDB.delete(ACCOUNT_TABLE, " accountNumber = ?", new String[]{accountNo});
        sqLiteDB.close();
    }

    public Account getAccount(String accountNumber) {
        SQLiteDatabase sqLiteDB = getReadableDatabase();
        Account account = null;
        Cursor cursor = sqLiteDB.query(ACCOUNT_TABLE,null, " accountNumber = ?",new String[]{accountNumber},null,null,null,null);
        if (cursor.moveToFirst()){
            account = new Account(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3));
            cursor.close();
        }
        sqLiteDB.close();
        return account;
    }

    public List<Transaction> getAllTransactions() {
        SQLiteDatabase sqLiteDB = getReadableDatabase();
        List<Transaction> transactionsList = new ArrayList<>();
        String getTransactionsQuery = "SELECT * FROM transactions";

        Cursor cursor = sqLiteDB.rawQuery(getTransactionsQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = new Transaction(format.parse(cursor.getString(1)), cursor.getString(0), ExpenseType.valueOf(cursor.getString(2)), cursor.getDouble(3));
                    transactionsList.add(transaction);

                }while (cursor.moveToNext());
            }
        }catch (ParseException ignored){}
        cursor.close();
        sqLiteDB.close();
        return transactionsList;
    }

    public List<Transaction> getPaginatedTransactions(int limit) {
        SQLiteDatabase sqLiteDB = getReadableDatabase();
        List<Transaction> transactionsList = new ArrayList<>();
        String getTransactionsQuery = "SELECT * FROM transactions LIMIT "+limit;

        Cursor cursor = sqLiteDB.rawQuery(getTransactionsQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = new Transaction(format.parse(cursor.getString(1)), cursor.getString(0), ExpenseType.valueOf(cursor.getString(2)), cursor.getDouble(3));
                    transactionsList.add(transaction);
                }while (cursor.moveToNext());
            }
        }catch (ParseException ignored){}
        cursor.close();
        sqLiteDB.close();
        return transactionsList;
    }

    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase sqLiteDB = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNumber", accountNo);
        contentValues.put("date" , format.format(date));
        contentValues.put("transactionType", expenseType.toString());
        contentValues.put("amount", amount);
        sqLiteDB.insert(TRANSACTION_TABLE,null,contentValues);
        sqLiteDB.close();
    }

    public void updateValues(Account account) {
        SQLiteDatabase sqLiteDB = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNumber", account.getAccountNo());
        contentValues.put("bankName", account.getBankName());
        contentValues.put("accountHolderName", account.getAccountHolderName());
        contentValues.put("balance", account.getBalance());
        sqLiteDB.update(ACCOUNT_TABLE, contentValues, " accountNumber = ?",new String[]{account.getAccountNo()});
        sqLiteDB.close();
    }
}
