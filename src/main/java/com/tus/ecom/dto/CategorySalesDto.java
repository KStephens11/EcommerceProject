package com.tus.ecom.dto;

import java.math.BigDecimal;

public class CategorySalesDto {
    public String category;
    public BigDecimal total;

    public CategorySalesDto(String category, BigDecimal total) {
        this.category = category;
        this.total = total;
    }

    public CategorySalesDto() {

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}