package com.store.inventario.module.buy.request;

import java.math.BigDecimal;

public record CreatePurchaseDetailRequest(String productCode, BigDecimal purchasePrice, int quantity) {
}
