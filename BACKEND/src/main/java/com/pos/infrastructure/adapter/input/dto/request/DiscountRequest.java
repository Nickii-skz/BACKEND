package com.pos.infrastructure.adapter.input.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

public class DiscountRequest {

    @NotBlank(message = "couponCode is required")
    @Size(max = 50)
    private String couponCode;

    @NotBlank(message = "type is required (PERCENTAGE or FIXED)")
    private String type;

    @NotNull
    @DecimalMin(value = "0.01", message = "value must be > 0")
    private BigDecimal value;

    @NotBlank(message = "scope is required (PRODUCT or CART)")
    private String scope;

    private String sku;

    @Min(value = 0)
    private int maxUsages;

    private Instant expiresAt;

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public int getMaxUsages() { return maxUsages; }
    public void setMaxUsages(int maxUsages) { this.maxUsages = maxUsages; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
