package com.github.poonamcoder.bank_management;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserOnboard {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int choice;
        System.out.println("");
        System.out.println("-----------------------------");
        System.out.println("Welcome to our bank!");
        do {
            System.out.println("");
            System.out.println("1. Create account");
            System.out.println("2. Withdrawal");
            System.out.println("3. Deposit");
            System.out.println("4. Get balance");
            System.out.println("5. Exit");
            System.out.println("");

            choice = Integer.parseInt(reader.readLine());
            Account accountManagement = new Account();
            switch (choice) {
                case 1:
                    accountManagement.createAccount();
                    break;
                case 2:
                    accountManagement.withdrawal();
                    break;
                case 3:
                    accountManagement.deposit();
                    break;
                case 4:
                    accountManagement.checkBalance();
                    break;
                case 5:
                    accountManagement.exit();
                    break;
                default:
                    System.out.println("Please choose from provided options");
            }
        } while (choice != 5);
    }
}
