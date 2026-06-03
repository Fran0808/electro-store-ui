package com.store.inventario.model.guia;

import java.util.List;

public record CreateInventoryGuideRequest(
    String type,
    String reason,
    String description,
    List<CreateGuideDetailRequest> detail
) {}
