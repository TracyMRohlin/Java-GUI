import java.awt.*;
import java.util.Comparator;
import java.io.Serializable;

public class AdminLog implements Serializable {
    private String custID;
    private String account;
    private double amount;
    private int transID;
    private long Atimestamp;
    static final long serialVersionUID = -3005023205032780691L;


    public AdminLog(long timestamp, int transactionID, String customerID, String accountNum, double anAmount) {
        Atimestamp = timestamp;
        account = accountNum;
        amount = anAmount;
        transID = transactionID;
        custID = customerID;
    }

    public long getTimestamp() {
        return Atimestamp;
    }

    public int getTransactionID() {
        return transID;
    }

    public String getCustomerID() {
        return custID;
    }

    public String getAccountNum() {
        return account;
    }

    public double getAmount() {
        return amount;
    }

    public static Comparator<AdminLog> CompareTimeStamp = new Comparator<AdminLog>() {
        // Sorts the IDs in ascending order
        public int compare(AdminLog a1, AdminLog a2) {
            double Balance1 = a1.getTimestamp();
            double Balance2 = a2.getTimestamp();

            //descending order
            return Double.compare(Balance1, Balance2);
        }
    };
}