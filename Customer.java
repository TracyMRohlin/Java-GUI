import java.io.IOException;
import java.io.Serializable;
import java.lang.Comparable;
import java.lang.Integer;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.text.NumberFormat;


public class Customer implements Serializable {
    private int interest;
    private String name;
    private String id;    // 3 digits string
    private String pin;    // 4 digits string
    private ArrayList<Account> acct;
    private double total_bal;  // for all accounts
    public static int nextCustomerNumber = 0; // increments every time a customer constructor is called
    final int intId;
    private int transaction_counter;
    static final long serialVersionUID = -3005023205032780691L;
    private StringBuilder builder = new StringBuilder();
    private static NumberFormat formatter = NumberFormat.getCurrencyInstance();


    public Customer(int starting_customer_number, String aPin, String aName) {
        intId = starting_customer_number + nextCustomerNumber;
        id = Integer.toString(intId);
        name = aName;
        pin = aPin;
        nextCustomerNumber++;
        acct = new ArrayList<>();
    }

    public String returnID() {
        return id;
    }

    public String returnPin() {
        return pin;
    }

    public String getName() {
        return name;
    }


    public void addAccount(int starting_account_number, boolean save) {
        // creates an account and adds the account (based on whether
        // it is a savings account or not) to the account array
        // int starting_account_number, String aPin, String aName, String anID)
        if (save) {
            Sav_Acct newAccount = new Sav_Acct(starting_account_number, pin, name, id);
            acct.add(newAccount);

        } else {
            Account newAccount = new Account(starting_account_number, pin, name, id);
            acct.add(newAccount);
        }
    }

    public String returnInfo() {
        // prints the customer name, id and account numbers
        String cus = String.format("<html>Customer Name: %s<br>Customer ID: %s", name, id);
        StringBuilder result = new StringBuilder();
        result.append(cus);

        if (acct.isEmpty()) {
            result.append("<br>You currently have no accounts at this bank</html>");
        } else {
            for (Account a : acct) {
                boolean active = a.checkActive();
                if (active) {
                String account = a.returnNumber();
                double balance = a.returnBalance();
                String acc = String.format("<br>Account #: %s", account);
                result.append(acc);
                String bal = String.format("<br>Balance available: $ %.2f", balance);
                result.append(bal);
            }
            }
            String tag = "</html>";
            result.append(tag);
        }
        return result.toString();
    }

    public double cal_total_bal() {
        for (Account a : acct) {
            total_bal += a.returnBalance();
        }
        return total_bal;
    }

    public Account returnAccount(String number) {
        // checks each account and compares the account numbers in order to return the correct account
        Account account = null;
        for (Account a : acct) {
            String checkNumb = a.returnNumber();
            if (number.equals(checkNumb)) {
                account = a;
            }
        }
        return account;
    }

    public ArrayList<String> getAllAccounts() {
        ArrayList<String> result = new ArrayList<>();
        for (Account a : acct) {
            String checkNumb = a.returnNumber();
            result.add(checkNumb);
        }
        return result;
    }

    public boolean addMoney(double deposit, String acctNumber) {
        // Deposits money into the account
        builder = new StringBuilder(); // resets the String builder just in case it's been used previously
        boolean accountFound = false;
        Account account = returnAccount(acctNumber);
        if (account != null) {
            account.depositMoney(deposit);
            double balance = account.returnBalance();
            String accountString = String.format("<html>Account #: %s", acctNumber);
            String balanceString = String.format("<br>New balance: $ %.2f</html>", balance);
            builder.append(accountString);
            builder.append(balanceString);
            accountFound = true;
        }
        return accountFound;
    }

    public boolean removeMoney(double withdrawal, String acctNumber) {
        // Withdraws money from an account
        builder = new StringBuilder(); // resets the builder in case it's been used
        boolean sufficient_funds = false;
        Account account = returnAccount(acctNumber);
        if (account != null) {
            double oldBalance = account.returnBalance();
            if (oldBalance >= withdrawal) {
                account.withdrawMoney(withdrawal);
                double newBalance = oldBalance - withdrawal;
                String accountString = String.format("<html>Account #: %s", acctNumber);
                String balanceString = String.format("<br>New balance: $ %.2f</html>", newBalance);
                builder.append(accountString);
                builder.append(balanceString);
                sufficient_funds = true;
            } else {
                builder.append("Insufficient funds.");
            }
        }
        return sufficient_funds;
    }

  public String printAccountInfo() {
        // Prints the available balance for an account
        String cusInfo = returnInfo();
        String removedBR = cusInfo.replaceAll("<br>+", "\n");
        String removedHTML = removedBR.replaceAll("<html>|</html>", "");

        StringBuilder result = new StringBuilder();
        result.append(removedHTML);
        double balance = cal_total_bal();
        String availBal = String.format("\nTotal available Balance: $ %.2f", balance);
        result.append(availBal);
        return result.toString();
    }

    public void removeAccount(String acctNumber) {
        Account account = returnAccount(acctNumber);
        if (account != null) {

            account.setBalanceNull();

        }
    }

    public void switchMoney(String from, String to, double amountMoney) {
        // Transfers money from one account to another
        boolean transfer_result = removeMoney(amountMoney, from);
        if (transfer_result) {
            addMoney(amountMoney, to);
        }
    }


    public ArrayList<Tuple> addInterestToSavings(int InterestRate) {
        // Adds interest to savings
        ArrayList<Tuple> savingsAccounts = new ArrayList<>(100);
        for (Account account : acct) {
            if (account instanceof Sav_Acct) {
                Sav_Acct sav = (Sav_Acct) account;
                double balance = sav.returnBalance();
                sav.addInterest_rate(balance, InterestRate);
                // saves the accounts and the balances as a tuple that have been added to so it can return to the log file
                String accountNum = sav.returnNumber();
                Tuple tuple = new Tuple(accountNum, balance);
                savingsAccounts.add(tuple);
            }
        }
        return savingsAccounts;
    }


    public int returnMaxAccount() {
        // returns the max account number to compare to all the other account numbers from other customers
        // so that it can set the "starting account number" to the correct number
        ArrayList<Integer> accounts = new ArrayList<Integer>();
        int maxAccountNumber = 0;
        if (!acct.isEmpty()) {
            for (Account a : acct) {
                String temp = a.returnNumber();
                int accountNumber = Integer.parseInt(temp);
                accounts.add(accountNumber);
            }
            maxAccountNumber = Collections.max(accounts);
        }
        return maxAccountNumber;
    }

    public static Comparator<Customer> CompareName = new Comparator<Customer>() {
        // Sorts the customer names in ascending order
        public int compare(Customer c1, Customer c2) {
            String CustomerName1 = c1.getName().toUpperCase();
            String CustomerName2 = c2.getName().toUpperCase();
            return CustomerName1.compareTo(CustomerName2);
        }
    };

    public static Comparator<Customer> CompareIDs = new Comparator<Customer>() {
        // Sorts the IDs in ascending order
        public int compare(Customer c1, Customer c2) {
            String CustomerID1 = c1.returnID();
            String CustomerID2 = c2.returnID();
            return CustomerID1.compareTo(CustomerID2);
        }
    };

    public ArrayList<Account> getAccounts() {
        // Sorts the accounts based on balance
        Collections.sort(acct, Account.CompareBalances);
        return acct;
    }

    public String transferMessage(String accountFrom, String accountTo, double transferAmount) {
        String transferInDollars = formatter.format(transferAmount);
        String from = String.format("Transferred %s from Account # %s to Account # %s", transferInDollars, accountFrom, accountTo);
        return from;
    }

    public String builderToString() {
        return builder.toString();
    }
}

