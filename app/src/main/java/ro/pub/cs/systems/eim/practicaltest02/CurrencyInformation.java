package ro.pub.cs.systems.eim.practicaltest02;

import java.util.Date;

public class CurrencyInformation {

    public Date updated;
    public Double rate;
    public String currency;

    public CurrencyInformation(Date updated, Double rate, String currency) {
        this.updated = updated;
        this.rate = rate;
        this.currency = currency;
    }

    public String toString() {
        return currency + ": " + this.rate.toString();
    }
}
