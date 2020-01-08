package com.sakthiveld.nio;

public class AskOrBidEntry {
    private long price;
    private long qty;
    private long orders;

    public AskOrBidEntry(long price, long qty, long orders) {
        this.price = price;
        this.qty = qty;
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "AskOrBidEntry{" +
                "price=" + price +
                ", qty=" + qty +
                ", orders=" + orders +
                '}';
    }
}
