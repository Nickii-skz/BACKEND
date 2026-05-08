package com.pos.domain.exception;
import java.util.UUID;

public class SaleNotFoundException extends PosException {
    private final UUID saleId;
    public SaleNotFoundException(UUID saleId) {
        super("Sale not found with ID: " + saleId);
        this.saleId = saleId;
    }
    public UUID getSaleId() { return saleId; }
}
