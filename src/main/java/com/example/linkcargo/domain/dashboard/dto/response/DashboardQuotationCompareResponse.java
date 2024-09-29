package com.example.linkcargo.domain.dashboard.dto.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record DashboardQuotationCompareResponse(

    Integer quotationCount,
    List<DashboardQuotationResponse> dashboardQuotationResponseList,
    List<Map<String,Integer>> thcCostList,
    List<Map<String,Integer>> handlingCostList,
    List<Map<String,Integer>> cfsCostList,
    List<Map<String,Integer>> listStatusCostList,
    List<Map<String,Integer>> customsClearanceCostList,
    List<Map<String,Integer>> truckingCostList,
    List<Map<String,Integer>> cicCostList,
    List<Map<String,Integer>> dofeeCostList,
    List<Map<String,Integer>> warfageCostList

) {

    public static DashboardQuotationCompareResponse fromEntity(
        List<DashboardQuotationResponse> dashboardQuotationResponseList,
        Map<String, List<Map<String, Integer>>> compareCostMap) {

        return DashboardQuotationCompareResponse.builder()
            .quotationCount(dashboardQuotationResponseList.size())
            .dashboardQuotationResponseList(dashboardQuotationResponseList)
            .thcCostList(compareCostMap.getOrDefault("thcCost", new ArrayList<>()))
            .handlingCostList(compareCostMap.getOrDefault("handlingCost", new ArrayList<>()))
            .cfsCostList(compareCostMap.getOrDefault("cfsCost", new ArrayList<>()))
            .listStatusCostList(compareCostMap.getOrDefault("liftStatusCost", new ArrayList<>()))
            .customsClearanceCostList(compareCostMap.getOrDefault("customsClearanceCost", new ArrayList<>()))
            .truckingCostList(compareCostMap.getOrDefault("truckingCost", new ArrayList<>()))
            .cicCostList(compareCostMap.getOrDefault("cicCost", new ArrayList<>()))
            .dofeeCostList(compareCostMap.getOrDefault("doFeeCost", new ArrayList<>()))
            .warfageCostList(compareCostMap.getOrDefault("warfageCost", new ArrayList<>()))
            .build();

    }
}
