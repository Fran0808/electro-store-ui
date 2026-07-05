package com.store.inventario.module.buy.request;

import java.util.List;

public record CreatePurchaseRequest(String supplierCode, List<CreatePurchaseDetailRequest> detail) {
}
