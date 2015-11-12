import java.text.NumberFormat;

public class Sav_Acct extends Account {

    static final long serialVersionUID = -3005023205032780691L;
    private NumberFormat formatter = NumberFormat.getCurrencyInstance();
    private static double depositAmount;

    public Sav_Acct(int starting_account_number, String aPin, String aName, String anID) {
        //int starting_account_number, String aPin, String aName, String anID)
        super(starting_account_number, aPin, aName, anID);

    }

    public void addInterest_rate(double balance, int interest_rate) {
        // "Deposits" the added interest into the account.
        double depositAmount = balance * interest_rate * 0.01;
        super.depositMoney(depositAmount);
        double newBalance = returnBalance();
        String balanceAsString = formatter.format(newBalance);
        String depositAsString = formatter.format(depositAmount);
    }

    public double getDepositAmount() {
        return depositAmount;
    }
}
