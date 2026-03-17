package com.tus.ecom.dto;

import java.math.BigDecimal;

public class SalesDto {
    private String saleType;
    private BigDecimal total;

    public SalesDto(String saleType, BigDecimal total) {
        this.saleType = saleType;
        this.total = total;
    }

    public SalesDto() {

    }

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}