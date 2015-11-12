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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.StringContent;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class AdminRunningFrame extends RunningFrame implements ActionListener {
    static final long serialVersionUID = -3005023205032780691L;
    static int dropdownChoice;
    static JButton OKButton;
    private static String[] new_menu_options;
    private static String customerID;

    public AdminRunningFrame(Atm atm_controller) {
        super(atm_controller);
    }

    public void startPage() {
        // Creates a start box that has a drop down component as well as text fields for PIN and Customer Number.
        new_menu_options = new String[]{"Please select an option", "Print by customer name",
                "Print by highest balance to lowest",
                "Print all the accounts for one customer", "Print by timestamp", "Exit the admin program"};

        JComboBox<String> comboMenu = new JComboBox<>(new_menu_options);
        comboMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox mo = (JComboBox) e.getSource();
                String choice = (String) mo.getSelectedItem();
                int i = 0;
                for (String option : new_menu_options) {
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

        OKButton = new JButton("OK");
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performDropDown();
            }
        });

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(MenuLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(comboMenu)
                        .addComponent(OKButton)));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(MenuLabel)
                        .addComponent(comboMenu))
                .addComponent(OKButton));
        pack();
        setVisible(true);
    }

    public void performDropDown() {
        if (dropdownChoice == 1) {
            atm_controller.printByCustomerName();
        } else if (dropdownChoice == 2) {
            atm_controller.printHighestBalance();
        } else if (dropdownChoice == 3) {
            atm_controller.model_printAllAccounts();
        } else if (dropdownChoice == 4) {
            atm_controller.printTimeStamp();
        } else {
            atm_controller.callStartPage();
        }
    }



    public void printInfo(String totalInformation) {
        JTextArea textArea = new JTextArea(500,500);
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);
        textArea.append(totalInformation);


        OKButton = new JButton("OK");
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atm_controller.model_admin();
            }
        });

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(textArea)
                .addComponent(OKButton));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(textArea))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(OKButton)));

        pack();
        setVisible(true);
    }

    public void showOneCustomerScreen(ArrayList<String> allIDs) {
        // Shows a screen of all the customers in the database
        JLabel menuLabel = new JLabel("Please select a customer:");
        menuLabel.setFont(new Font("Futura", Font.PLAIN, 16));
        contentPane.add(menuLabel);


        String[] menuOptions = allIDs.toArray(new String[allIDs.size()]);
        JComboBox<String> ID_options = new JComboBox<>(menuOptions);
        ID_options.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox mo = (JComboBox) e.getSource();
                String id = (String)mo.getSelectedItem();
                setChoice(id);
            }
        });

        OKButton = new JButton("OK");
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atm_controller.printAllAccounts(customerID);
            }
        });
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(menuLabel)
                        .addComponent(OKButton))
                .addComponent(ID_options));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(menuLabel)
                        .addComponent(ID_options))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(OKButton)));

        pack();
        setVisible(true);

    }

    public void setChoice(String choice) {
        customerID = choice;
    }

    public String getCustomerID() {
        return customerID;
    }
}