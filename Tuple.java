public class Tuple {
    private String first;
    private double second;

    public Tuple(String accountNum, double depositAmount) {
        first = accountNum;
        second = depositAmount;
    }

    public String getFirst() {
        return first;
    }
    public double getSecond() {
        return second;
    }
}