package ro.star.internship.bf.shop.model;

public class Client {
    private final String userName;
    private long balance;
    public Client(String userName, long balance){
        this.userName = userName;
        this.balance = balance;
    }

    public long getBalance() {
        return balance;
    }

    public String getUserName() {
        return userName;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}

