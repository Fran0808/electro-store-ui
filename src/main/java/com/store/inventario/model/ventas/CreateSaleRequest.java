package com.store.inventario.model.ventas;

import java.util.List;

public record CreateSaleRequest(
    String customerCode,
    List<CreateSaleDetailRequest> detail
) {}
