export class OrdersPanel {

    constructor() {
        this.ordersBtn = $("#ordersBtn");
        this.ordersSection = $("#orders-section");
        this.statsView = $("#statsView");
        this.ordersView = $("#ordersView");
        this.ordersTableBody = $("#ordersTableBody");
        this.lowStockList = $("#lowStockList");
        this.categorySales = $("#categorySales");
        this.totalRevenue = $("#totalRevenue");
    }

    init() {
        this.bindEvents();
    }

    bindEvents() {

        $("#ordersBtn").click(() => {
            $("#products-section, #management-section").hide();
            this.ordersSection.show();
            this.loadStats();      // load on first show
            this.loadOrders();     // optional - can lazy load
        });

        $("#statsTab").click(() => {
            this.statsView.show();
            this.ordersView.hide();
            $("#statsTab").addClass("active");
            $("#ordersTab").removeClass("active");
        });

        $("#ordersTab").click(() => {
            this.statsView.hide();
            this.ordersView.show();
            $("#ordersTab").addClass("active");
            $("#statsTab").removeClass("active");
            this.loadOrders();     // reload when switching tab
        });

        $("#homeBtn").click(() => {
            this.ordersSection.hide(); // hide when going home
        });
    }

    loadStats() {

        // Sales by Category
        $.get("/api/orders/stats/sales-by-category", (data) => {
            this.categorySales.empty();
            let totalRev = 0;

            data.forEach(item => {
                totalRev += Number(item.total);
                this.categorySales.append(`
                    <div class="list-group-item d-flex justify-content-between">
                        <span>${item.category}</span>
                        <strong>€${Number(item.total).toFixed(2)}</strong>
                    </div>
                `);
            });

            this.totalRevenue.text(`€${totalRev.toFixed(2)}`);

        }).fail(() => {
            this.totalRevenue.text("Error loading");
        });


        // Low stock items
        $.get("/api/products/low-stock?threshold=5", (lowStockProducts) => {
            this.lowStockList.empty();

            if (lowStockProducts.length === 0) {
                this.lowStockList.append(
                    '<div class="list-group-item text-success">No low stock products!</div>'
                );
            } else {
                lowStockProducts.forEach(p => {
                    const alertClass = p.quantity <= 2 ? 'list-group-item-danger' : 'list-group-item-warning';
                    this.lowStockList.append(`
                    <div class="list-group-item ${alertClass} d-flex justify-content-between align-items-center">
                        <div>
                            <strong>${p.name}</strong><br>
                            <small>${p.category || '—'} • ${p.brand || '—'}</small>
                        </div>
                        <span class="badge bg-dark rounded-pill">Stock: ${p.quantity}</span>
                    </div>
                `);
                });
            }
        }).fail(() => {
            this.lowStockList.html('<div class="list-group-item text-danger">Failed to load low stock</div>');
        });

    }

    loadOrders() {
        $.get("/api/orders", (orders) => {  // assume you add this endpoint
            this.ordersTableBody.empty();

            if (orders.length === 0) {
                this.ordersTableBody.append('<tr><td colspan="5" class="text-center">No orders yet</td></tr>');
                return;
            }

            orders.forEach(order => {
                const itemsCount = order.items?.length || 0;
                const row = `
                    <tr>
                        <td>#${order.id}</td>
                        <td>${new Date(order.orderDate).toLocaleString()}</td>
                        <td>${order.username || "Guest"}</td>
                        <td>${itemsCount} item${itemsCount !== 1 ? 's' : ''}</td>
                        <td class="text-success">€${Number(order.totalAmount).toFixed(2)}</td>
                    </tr>
                `;
                this.ordersTableBody.append(row);
            });
        }).fail(() => {
            this.ordersTableBody.html('<tr><td colspan="5" class="text-danger">Failed to load orders</td></tr>');
        });
    }

}