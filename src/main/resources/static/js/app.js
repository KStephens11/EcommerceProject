import { Products } from './modules/products.js';
import {Navbar} from "./modules/navbar.js";

$(document).ready(function () {

    const products = new Products();
    const navbar = new Navbar();

    products.init();
    navbar.init();

});