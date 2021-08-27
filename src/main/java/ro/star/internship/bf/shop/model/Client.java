package ro.star.internship.bf.shop.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String username;
    private long balance;

    public long getBalance() {
        return balance;
    }

    public String getUserName() {
        return username;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}

