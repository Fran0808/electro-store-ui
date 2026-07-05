package com.store.inventario.module.movement.request;

import java.util.List;

public record CreateInventoryGuideRequest(
    String type,
    String reason,
    String description,
    List<CreateGuideDetailRequest> detail
) {}
