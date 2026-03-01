export class Navbar {

    constructor(userData, productsInstance) {
        this.userData = userData;
        this.products = productsInstance;
    }

    init() {
        this.showButton();
        this.setUsername();
        this.bindEvents();
    }

    showButton() {
        if (this.userData.roles.includes("ROLE_ADMIN")) {
            $("#productManagementBtn").show();
        }
    }

    bindEvents() {

        if (this.userData.roles.includes("ROLE_ADMIN")) {

            $("#productManagementBtn").click(() => {
                $("#management-section").show();
                $("#products-section").hide();
            });

        }

        $("#homeBtn").click(() => {
            $("#management-section").hide();
            $("#products-section").show();

            this.products.loadProducts();   // 🔥 now works
        });

    }

    setUsername() {
        if (this.userData?.username) {
            $("#usernameDisplay").text(this.userData.username);
        }
    }
}