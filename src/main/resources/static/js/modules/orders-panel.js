export class OrdersPanel {

    constructor() {
        this.ordersBtn = $("#ordersBtn");
        this.ordersSection = $("#orders-section");
        this.statsView = $("#statsView");
        this.ordersView = $("#ordersView");
        this.ordersTableBody = $("#ordersTableBody");
        this.lowStockList = $("#lowStockList");
        this.brandSales = $("#brandSales");
        this.totalRevenue = $("#totalRevenue");

        // Chart instances
        this.salesChart = null;
        this.revenueChart = null;
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
            const labels = [];
            const values = [];

            data.forEach(item => {
                labels.push(item.saleType);
                values.push(Number(item.total).toFixed(2));
            });

            const salesCtx = document.getElementById("salesByCategoryChart");
            if (salesCtx) {
                if (this.salesChart) this.salesChart.destroy();
                this.salesChart = new Chart(salesCtx, {
                    type: 'bar',
                    data: {
                        labels,
                        datasets: [{
                            label: 'Revenue (€)',
                            data: values,
                            backgroundColor: [
                                'rgba(13,110,253,0.75)',
                                'rgba(25,135,84,0.75)',
                                'rgba(220,53,69,0.75)',
                                'rgba(255,193,7,0.75)',
                                'rgba(13,202,240,0.75)',
                                'rgba(111,66,193,0.75)',
                                'rgba(253,126,20,0.75)',
                            ],
                            borderRadius: 6,
                            borderSkipped: false,
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: { display: false },
                            tooltip: {
                                callbacks: {
                                    label: ctx => ` €${Number(ctx.parsed.y).toFixed(2)}`
                                }
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: { callback: val => `€${val}` },
                                grid: { color: 'rgba(0,0,0,0.06)' }
                            },
                            x: { grid: { display: false } }
                        }
                    }
                });
            }

        }).fail(() => {
            console.error("Failed to load sales by category");
        });


        // Sales by Brand
        $.get("/api/orders/stats/sales-by-brand", (data) => {
            this.brandSales.empty();
            let totalRev = 0;

            data.forEach(item => {
                totalRev += Number(item.total);
                this.brandSales.append(`
                    <div class="list-group-item d-flex justify-content-between">
                        <span>${item.saleType}</span>
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


        // Revenue over time
        $.get("/api/orders", (orders) => {
            if (!orders || orders.length === 0) {
                if (this.revenueChart) { this.revenueChart.destroy(); this.revenueChart = null; }
                return;
            }

            const sorted = [...orders].sort((a, b) =>
                new Date(a.orderDate) - new Date(b.orderDate)
            );

            const labels = sorted.map(o => {
                const d = new Date(o.orderDate);
                return d.toLocaleString('en-IE', {
                    day: '2-digit', month: '2-digit', year: 'numeric',
                    hour: '2-digit', minute: '2-digit'
                });
            });
            const values = sorted.map(o => Number(o.totalAmount).toFixed(2));

            const revenueCtx = document.getElementById("revenueOverTimeChart");
            if (revenueCtx) {
                if (this.revenueChart) this.revenueChart.destroy();
                this.revenueChart = new Chart(revenueCtx, {
                    type: 'line',
                    data: {
                        labels,
                        datasets: [{
                            label: 'Order Revenue (€)',
                            data: values,
                            borderColor: 'rgba(13,110,253,0.9)',
                            backgroundColor: 'rgba(13,110,253,0.1)',
                            borderWidth: 2,
                            pointRadius: 4,
                            pointBackgroundColor: 'rgba(13,110,253,1)',
                            fill: true,
                            tension: 0.3,
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: { display: false },
                            tooltip: {
                                callbacks: {
                                    label: ctx => ` €${Number(ctx.parsed.y).toFixed(2)}`
                                }
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: { callback: val => `€${val}` },
                                grid: { color: 'rgba(0,0,0,0.06)' }
                            },
                            x: { grid: { display: false } }
                        }
                    }
                });
            }

        }).fail(() => {
            console.error("Failed to load orders for revenue chart");
        });

    }

    loadOrders() {
        $.get("/api/orders", (orders) => {
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
                    <td>${itemsCount}</td>
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