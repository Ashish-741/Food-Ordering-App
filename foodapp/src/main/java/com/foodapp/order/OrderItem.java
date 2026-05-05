package com.foodapp.order;

import com.foodapp.menu.MenuItem;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Snapshot of a menu item at the time of order.
 * Stores item_name and unit_price so that future menu changes don't affect past orders.
 */
@Entity
@Table(name = "order_items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    // Snapshot fields — preserved even if the vendor changes the menu later
    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private BigDecimal totalPrice; // unitPrice * quantity
}
