package com.farmwisdom.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class AdminStatsResponse {
    private long userCount;
    private long postCount;
    private long cropCount;
    private long weatherCount;
} 