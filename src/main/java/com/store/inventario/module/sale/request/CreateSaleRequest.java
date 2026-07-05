package com.store.inventario.module.sale.request;

import java.util.List;

public record CreateSaleRequest(
    String customerCode,
    List<CreateSaleDetailRequest> detail
) {}
