package com.fitpro.dto.crm;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CrmSummaryResponse {
    private long totalLeads;
    private long newLeads;
    private long wonLeads;
    private long lostLeads;
    private double conversionRate;
}
