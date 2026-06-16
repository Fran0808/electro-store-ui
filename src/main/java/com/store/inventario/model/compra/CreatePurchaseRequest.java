package com.store.inventario.model.compra;

import java.util.List;

public record CreatePurchaseRequest(String supplierCode, List<CreatePurchaseDetailRequest> detail) {
}
