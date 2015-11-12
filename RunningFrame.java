
import java.awt.*;
import java.awt.Button;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Override;
import java.lang.Runnable;
import javax.swing.*;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.text.StringContent;
import java.lang.StringBuilder;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;


public class RunningFrame extends JFrame implements ActionListener {
    protected final long serialVersionUID = -3005023205032780691L;
    private String customerID;
    private String customerPIN;
    protected GroupLayout layout;
    protected Container contentPane;
    private static int DEFAULT_WIDTH = 600;
    private static int DEFAULT_HEIGHT = 800;
    private boolean clicked = false;
    private JButton OKButton;
    protected Atm atm_controller;
    private  JTextField IDtext;
    private  JTextField PINtext;
    private static String[] menu_options;
    private static int dropdownChoice = 10;
    private String accountString;
    private String depositString;
    private String from;
    private String to;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
    private static String adminPin = "abcd";
    private static String adminID = "000";
    private ArrayList<String> accountOptions = new ArrayList<String>();

    public RunningFrame(Atm atm) {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocationRelativeTo(null);
        setLayout(new GridLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Welcome to SJSU Banking System");

        contentPane = getContentPane();
        layout = new GroupLayout(contentPane);
        contentPane.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        atm_controller = atm;
    }

    public void startPage() {
        // Creates a start box that has a drop down component as well as text fields for PIN and Customer Number.
        menu_options = new String[]{"Please select an option", "Open Account", "Deposit", "Withdraw", "Transfer",
                "Account Information", "Close Account"};

        JComboBox<String> comboMenu = new JComboBox<>(menu_options);
        comboMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox mo = (JComboBox) e.getSource();
                String choice = (String) mo.getSelectedItem();
                int i = 0;
                for (String option : menu_options) {
                    if (option.equalsIgnoreCase(choice)) {
                        dropdownChoice = i;
                        break;
                    }
                    i++;
                }
            }
        });

        JLabel MenuLabel = new JLabel("Please choose an option: ");
        MenuLabel.setFont(new Font("Futura", Font.PLAIN, 16));
        contentPane.add(MenuLabel);

        JLabel CustomerIDLabel = new JLabel("Customer ID Number: ");
        CustomerIDLabel.setFont(new Font("Futura", Font.PLAIN, 16));
        IDtext = new JTextField("Please enter your Customer ID ", 16);

        JLabel PinLabel = new JLabel("PIN Number: ");
        PinLabel.setFont(new Font("Futura", Font.PLAIN, 16));
        contentPane.add(PinLabel);
        PINtext = new JTextField("Please enter your PIN Number ", 16);
        PINtext.addActionListener(this);
        contentPane.add(PINtext);

        OKButton = new JButton("OK");
        OKButton.addActionListener(this);

        JButton accountButton = new JButton("Create an account");
        accountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                atm_controller.model_create_account();
            }
        });

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(MenuLabel)
                        .addComponent(CustomerIDLabel)
                        .addComponent(PinLabel)
                        .addComponent(accountButton))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(comboMenu)
                        .addComponent(IDtext)
                        .addComponent(PINtext)
                        .addComponent(OKButton)));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(MenuLabel)
                        .addComponent(comboMenu))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(CustomerIDLabel)
                        .addComponent(IDtext))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(PinLabel)
                        .addComponent(PINtext))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(OKButton)
                                .addComponent(accountButton)
                ));
        pack();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        // the action event for the dropdown menu and ok button for the start page
        customerPIN = PINtext.getText();
        customerID = IDtext.getText();
        if (customerID.equals(adminID)) {
            if (customerPIN.equals(adminPin)) {
                dropdownChoice = 0;
            } else {
                errorMessage("Invalid PIN number.");
            }
        }
        performDropDown();
    }

    public void errorMessage(String error){
        // Returns when the account has not been found during deposit/transfer/withdrawal
        JOptionPane.showMessageDialog(this, error, "Error!", JOptionPane.ERROR_MESSAGE);
        atm_controller.callStartPage();
    }

    public void displayInfo(String infoMessage){
        // Shows new dialog box with account balance info when something has been done to the accounts
        JOptionPane.showMessageDialog(this, infoMessage, "Updated Information:", JOptionPane.INFORMATION_MESSAGE);
        atm_controller.callStartPage();
    }

    public void createAccountScreen() {
        // Shows the create account screen
        JLabel nameLabel = new JLabel("Please enter your username:");
        nameLabel.setFont(new Font("Futura", Font.PLAIN, 16));
        contentPane.add(nameLabel);

        JLabel pinLabel = new JLabel("Please enter a four digit pin:");
        pinLabel.setFont(new Font("Futura", Font.PLAIN, 16));
        contentPane.add(pinLabel);

        JTextField nameText = new JTextField();
        JTextField pinText = new JTextField();
        contentPane.add(nameText);
        contentPane.add(pinText);

        OKButton = new JButton("OK");
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cusName = nameText.getText();
                String cusPIN = pinText.getText();
                if (cusPIN.length() == 4) {
                    atm_controller.create_account(cusName, cusPIN);
                } else {
                    errorMessage("PIN number must be 4 digits!");
                }
            }
        });


        layout.setHorizontalGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(nameLabel)
                                .addComponent(pinLabel))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(nameText)
                                .addComponent(pinText)
                                .addComponent(OKButton)));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(nameLabel)
                        .addComponent(nameText))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(pinLabel)
                        .addComponent(pinText))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(OKButton)));
        pack();
        setVisible(true);
    }

    public void performDropDown() {
        if (dropdownChoice == 0) {
            atm_controller.model_admin();
        } else if (dropdownChoice == 1) {
            atm_controller.model_open_account(customerID, customerPIN);
        } else if (dropdownChoice == 2) {
            atm_controller.model_deposit(customerID, customerPIN);
        } else if (dropdownChoice == 3) {
            atm_controller.model_withdraw(customerID, customerPIN);
        } else if (dropdownChoice == 4) {
            atm_controller.model_transfer(customerID, customerPIN);
        } else if (dropdownChoice == 5) {
            atm_controller.model_account_info(customerID, customerPIN);
        } else if (dropdownChoice == 6) {
            atm_controller.model_close_account(customerID, customerPIN);
        } else if (dropdownChoice == 7) {
            System.exit(0);
        } else {
            atm_controller.callStartPage();
        }
    }

    public void depositScreen(String phrase, ArrayList<String> allAccounts) {
        // Shows the screen for deposit function
        JLabel customerInfo = new JLabel(phrase);
        customerInfo.setFont(new Font("Futura", Font.PLAIN, 12));
        contentPane.add(customerInfo);

        JTextField depositAmount = new JTextField("Please enter the amount you would like to deposit in 00.00 format ");
        depositAmount.setFont(new Font("Futura", Font.PLAIN, 12));
        contentPane.add(depositAmount);

        // Creates a drop down box of all the accounts

        boolean hasAccounts = createAccountMenu(allAccounts);
        if (!hasAccounts) {
            atm_controller.callStartPage();
        } else {
            String[] accountOptionsArray = accountOptions.toArray(new String[accountOptions.size()]);

            JComboBox<String> accountsOption = new JComboBox<String>(accountOptionsArray);
            accountsOption.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JComboBox fromMo = (JComboBox) e.getSource();
                    String from = (String) fromMo.getSelectedItem();
                    setAccountOne(from);
                }
            });

            OKButton = new JButton("OK");
            OKButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    depositString = depositAmount.getText();
                    atm_controller.deposit(from, depositString);
                }
            });
            layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(customerInfo)
                            .addComponent(depositAmount)
                            .addComponent(OKButton))
                    .addComponent(accountsOption));

            layout.setVerticalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(customerInfo)
                            .addComponent(accountsOption))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(depositAmount))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(OKButton)));
            pack();
            setVisible(true);
        }
    }

    public void withdrawalScreen(String phrase, ArrayList<String> allAccounts) {
        // Shows the screen for withdrawal function
        JLabel customerInfo = new JLabel(phrase);
        customerInfo.setFont(new Font("Futura", Font.PLAIN, 12));
        contentPane.add(customerInfo);

        JTextField withdrawalAmount = new JTextField("Please enter the amount you would like to withdraw in 00.00 format ");
        withdrawalAmount.setFont(new Font("Futura", Font.PLAIN, 12));
        contentPane.add(withdrawalAmount);

        // Creates a drop down box of all the accounts

        boolean hasAccounts = createAccountMenu(allAccounts);
        if (!hasAccounts) {
            atm_controller.callStartPage();
        } else {
            String[] accountOptionsArray = accountOptions.toArray(new String[accountOptions.size()]);

            JComboBox<String> accountsOption = new JComboBox<String>(accountOptionsArray);
            accountsOption.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JComboBox fromMo = (JComboBox) e.getSource();
                    String from = (String) fromMo.getSelectedItem();
                    setAccountOne(from);
                }
            });

            OKButton = new JButton("OK");
            OKButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String withdrawString = withdrawalAmount.getText();
                    atm_controller.withdraw(withdrawString, from);
                }
            });
            layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(customerInfo)
                            .addComponent(withdrawalAmount)
                            .addComponent(OKButton))
                    .addComponent(accountsOption));

            layout.setVerticalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(customerInfo)
                            .addComponent(accountsOption))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(withdrawalAmount))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(OKButton)));
            pack();
            setVisible(true);
        }
    }

    public void openAccountScreen() {
        // Shows the open account page
        ButtonGroup buttons = new ButtonGroup();

        JLabel text = new JLabel("Please choose account type:");
        text.setFont(new Font("Futura", Font.PLAIN, 16));
        contentPane.add(text);

        JRadioButton savingButton = new JRadioButton("Savings");
        JRadioButton checkingButton = new JRadioButton("Checkings");
        contentPane.add(savingButton);
        contentPane.add(checkingButton);
        buttons.add(savingButton);
        buttons.add(checkingButton);


        OKButton = new JButton("OK");
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean savings = savingButton.isSelected();
                atm_controller.open_account(savings);
            }
        });

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(text))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(savingButton)
                        .addComponent(checkingButton))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(OKButton)));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(text)
                        .addComponent(savingButton)
                        .addComponent(OKButton))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(checkingButton)));


        pack();
        setVisible(true);
    }


    public boolean createAccountMenu(ArrayList<String> accounts) {
        accountOptions.add("Please choose one.");
        boolean hasAccounts = false;
        if (accounts.isEmpty()) {
            errorMessage("You do not have any accounts at this bank.");
        } else {
            hasAccounts = true;
            accountOptions.addAll(accounts);
        } return hasAccounts;
    }


    public void transferScreen(String phrase, ArrayList<String> accounts) {
        // creates the screen so a customer can transfer from one account to another

        if (accounts.size() == 1) {
            errorMessage("You must have two accounts in order to transfer.");;
        } else {

            JLabel customerInfo = new JLabel(phrase);
            customerInfo.setFont(new Font("Futura", Font.PLAIN, 12));
            contentPane.add(customerInfo);

            JLabel fromLabel = new JLabel("From account:");
            customerInfo.setFont(new Font("Futura", Font.PLAIN, 12));
            contentPane.add(fromLabel);

            JLabel toLabel = new JLabel("To account:");
            customerInfo.setFont(new Font("Futura", Font.PLAIN, 12));
            contentPane.add(toLabel);

            JLabel withdrawalLabel = new JLabel("Transfer amount:");
            withdrawalLabel.setFont(new Font("Futura", Font.PLAIN, 12));
            contentPane.add(withdrawalLabel);
            JTextField withdrawalAmount = new JTextField("Please enter the amount you would like to transfer in 00.00 format ");
            withdrawalAmount.setFont(new Font("Futura", Font.PLAIN, 12));
            contentPane.add(withdrawalAmount);

            boolean hasAccounts = createAccountMenu(accounts);
            if (!hasAccounts) {
                atm_controller.callStartPage();
            }
            String[] accountOptionsArray = accountOptions.toArray(new String[accountOptions.size()]);

            JComboBox<String> accountFrom = new JComboBox<String>(accountOptionsArray);
            accountFrom.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JComboBox fromMo = (JComboBox) e.getSource();
                    String from = (String) fromMo.getSelectedItem();
                    setAccountOne(from);
                }
            });


            JComboBox<String> accountTo = new JComboBox<String>(accountOptionsArray);
            accountTo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JComboBox toMO = (JComboBox) e.getSource();
                    String to = (String) toMO.getSelectedItem();
                    setAccountTwo(to);
                }
            });

            OKButton = new JButton("OK");
            OKButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String transferAmount = withdrawalAmount.getText();
                    atm_controller.transfer(transferAmount, from, to);
                }
            });

            layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(customerInfo)
                            .addComponent(withdrawalLabel))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(fromLabel)
                            .addComponent(accountFrom)
                            .addComponent(withdrawalAmount))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(toLabel)
                                    .addComponent(accountTo)
                                    .addComponent(OKButton)
                    ));


            layout.setVerticalGroup(layout.createSequentialGroup()
                    .addComponent(customerInfo)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(withdrawalLabel)
                            .addComponent(withdrawalAmount))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(fromLabel)
                            .addComponent(toLabel))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(accountFrom)
                            .addComponent(accountTo))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(OKButton)));

            pack();
            setVisible(true);
        }
    }


    public void accountInfoScreen(String info, ArrayList<AdminLog> adminLogs, String customerID) {
        // Shows the account information including total balance
        StringBuilder text = new StringBuilder();
        text.append(info);

        String CusID = "[ID #]";
        String Tim = "[Time Stamp]";
        String transID = "[Transaction ID]";
        String CusAcct = "[Account #]";
        String transAmount = "[Amount]";
        String header = String.format("\n\n%-30s %-20s %-10s %10s %10s\n", Tim, transID, CusID, CusAcct, transAmount);
        text.append(header);

        if (!adminLogs.isEmpty()) {
            for (AdminLog a: adminLogs) {
                String cusID = a.getCustomerID();
                if (cusID.equals(customerID)) {
                   long timestamp = a.getTimestamp();
                    String date = dateFormat.format(timestamp);
                    int transactionID = a.getTransactionID();
                    String accountID = a.getAccountNum();
                    double amount = a.getAmount();
                    String newLine = String.format("%-30s %-20d %-10s %10s %10s\n", date, transactionID, cusID, accountID, amount);
                    text.append(newLine);
                    }
                }
            } else {
                text.append("\n\nNo transactions have been recordered yet.");
            }
        String totalInformation = text.toString();

        JTextArea accountInfo = new JTextArea(500,500);
        JScrollPane scrollPane = new JScrollPane(accountInfo);
        accountInfo.setEditable(false);
        accountInfo.append(totalInformation);

        contentPane.add(accountInfo);
        OKButton = new JButton("OK");
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atm_controller.callStartPage();
            }
        });

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(accountInfo)
                .addComponent(OKButton));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(accountInfo))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(OKButton)));
        pack();
        setVisible(true);
    }

    public void closeAccountScreen(ArrayList<String> accounts){
        accountOptions = new ArrayList<String>();
        accountOptions.add("Please choose one.");
        if (accounts.isEmpty()) {
            errorMessage("You do not have any accounts at this bank.");;
        } else {
            for (String acc : accounts) {
                accountOptions.add(acc);
            }
        }

        String[] accountOptionsArray = accountOptions.toArray(new String[accountOptions.size()]);;
        // Sets the dropdown menu for accounts
        JComboBox<String> accountsOps = new JComboBox<String>(accountOptionsArray);
        accountsOps.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox Mo = (JComboBox) e.getSource();
                String account = (String) Mo.getSelectedItem();
                setAccountOne(account);
            }
        });

        JLabel closeLabel = new JLabel("Please select the account you wish to close:");
        closeLabel.setFont(new Font("Futura", Font.PLAIN, 16));
        contentPane.add(closeLabel);



        OKButton = new JButton("OK");
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int reply = askBeforeDeleting();
                if (reply == JOptionPane.YES_OPTION) {
                    String dialogMessage = atm_controller.close_account(from);
                    addedAccount(dialogMessage);
                } else {
                    revertToMainScreen();
                }
            }
        });

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(closeLabel)
                        .addComponent(OKButton))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(accountsOps)));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(closeLabel)
                        .addComponent(accountsOps))
                .addComponent(OKButton));
        pack();
        setVisible(true);
    }

    public int askBeforeDeleting() {
        int reply = JOptionPane.showConfirmDialog(this, "Are you sure? You are about to delete an account!",
                "WARNING!",
                JOptionPane.YES_NO_OPTION);
        return reply;
    }

    public void addedAccount(String dialogMessage) {
        JOptionPane.showMessageDialog(this, dialogMessage, "Updated!", JOptionPane.INFORMATION_MESSAGE);
        atm_controller.callStartPage();
    }

    public void revertToMainScreen() {
        JOptionPane.showMessageDialog(this, "No worries! Taking you back to main screen!");
        atm_controller.callStartPage();
    }

    public void setAccountOne(String a) {
        from = a;
    }

    public void setAccountTwo(String b) {
        to = b;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getCustomerPIN()
    {
        return customerPIN;
    }
}

