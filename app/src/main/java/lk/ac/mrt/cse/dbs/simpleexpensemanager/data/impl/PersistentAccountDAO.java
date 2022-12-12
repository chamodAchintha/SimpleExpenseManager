package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {

    private final DBHelper dbHelper;
    public PersistentAccountDAO(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<Account> accountsList = dbHelper.getAccountsList();
        List<String> accountNumbersList = new ArrayList<>();
        if (accountsList.size()==0)
            return accountNumbersList;
        for (Account account : accountsList){
            accountNumbersList.add(account.getAccountNo());
        }
        return accountNumbersList;
    }

    @Override
    public List<Account> getAccountsList() {
        return dbHelper.getAccountsList();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Account account = dbHelper.getAccount(accountNo);
        if(account==null)
            throw new InvalidAccountException("Invalid account number");
        return account;
    }

    @Override
    public void addAccount(Account account) {
        dbHelper.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if (getAccount(accountNo)==null)
            throw new InvalidAccountException("Invalid account number");
        else
            dbHelper.removeAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Account account = getAccount(accountNo);
        double balance = account.getBalance();
        if (expenseType == ExpenseType.EXPENSE) {
            account.setBalance(balance - amount);
        } else if (expenseType == ExpenseType.INCOME) {
            account.setBalance(balance + amount);
        }
        if (account.getBalance()>0) {
            dbHelper.updateValues(account);
        }
    }
}
