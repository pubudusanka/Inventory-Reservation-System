package com.order.management.inventoryreservation.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StandardResponseDto {

    private int code;
    private String message;
    private Object data;
}
