import { Products } from './modules/products.js';
import {Navbar} from "./modules/navbar.js";
import {ProductManagement} from "./modules/product-management.js";
import {Cart} from "./modules/cart.js";
import {OrdersPanel} from "./modules/orders-panel.js";

$(document).ready(function () {

    const products = new Products();
    products.init();

    const productManagement = new ProductManagement();
    productManagement.init();

    const ordersPanel = new OrdersPanel();
    ordersPanel.init();

    $.get("/api/users/me", (data) => {

        const cart = new Cart(products, data.username);
        cart.init();
        window.cartInstance = cart;

        const navbar = new Navbar(data, products, ordersPanel, productManagement);
        navbar.init();

    })

});