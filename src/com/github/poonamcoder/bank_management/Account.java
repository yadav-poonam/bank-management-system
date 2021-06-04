package com.github.poonamcoder.bank_management;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

enum TransactionType {
    Cr,
    Dr
}

enum TransactionMode {
    cash,
    online,
    atm
}

public class Account {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static final LinkedHashMap<String, Integer> existingAccountsCache = new LinkedHashMap<>();
    public static LinkedHashMap<Integer, Account> existingAccounts = new LinkedHashMap<>();
    private String userName;
    private Integer accountNumber;
    private Double currentBalance;

    public Account() {
    }

    public Account(String userName, Integer accountNumber, Double currentBalance) {
        this.userName = userName;
        this.accountNumber = accountNumber;
        this.currentBalance = currentBalance;
    }

    private void loadAccountsFromCsv() {
        List<LinkedHashMap<String, String>> currentAccounts = CsvUtil.read("account.csv");
        if (currentAccounts.isEmpty())
            return;
        for (LinkedHashMap<String, String> currentAccount : currentAccounts) {
            existingAccountsCache.putIfAbsent(
                    currentAccount.get("name"),
                    Integer.parseInt(currentAccount.get("account_number")));
            if (existingAccounts.containsKey(Integer.parseInt(currentAccount.get("account_number"))))
                existingAccounts.replace(
                        Integer.parseInt(currentAccount.get("account_number")),
                        new Account(
                                currentAccount.get("name"),
                                Integer.parseInt(currentAccount.get("account_number")),
                                Double.parseDouble(currentAccount.get("balance"))));
            else
                existingAccounts.put(
                        Integer.parseInt(currentAccount.get("account_number")),
                        new Account(
                                currentAccount.get("name"),
                                Integer.parseInt(currentAccount.get("account_number")),
                                Double.parseDouble(currentAccount.get("balance"))));

        }
    }

    private void writeAccountsToCsv() {
        List<LinkedHashMap<String, String>> allAccounts = new ArrayList<>();
        for (Account account : existingAccounts.values()) {
            LinkedHashMap<String, String> accountMap = new LinkedHashMap<>();
            accountMap.put("name", account.userName);
            accountMap.put("account_number", account.accountNumber.toString());
            accountMap.put("balance", account.currentBalance.toString());
            allAccounts.add(accountMap);
        }
        CsvUtil.write("account.csv", allAccounts, false);
    }

    public String getCurrentDateTime() {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(dateTimeFormatter);
    }

    public void writeAccountStatementToCsv(TransactionType transactionType, Integer accountNumber, Double txnAmount, TransactionMode transactionMode) {
        List<LinkedHashMap<String, String>> accountStatement = new ArrayList<>();
        LinkedHashMap<String, String> statement = new LinkedHashMap<>();
        statement.put("date", getCurrentDateTime());
        statement.put("txn_type", transactionType.name());
        statement.put("account_number", accountNumber.toString());
        statement.put("txn_amount", txnAmount.toString());
        statement.put("balance", existingAccounts.get(accountNumber).currentBalance.toString());
        statement.put("mode", transactionMode.name());
        accountStatement.add(statement);
        CsvUtil.write("account_statement.csv", accountStatement, true);
    }

    public void createAccount() throws IOException {
        loadAccountsFromCsv();
        System.out.println("Enter desired account number");
        this.accountNumber = Integer.parseInt(reader.readLine());
        System.out.println("Enter desired user name");
        this.userName = reader.readLine();
        if (existingAccountsCache.containsKey(userName) || existingAccountsCache.containsValue(accountNumber)) {
            System.out.println("Account with this account number or user name already exists");
            return;
        }
        this.currentBalance = 0D;
        existingAccounts.put(this.accountNumber, this);
        writeAccountsToCsv();
        loadAccountsFromCsv();
        System.out.println("Hi, " + this.userName + " your account has been created successfully");
    }

    public void withdrawal() throws IOException {
        loadAccountsFromCsv();
        System.out.println("Enter account number: ");
        Integer accountNumber = Integer.parseInt(reader.readLine());
        if (!existingAccounts.containsKey(accountNumber)) {
            System.out.println("Account number does not exist");
            return;
        }
        System.out.println("Enter mode of withdrawal (cash/online/atm) : ");
        String txnMode = reader.readLine();
        TransactionMode transactionMode;
        try {
            transactionMode = TransactionMode.valueOf(txnMode);
        } catch (Exception e) {
            System.out.println("Invalid transaction Mode selected");
            return;
        }
        TransactionType transactionType = TransactionType.Dr;
        System.out.println("Enter amount to withdraw: ");
        Double withdrawAmount = Double.parseDouble(reader.readLine());
        if (withdrawAmount < 0D || withdrawAmount > existingAccounts.get(accountNumber).currentBalance) {
            System.out.println("Invalid withdrawal amount");
            return;
        }
        existingAccounts.get(accountNumber).currentBalance = existingAccounts.get(accountNumber).currentBalance - withdrawAmount;
        writeAccountsToCsv();
        loadAccountsFromCsv();
        writeAccountStatementToCsv(transactionType, accountNumber, withdrawAmount, transactionMode);
        System.out.println("Transaction successful");
    }

    public void deposit() throws IOException {
        loadAccountsFromCsv();
        System.out.println("Enter account number: ");
        Integer accountNumber = Integer.parseInt(reader.readLine());
        if (!existingAccounts.containsKey(accountNumber)) {
            System.out.println("Account number does not exist");
            return;
        }
        System.out.println("Enter mode of deposit (cash/online) : ");
        String txnMode = reader.readLine();
        TransactionMode transactionMode;
        try {
            transactionMode = TransactionMode.valueOf(txnMode);
        } catch (Exception e) {
            System.out.println("Invalid transaction Mode selected");
            return;
        }
        if (transactionMode.equals(TransactionMode.atm)) {
            System.out.println("Invalid transaction Mode selected");
            return;
        }
        TransactionType transactionType = TransactionType.Cr;
        System.out.println("Enter amount to deposit: ");
        Double depositAmount = Double.parseDouble(reader.readLine());
        if (depositAmount < 0D) {
            System.out.println("Invalid deposit amount");
            return;
        }
        existingAccounts.get(accountNumber).currentBalance = existingAccounts.get(accountNumber).currentBalance + depositAmount;
        writeAccountsToCsv();
        loadAccountsFromCsv();
        writeAccountStatementToCsv(transactionType, accountNumber, depositAmount, transactionMode);
        System.out.println("Transaction successful");
    }

    public void checkBalance() throws IOException {
        loadAccountsFromCsv();
        System.out.println("Enter account number: ");
        Integer accountNumber = Integer.parseInt(reader.readLine());
        if (!existingAccounts.containsKey(accountNumber)) {
            System.out.println("Account number does not exist");
            return;
        }
        System.out.println("Your current balance is : " + existingAccounts.get(accountNumber).currentBalance);
    }

    public void exit() {
        System.out.println("Thank you for visiting us!");
    }
}
