package com.sun.wineshop.dto.request;

import java.util.List;

public record ProductSearchRequest(
    String name,
    Double minPrice,
    Double maxPrice,
    Double minAlcoholPercentage,
    Double maxAlcoholPercentage,
    List<Long> categoryIds
){}
