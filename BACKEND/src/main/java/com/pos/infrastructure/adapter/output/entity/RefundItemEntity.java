package com.pos.infrastructure.adapter.output.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "refund_items")
public class RefundItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refund_id", nullable = false)
    private RefundEntity refund;

    @Column(nullable = false, length = 50)
    private String sku;

    @Column(nullable = false)
    private int quantity;

    public RefundItemEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public RefundEntity getRefund() { return refund; }
    public void setRefund(RefundEntity refund) { this.refund = refund; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
