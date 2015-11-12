

import java.io.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.*;
import java.lang.ClassNotFoundException;
import java.lang.Double;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.lang.Integer;
import java.lang.String;
import java.lang.StringBuffer;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.lang.reflect.Array;
import java.util.*;
import java.util.ArrayList;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import java.awt.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;



public class Atm extends JFrame implements Serializable {
    private ArrayList<Customer> cust;
    private  ArrayList<AdminLog> adminLogs = new ArrayList<AdminLog>(100);
    private int starting_account_number = 1001;
    private int starting_customer_number = 101;
    private String admin_pin;
    private int interest_rate;
    private int transaction_counter = 0;
    private String id;
    private RunningFrame jf = null;
    private AdminRunningFrame ajf = null;
    private String pin;
    private boolean found = false;
    private String depositAmount;
    private Customer customer = null;
    private long timestamp;
    private static ArrayList<Integer> allIDs;
    private static Date date = new Date();


    private NumberFormat formatter = NumberFormat.getCurrencyInstance();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
    private File DBfile = new File("p2.dat");
    private File logFile = new File("p2.log");
    static final long serialVersionUID = -3005023205032780691L;

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 200;



    ConsoleReader console = new ConsoleReader(System.in);


    public Atm()  // constructor
    // sets the customer array to that found in the file if the file exists
    {
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (logFile.exists()) {
            try {
                adminLogs = returnLog("p2.log");
                if (!adminLogs.isEmpty()) {
                    // there may be customer data but perhaps no transactions yet.
                    ArrayList<Integer> allTransactions = new ArrayList<>(100);
                    for (AdminLog a : adminLogs) {

                        int transaction = a.getTransactionID();;
                        allTransactions.add(transaction);
                    }
                    transaction_counter = Collections.max(allTransactions) + 1;
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }

        if (DBfile.exists()) {
            try {

                cust = returnSavedData("p2.dat");

                // sets the starting ID to the max of all the IDs stored in the file.
                allIDs = new ArrayList<Integer>(100);
                ArrayList<Integer> allAccounts = new ArrayList<>();
                for (Customer customer : cust) {
                    String idString = customer.returnID();
                    int id = Integer.parseInt(idString);
                    allIDs.add(id);
                    int accountNumber = customer.returnMaxAccount();
                    allAccounts.add(accountNumber);
                }
                // checks to see if the customer has even made any accounts, if not, max accounts will be set at 1001
                if (starting_account_number != 1) {
                    starting_account_number = Collections.max(allAccounts) + 1;
                }
                starting_customer_number = Collections.max(allIDs) + 1;

            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        } else {
            cust = new ArrayList<>(100);
            // creates the file if it does not exist
            try {
                saveFile(cust, DBfile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        interest_rate = 5;
    }

    public void model_admin() {
        if (ajf != null) {
            ajf.dispose();
        }
        if (jf != null) {
            jf.dispose();
        }
        ajf = new AdminRunningFrame(this);
        ajf.startPage();
        if (DBfile.exists()) {
            try {
                cust = returnSavedData("p2.dat");
                if (cust.isEmpty()) {
                    ajf.errorMessage("There are no customers in this database.");
                }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            }
        }
        if (logFile.exists()) {
            try {
                adminLogs = returnLog("p2.log");
                if (adminLogs.isEmpty()) {
                    ajf.errorMessage("No entries have been found!");
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void printByCustomerName() {
        // Prints the database info by customer's name in ascending order
        Collections.sort(cust, Customer.CompareName);
        ajf.dispose();
        ajf = new AdminRunningFrame(this);

        StringBuilder stringResults = new StringBuilder();
        String header = header();
        stringResults.append(header);

        if (!cust.isEmpty()) {
            for (Customer customer : cust) {
                String id = customer.returnID();
                String name = customer.getName().toUpperCase();
                String pin = customer.returnPin();
                ArrayList<Account> accounts = customer.getAccounts();
                if (!accounts.isEmpty()) {
                    for (Account account : accounts) {
                        if (account.checkActive()) {
                            String accountNumber = account.returnNumber();
                            double balance = account.returnBalance();
                            String balanceAsString = formatter.format(balance);
                            String customerInfo = printAdminInfo(name, id, accountNumber, pin, balanceAsString);
                            stringResults.append(customerInfo);
                        }
                    }

                } else {
                    // Still prints customers who have created customer accounts but not set up any checking/savings
                    String noAccounts = String.format("%-20s %-9s %-10s\n", name, id, pin);
                    stringResults.append(noAccounts);
                }
            }
        }
        String resultsAsString = stringResults.toString();
        ajf.printInfo(resultsAsString);
    }

    public void printHighestBalance() {
        // prints the accounts from highest amount to lowest, with those who have empty accounts last
        ajf.dispose();
        ajf = new AdminRunningFrame(this);

        StringBuilder stringResults = new StringBuilder();
        String header = header();
        stringResults.append(header);

        if (!cust.isEmpty()) {
            ArrayList<Account> allAccounts = new ArrayList<Account>();
            ArrayList<Customer> CustomersNoAccounts = new ArrayList<Customer>();
            for (Customer c : cust) {
                ArrayList<Account> accounts = c.getAccounts();
                if (!accounts.isEmpty()) {
                    // Concatenates all the accounts together for easy sorting
                    allAccounts.addAll(accounts);
                }
                // Adds customers without accounts to a separate list to be printed at the end of all the others
                else {
                    CustomersNoAccounts.add(c);
                }
            }
            if (!allAccounts.isEmpty()) {
                Collections.sort(allAccounts, Account.CompareBalances);
                for (Account a : allAccounts) {
                    if (a.checkActive()) {
                        String id = a.getID();
                        String name = a.getName().toUpperCase();
                        String pin = a.getPin();
                        String accountNumber = a.returnNumber();
                        double balance = a.returnBalance();
                        String balanceAsString = formatter.format(balance);
                        String customerInfo = printAdminInfo(name, id, accountNumber, pin, balanceAsString);
                        stringResults.append(customerInfo);
                    }
                }
            }
            if (!CustomersNoAccounts.isEmpty()) {
                Collections.sort(CustomersNoAccounts, Customer.CompareName);
                for (Customer c : CustomersNoAccounts) {
                    String id = c.returnID();
                    String name = c.getName().toUpperCase();
                    String pin = c.returnPin();
                    String noAccounts = String.format("%-20s %-9s %-10s\n", name, id, pin);
                    stringResults.append(noAccounts);
                }
            }
            String resultsAsString = stringResults.toString();
            ajf.printInfo(resultsAsString);
        }
    }

    public void model_printAllAccounts() {
        ArrayList<String> cusIDs = new ArrayList<String>(100);
        cusIDs.add("Accounts:");
        Collections.sort(cust, Customer.CompareIDs);
        for (Customer c: cust) {
            String id = c.returnID();
            cusIDs.add(id);
        }
        if (ajf != null) {
            ajf.dispose();
        }
        ajf = new AdminRunningFrame(this);
        ajf.showOneCustomerScreen(cusIDs);
    }

    public void printAllAccounts(String customerID) {
        // Sorts the customer based on ID, then prints the accounts information

        // change the withdrawal funcitons and deposit functions too
        ajf.dispose();
        ajf = new AdminRunningFrame(this);

        Collections.sort(cust, Customer.CompareIDs);
        String searchString = ajf.getCustomerID();

        StringBuilder stringResults = new StringBuilder();
        String header = header();
        stringResults.append(header);
        for (Customer customer : cust) {
            String id = customer.returnID();
            String name = customer.getName().toUpperCase();
            String pin = customer.returnPin();
            if (searchString.equals(id)) {
                ArrayList<Account> accounts = customer.getAccounts();
                if (!accounts.isEmpty()) {
                    for (Account account : accounts) {
                        if (account.checkActive()) {
                            String accountNumber = account.returnNumber();
                            double balance = account.returnBalance();
                            String balanceAsString = formatter.format(balance);
                            String customerInfo = printAdminInfo(name, id, accountNumber, pin, balanceAsString);
                            stringResults.append(customerInfo);
                        }
                    }
                }
            }
        }
        String resultsAsString = stringResults.toString();
        ajf.printInfo(resultsAsString);
    }

    public void printTimeStamp() {
        ajf.dispose();
        ajf = new AdminRunningFrame(this);

        StringBuilder stringResults = new StringBuilder();
        // creates a new header
        String Tim = "[Time Stamp]";
        String transID = "[Transaction ID]";
        String CusID = "[ID #]";
        String CusAcct = "[Account #]";
        String transAmount = "[Amount]";
        String header = String.format("%-30s %-20s %-10s %10s %10s\n", Tim, transID, CusID, CusAcct, transAmount);
        stringResults.append(header);

        if (cust.isEmpty()) {
            ajf.errorMessage("There are no customers in this database."); }

            if (adminLogs.isEmpty()) {

                ajf.errorMessage("No entries have been found!");
            } else {
                Collections.sort(adminLogs, AdminLog.CompareTimeStamp);
                for (AdminLog a: adminLogs) {
                    long timestamp = a.getTimestamp();
                    String date = dateFormat.format(timestamp);
                    int transactionID = a.getTransactionID();
                    String customerID = a.getCustomerID();
                    String accountID = a.getAccountNum();
                    double amount = a.getAmount();
                    String newLine = String.format("%-30s %-20d %-10s %10s %10s\n", date, transactionID, customerID, accountID, amount);
                    stringResults.append(newLine);
                }
                String resultsAsString = stringResults.toString();
                ajf.printInfo(resultsAsString);

            }
    }

    public String header() {
        // Prints the header

        String CusName = "[Customer Name]";
        String CusID = "[ID #]";
        String CusAcct = "[Account #]";
        String CusPin = "[Pin #]";
        String CusBal = "[Current Balance]";
        String result = String.format("%-20s %-20s %-10s %10s %18s\n", CusName, CusID, CusPin, CusAcct, CusBal);
        return result;
    }

    public String printAdminInfo(String name, String id, String accountNumber, String pin, String balanceAsString) {
        // Prints the data from each account
        String result = String.format("%-20s %-20s %-10s %10s %18s\n", name, id, pin, accountNumber, balanceAsString);
        return result;
        }



    @SuppressWarnings("unchecked")
    public static ArrayList<Customer> returnSavedData(String filename) throws IOException, ClassNotFoundException {
        ArrayList<Customer> obj = new ArrayList<Customer>();
        FileInputStream fileinput = new FileInputStream(filename);
        ObjectInputStream inObj = new ObjectInputStream(fileinput);
        obj = (ArrayList) inObj.readObject();
        inObj.close();
        fileinput.close();
        return obj;
    }
    @SuppressWarnings("unchecked")
    public static ArrayList<AdminLog> returnLog(String filename) throws IOException, ClassNotFoundException {
        ArrayList<AdminLog> obj = new ArrayList<AdminLog>();
        FileInputStream fileinput = new FileInputStream(filename);
        ObjectInputStream inObj = new ObjectInputStream(fileinput);
        obj = (ArrayList) inObj.readObject();
        inObj.close();
        fileinput.close();
        return obj;
    }

    public void saveFile(ArrayList<? extends Serializable> objectArray, File fileName)throws IOException {
        if (!fileName.exists()) {
            fileName.createNewFile();
        }

        FileOutputStream fileoutput = new FileOutputStream(fileName);
        ObjectOutputStream output = new ObjectOutputStream(fileoutput);
        output.writeObject(objectArray);
        output.close();
        fileoutput.close();

    }

    private void validate_info(String newUserID, String newPin) {
        // validates that the customer has put in the correct Customer name and PIN
        if (newPin == null || newPin.length() != 4) {
            // returns the Customer object right away if the pin number is wrong, which will cause
            // the functions defined below to just pass through without doing anything
            pin = newPin;
        } else {
            // Validate user information
            for (Customer c : cust) {
                // Checks to see if the customer is already in the database
                String possibleCustID = c.returnID();

                if (possibleCustID.equals(newUserID)) {
                    found = true;
                    String possibleCustPIN = c.returnPin(); // Sets possible customer PIN for validation
                    if (possibleCustPIN.equals(newPin)) {
                        customer = c;
                        pin = newPin;
                    }
                    pin = newPin; // so if customer ends up being null but the pin still has value, then the pin was incorrect
                }
            }
        }

        if (!found) {  // calls the correct warning message if  either the customer or pin is not correct
            if (pin == null) {
                jf.errorMessage("That customer is not in our database.");
            } else {
                jf.errorMessage("Invalid PIN number."); // if the customer has not been found but the pin has been set that means the pin was invalid
            }
        }
    }


    public void model_create_account() {
        jf.dispose();
        jf = new RunningFrame(this);
        jf.createAccountScreen();
    }

    public void create_account(String name, String newPin){
        // Actually creates the customer account
        Customer customer = new Customer(starting_customer_number, newPin, name);
        cust.add(customer);
        String newCusID = customer.returnID();
        String idString = String.format("Your new Customer ID is: %s\n", newCusID);
        transaction_counter++;
        add_interest();
        try {
            saveFile(cust, DBfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        jf.displayInfo(idString);
    }

    public void model_open_account(String customerID, String customerPIN) {
        // calls the openAccountScreen function in RunningFrame class
        validate_info(customerID, customerPIN);
        if (customer != null) {
            jf.dispose();
            jf = new RunningFrame(this);
            jf.openAccountScreen();
        }
    }

    public void open_account(boolean save){
        // actually opens the account
        customer.addAccount(starting_account_number, save); // addAccount will create a new account based on whether it is savings/checkings

        StringBuilder text = new StringBuilder();
        text.append("<html>Account created!<br>");
        String result = customer.returnInfo();
        String subResult = result.substring(6);
        text.append(subResult);
        String infoText = text.toString();
        jf.displayInfo(infoText);

        transaction_counter++;
        add_interest();
        try {
            saveFile(cust, DBfile);
            saveFile(adminLogs, logFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        customer = null;  // resets the customer and account after each transaction
        pin = null;
    }

    public void model_deposit(String customerID, String customerPIN) {
        // calls the depositScreen from the RunningFrame class
        validate_info(customerID, customerPIN);
        if (customer != null) {
                jf.dispose();
                jf = new RunningFrame(this);
                String result = customer.returnInfo();
                ArrayList<String> allAccounts = customer.getAllAccounts();
                jf.depositScreen(result, allAccounts);
            }
        }

    public void deposit(String accountNum, String depositAmount) {
        // actually deposits the appropriate amount into the account

        double deposit = Double.parseDouble(depositAmount); // the balance in the Account class is saved as a double so it must be converted here
        boolean accountFound = customer.addMoney(deposit, accountNum);

        if (accountFound == true) {
            // create the log file
            timestamp = date.getTime();
            String customerID = customer.returnID();
            AdminLog newTransaction = new AdminLog(timestamp, transaction_counter, customerID, accountNum, deposit);
            adminLogs.add(newTransaction);

            transaction_counter++;
            add_interest();
            String infoMessage = customer.builderToString();
            jf.displayInfo(infoMessage);
            try {
                saveFile(cust, DBfile);
                saveFile(adminLogs, logFile);
            } catch (IOException e) {
                e.printStackTrace();
            }} else {
                jf.errorMessage("We cannot find that account number in our database");  // pops up an error message if the account was not found
            }
        customer = null;  // resets the customer and account after each transaction
        pin = null;
    }

    public void model_withdraw(String customerID, String customerPIN) {
        // calls the withdrawal screen from the RunningFrame class
        validate_info(customerID, customerPIN);
        if (customer != null) {
            jf.dispose();
            jf = new RunningFrame(this);
            String result = customer.returnInfo();
            ArrayList<String> allAccounts = customer.getAllAccounts();
            jf.withdrawalScreen(result, allAccounts);
            }
        }

    public void withdraw(String withdrawAmount, String accountNum) {
        // actually performs the withdrawal service
        double withdrawal = Double.parseDouble(withdrawAmount);
        boolean accountFound = customer.removeMoney(withdrawal, accountNum);
        if (accountFound == true) {
            // creates the log file
            double logAmount = -withdrawal;
            timestamp = date.getTime();
            String customerID = customer.returnID();
            AdminLog newTransaction = new AdminLog(timestamp, transaction_counter, customerID, accountNum, logAmount);
            adminLogs.add(newTransaction);

            transaction_counter++;
            add_interest();
            String infoMessage = customer.builderToString();
            jf.displayInfo(infoMessage);

            try {
                saveFile(cust, DBfile);
                saveFile(adminLogs, logFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            jf.errorMessage("We cannot find that account number in our database");  // pops up an error message if the account was not found
            }
        customer = null;  // resets the customer and account after each transaction
        pin = null;
    }

    public void model_transfer(String customerID, String customerPIN) {
        // calls the transfer screen from the RunningFrame class
        validate_info(customerID, customerPIN);
        if (customer != null) {
            jf.dispose();
            jf = new RunningFrame(this);
            String result = customer.returnInfo();
            ArrayList<String> allAccounts = customer.getAllAccounts();
            jf.transferScreen(result, allAccounts);
        }
    }

    public void transfer(String transferAmount, String accountFrom, String accountTo) {
        // actually transfers the model
        double transferDouble = Double.parseDouble(transferAmount);
        boolean accountFromFound = customer.removeMoney(transferDouble, accountFrom);
        boolean accountToFound = customer.addMoney(transferDouble, accountTo);
        if (accountFromFound == true && accountToFound == true) {
            // creates the log file
            double logAmount = -transferDouble;
            timestamp = date.getTime();
            String customerID = customer.returnID();

            AdminLog firstTransaction = new AdminLog(timestamp, transaction_counter, customerID, accountFrom, transferDouble);

            timestamp = date.getTime();
            AdminLog secondTransaction = new AdminLog(timestamp, transaction_counter, customerID, accountTo, logAmount);
            adminLogs.add(firstTransaction);
            adminLogs.add(secondTransaction);

            for (AdminLog a : adminLogs) {
                long timestampe = a.getTimestamp();
            }

            transaction_counter++;
            add_interest();
            String infoMessage = customer.transferMessage(accountFrom, accountTo, transferDouble);
            jf.displayInfo(infoMessage);
            try {
                saveFile(cust, DBfile);
                saveFile(adminLogs, logFile);
            } catch (IOException e) {
                e.printStackTrace();
            }} else {
            jf.errorMessage("We cannot find that account number in our database");  // pops up an error message if the account was not found
        }
        customer = null;  // resets the customer and account after each transaction
        pin = null;
    }

    public void model_account_info(String customerID, String customerPIN) {
        // calls the transfer screen from the RunningFrame class
        validate_info(customerID, customerPIN);
        if (customer != null) {
            jf.dispose();
            jf = new RunningFrame(this);
            String text = customer.printAccountInfo();
            jf.accountInfoScreen(text, adminLogs, customerID);
        }
    }

    public void model_close_account(String customerID, String customerPIN) {
        // calls the transfer screen from the RunningFrame class
        validate_info(customerID, customerPIN);
        if (customer != null) {
            jf.dispose();
            jf = new RunningFrame(this);
            ArrayList<String> allAccounts = customer.getAllAccounts();
            jf.closeAccountScreen(allAccounts);
        }
    }

    public String close_account(String accountNum) {
        // actually closes the customer's account and returns a dialog box
        customer.removeAccount(accountNum);
        String cusInfo = customer.returnInfo();

        StringBuilder result = new StringBuilder();
        result.append("<html>Deleted account!<br>Updated information:<br>");
        String subCusInfo = cusInfo.substring(7, cusInfo.length()); // removes the last html tag from returnInfo()
        result.append(subCusInfo);
        try {
            saveFile(cust, DBfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        customer = null;  // resets the customer and account after each transaction
        pin = null;
        return result.toString();
    }

    public void add_interest() {
        if ((transaction_counter % 5) == 0) {
            for (Customer c : cust) {
                ArrayList<Tuple> accountsAddedTo = new ArrayList<>(100);
                accountsAddedTo = c.addInterestToSavings(interest_rate);
                timestamp = date.getTime();
                if (!accountsAddedTo.isEmpty()) {
                    String customerID = c.returnID();
                    for (Tuple tuple : accountsAddedTo) {
                        String accountNumber = tuple.getFirst();
                        double logAmount = tuple.getSecond();
                        AdminLog newTransaction = new AdminLog(timestamp, transaction_counter, customerID, accountNumber, logAmount);
                        adminLogs.add(newTransaction);
                    }
                }
                try {
                    saveFile(cust, DBfile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void callStartPage() {
        customer = null;
        pin = null;
        if (ajf != null) {
            ajf.dispose();
        }
        if (jf != null){
            jf.dispose();
        }
        jf = new RunningFrame(this);
        jf.startPage();
    }

}



