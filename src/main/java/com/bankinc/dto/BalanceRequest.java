package com.bankinc.dto;

import java.math.BigDecimal;

public class BalanceRequest {

    private String cardId;
    private BigDecimal balance;

    public String getCardId() {
        return cardId;
    }
    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
