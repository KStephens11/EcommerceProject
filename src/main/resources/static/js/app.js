import { Products } from './modules/products.js';
import {Navbar} from "./modules/navbar.js";
import {ProductManagement} from "./modules/product-management.js";

$(document).ready(function () {

    const products = new Products();
    products.init();

    const productManagement = new ProductManagement();
    productManagement.init();

    $.get("/api/users/me", (data) => {

        const navbar = new Navbar(data, products);
        navbar.init();

    });

});