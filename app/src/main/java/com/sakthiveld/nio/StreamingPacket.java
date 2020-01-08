package com.sakthiveld.nio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class StreamingPacket {

    private String sToken;
    private byte iMktsegID;
    private long lOpenPrice;
    private long lHighPrice;
    private long lLowPrice;
    private int iLastPrice;
    private long lClosePrice; //TODO:Like change they can calculate close.
    private long lAvgPrice;
    private long lTotalVolume;
    private long lLastUpdatedTime;
    private int iChange;
    private double dChangePercent;
    private long lYearHigh;
    private long lYearLow;
    private long lTotalBuyQty;
    private long lTotalSellQty;
    private long lLcl;
    private long lUcl;
    private AskOrBidEntry[] ask_entry = new AskOrBidEntry[AppConstant.ASK_BID_SIZE];
    private AskOrBidEntry[] bid_entry = new AskOrBidEntry[AppConstant.ASK_BID_SIZE];
    private long lOI;
    private int iOIChange;

    @Override
    public String toString() {
        return "StreamingPacket{" +
                "sToken='" + sToken + '\'' +
                ", iMktsegID=" + iMktsegID +
                ", lOpenPrice=" + lOpenPrice +
                ", lHighPrice=" + lHighPrice +
                ", lLowPrice=" + lLowPrice +
                ", iLastPrice=" + iLastPrice +
                ", lClosePrice=" + lClosePrice +
                ", lAvgPrice=" + lAvgPrice +
                ", lTotalVolume=" + lTotalVolume +
                ", lLastUpdatedTime=" + lLastUpdatedTime +
                ", iChange=" + iChange +
                ", dChangePercent=" + dChangePercent +
                ", lYearHigh=" + lYearHigh +
                ", lYearLow=" + lYearLow +
                ", lTotalBuyQty=" + lTotalBuyQty +
                ", lTotalSellQty=" + lTotalSellQty +
                ", lLcl=" + lLcl +
                ", lUcl=" + lUcl +
                ", ask_entry=" + Arrays.toString(ask_entry) +
                ", bid_entry=" + Arrays.toString(bid_entry) +
                ", lOI=" + lOI +
                ", iOIChange=" + iOIChange +
                '}';
    }

    void createFromBytes(byte[] bytes, int transCode) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.nativeOrder());
        switch (transCode) {
            case AppConstant.QUOTE_1:
            case AppConstant.QUOTE_1_NCDEX:
                frameQuote1(buffer, transCode);
                break;
            case AppConstant.QUOTE_2:
            case AppConstant.QUOTE_2_NCDEX:
                frameQuote2(buffer, transCode);
                break;
            case AppConstant.QUOTE_3:
            case AppConstant.QUOTE_3_NCDEX:
                frameQuote3(buffer, transCode);
                break;
        }
    }

    private void frameQuote1(ByteBuffer buffer, int transCode) {

        if (transCode == AppConstant.QUOTE_1_NCDEX) {
            byte[] bToken = new byte[AppConstant.TOKEN_LENGTH];
            for (int i = 0; i < bToken.length; i++) {
                bToken[i] = buffer.get();
            }
            sToken = new String(bToken);
        } else {
            //long iToken = Integer.toUnsignedLong(buffer.getInt());
            long iToken = buffer.getInt(0);
            sToken = String.valueOf(iToken);
        }
        iMktsegID = buffer.get();
        iLastPrice = buffer.getInt();
        iChange = buffer.getInt();
        dChangePercent = ((double) iChange / ((double) iLastPrice - (double) iChange)) * 100.0;
    }

    private void frameQuote2(ByteBuffer buffer, int transCode) {
        if (transCode == AppConstant.QUOTE_2_NCDEX) {
            byte[] bToken = new byte[AppConstant.TOKEN_LENGTH];
            for (int i = 0; i < bToken.length; i++) {
                bToken[i] = buffer.get();
            }
            sToken = new String(bToken);
        } else {
            int lToken = buffer.getInt();
            sToken = String.valueOf(lToken);
        }
        iMktsegID = buffer.get();
        lOpenPrice = buffer.getInt(0);
        lHighPrice = buffer.getInt(0);
        lLowPrice = buffer.getInt(0);
        iLastPrice = buffer.getInt();
        lClosePrice = buffer.getInt(0);
        lAvgPrice = buffer.getInt(0);
        lTotalVolume = buffer.getInt(0);
        lLastUpdatedTime = buffer.getInt(0);
        iChange = buffer.getInt();
        dChangePercent = ((double) iChange / ((double) iLastPrice - (double) iChange)) * 100.0;
        lYearHigh = buffer.getInt(0);
        lYearLow = buffer.getInt(0);
        lTotalBuyQty = buffer.getInt(0);
        lTotalSellQty = buffer.getInt(0);
        lLcl = buffer.getInt(0);
        lUcl = buffer.getInt(0);

        for (int i = 0; i < ask_entry.length; i++) {
            long askPrice = buffer.getInt(0);
            long askQty = buffer.getInt(0);
            long askOrders = buffer.getInt(0);
            AskOrBidEntry ask = new AskOrBidEntry(askPrice, askQty, askOrders);
            ask_entry[i] = ask;
        }

        for (int i = 0; i < bid_entry.length; i++) {
            long bidPrice = buffer.getInt(0);
            long bidQty = buffer.getInt(0);
            long bidOrders = buffer.getInt(0);
            AskOrBidEntry bid = new AskOrBidEntry(bidPrice, bidQty, bidOrders);
            bid_entry[i] = bid;
        }
    }

    private void frameQuote3(ByteBuffer buffer, int transCode) {

        if (transCode == AppConstant.QUOTE_3_NCDEX) {
            byte[] bToken = new byte[AppConstant.TOKEN_LENGTH];
            for (int i = 0; i < bToken.length; i++) {
                bToken[i] = buffer.get();
            }
            sToken = new String(bToken);
        } else {
            long lToken = buffer.getInt(0);
            sToken = String.valueOf(lToken);
        }
        iMktsegID = buffer.get();
        iLastPrice = buffer.getInt();
        iChange = buffer.getInt();
        dChangePercent = ((double) iChange / ((double) iLastPrice - (double) iChange)) * 100.0;
        lTotalVolume = buffer.getInt(0);
        lLastUpdatedTime = buffer.getInt(0);
        lYearHigh = buffer.getInt(0);
        lYearLow = buffer.getInt(0);
        lOI = buffer.getInt(0);
        iOIChange = buffer.getInt();
    }

}
