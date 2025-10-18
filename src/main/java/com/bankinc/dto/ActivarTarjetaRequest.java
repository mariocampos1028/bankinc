package com.bankinc.dto;

import lombok.Getter;
import lombok.Setter;


public class ActivarTarjetaRequest {

    private String cardId;

    public String getCardId() {
        return cardId;
    }
    public void setCardId(String idTarjeta) {
        this.cardId = idTarjeta;
    }

}
