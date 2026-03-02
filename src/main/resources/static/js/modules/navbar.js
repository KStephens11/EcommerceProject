export class Navbar {

    constructor(userData, productsInstance, ordersInstance, managementInstance) {
        this.userData = userData;
        this.products = productsInstance;
        this.orders = ordersInstance;
        this.management = managementInstance;
    }

    init() {
        this.showButton();
        this.setUsername();
        this.bindEvents();
    }

    showButton() {
        if (this.userData.roles.includes("ROLE_ADMIN")) {
            $("#productManagementBtn").show();
            $("#ordersBtn").show()
        }
    }

    bindEvents() {

        if (this.userData.roles.includes("ROLE_ADMIN")) {

            $("#productManagementBtn").click(() => {
                $("#products-section").hide();
                $("#orders-section").hide();
                $("#management-section").show();
                this.management.loadProducts();

            });

            $("#ordersBtn").click(() => {
                $("#management-section").hide();
                $("#products-section").hide();
                $("#orders-section").show();
                this.orders.loadStats();
                this.orders.loadOrders();
            })
        }

        $("#logout").click( () => {

            $.post("/api/auth/logout", function () {
                window.location.href = "/login";
            });

        });


        $("#homeBtn").click(() => {
            $("#management-section").hide();
            $("#orders-section").hide();
            $("#products-section").show();

            this.products.loadProducts();

        });

    }

    setUsername() {
        if (this.userData?.username) {
            $("#usernameDisplay").text(this.userData.username);
        }
    }
}