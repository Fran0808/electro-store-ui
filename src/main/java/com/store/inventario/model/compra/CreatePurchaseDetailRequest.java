package com.store.inventario.model.compra;

import java.math.BigDecimal;

public record CreatePurchaseDetailRequest(String productCode, BigDecimal purchasePrice, int quantity) {
}
