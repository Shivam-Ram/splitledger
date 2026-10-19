package com.splitledger.app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "group_members")
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "percentage_share", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentageShare; // e.g., 40.00, 30.00, 15.00

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public GroupMember() {}

    public GroupMember(User user, BigDecimal percentageShare) {
        this.user = user;
        this.percentageShare = percentageShare;
        this.active = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BigDecimal getPercentageShare() { return percentageShare; }
    public void setPercentageShare(BigDecimal percentageShare) { this.percentageShare = percentageShare; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}