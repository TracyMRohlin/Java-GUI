import java.io.Serializable;
import java.util.Comparator;


public class Account implements Serializable
        {
        private String number;		// 4 digits string
        private double balance;
        private boolean active;
        public static int nextAccount = 0;
            private String	name;
            private String	id;	// 3 digits string
            private String	pin;	// 4 digits string
            static final long serialVersionUID = -3005023205032780691L;

        public Account(int starting_account_number, String aPin, String aName, String anID) {
            int newAccount = nextAccount + starting_account_number;
            number = Integer.toString(newAccount);
            balance = 0;
            name = aName;
            pin = aPin;
            id = anID;
            nextAccount++;
            active = true;

        }

            public String returnNumber() {
               return number;
            }

            public double returnBalance() {
            return balance;
            }

            public void depositMoney(double deposit) {
                balance += deposit; // adds money to the balance
            }

            public void withdrawMoney(double withdrawal){
                balance -= withdrawal; // removes money from the balance
            }

            public void setBalanceNull() {
                balance = 0;
                active = false;
            }

            public boolean checkActive() {
                return active;
            }

            public static Comparator<Account> CompareBalances = new Comparator<Account>() {
                // Sorts the IDs in ascending order
                public int compare(Account a1, Account a2) {
                    double Balance1 = a1.returnBalance();
                    double Balance2 = a2.returnBalance();

                    //ascending order
                    return Double.compare(Balance2, Balance1);
                }
            };

            public String getID() {
                return id;
            }

            public String getName() {
                return name;
            }

            public String getPin() {
                return pin;
            }
        }

