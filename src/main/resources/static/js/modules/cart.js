export class Cart {
    constructor(productsInstance, userID = null) {
        this.products = productsInstance;
        this.userKey = userID;
        this.cart = this.loadCart();
        this.updateCartCount();
    }

    init() {
        this.bindEvents();
    }

    bindEvents() {

        // Cart button event
        $('#cartBtn').click(() => {
            this.renderCart();
            const offcanvas = new bootstrap.Offcanvas(document.getElementById('cartOffcanvas'));
            offcanvas.show();
        });

        // Event for quantity and remove
        $(document).on('click', '.change-qty', (e) => {
            const id = Number($(e.currentTarget).data('id'));
            const delta = Number($(e.currentTarget).data('delta'));
            this.changeQuantity(id, delta);
        });

        $(document).on('click', '.remove-item', (e) => {
            const id = Number($(e.currentTarget).data('id'));
            this.removeItem(id);
        });

        // Checkout event
        $('#checkoutBtn').click(() => {
            this.checkout();
        });

    }

    loadCart() {
        const saved = localStorage.getItem(this.userKey);
        return saved ? JSON.parse(saved) : [];
    }

    saveCart() {
        localStorage.setItem(this.userKey, JSON.stringify(this.cart));
        this.updateCartCount();
    }

    updateCartCount() {
        const count = this.cart.reduce((sum, item) => sum + item.quantity, 0);
        $('#cartCount').text(count);
        $('#checkoutBtn').prop('disabled', count === 0);
    }

    addToCart(productId, quantity = 1) {
        const product = this.products.allProducts.find(p => p.id === productId);
        if (!product) return;

        const existing = this.cart.find(item => item.id === productId);

        if (existing) {
            existing.quantity += quantity;
        } else {
            this.cart.push({
                id: product.id,
                name: product.name,
                price: product.price,
                image: product.image,
                quantity: quantity
            });
        }

        this.saveCart();

    }

    changeQuantity(productId, delta) {
        const item = this.cart.find(i => i.id === productId);
        if (!item) return;

        item.quantity = Math.max(1, item.quantity + delta);
        if (item.quantity === 0) {
            this.cart = this.cart.filter(i => i.id !== productId);
        }

        this.saveCart();
        this.renderCart();
    }

    removeItem(productId) {
        this.cart = this.cart.filter(i => i.id !== productId);
        this.saveCart();
        this.renderCart();
    }

    getTotal() {
        return this.cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
    }

    renderCart() {
        const container = $('#cartItems');
        container.empty();

        if (this.cart.length === 0) {
            container.html('<p class="text-center text-muted">Your cart is empty</p>');
            $('#cartTotal').text('€0.00');
            return;
        }

        this.cart.forEach(item => {
            const html = `
                <div class="d-flex align-items-center mb-3 border-bottom pb-3">
                    <img src="${item.image}" alt="${item.name}" style="width:80px; height:80px; object-fit:contain;" class="me-3">
                    <div class="flex-grow-1">
                        <h6 class="mb-1">${item.name}</h6>
                        <p class="mb-1 text-danger">€${item.price.toFixed(2)}</p>
                        <div class="d-flex align-items-center">
                            <button class="btn btn-sm btn-outline-secondary change-qty" data-id="${item.id}" data-delta="-1">-</button>
                            <span class="mx-2">${item.quantity}</span>
                            <button class="btn btn-sm btn-outline-secondary change-qty" data-id="${item.id}" data-delta="1">+</button>
                        </div>
                    </div>
                    <button class="btn btn-sm btn-danger ms-3 remove-item" data-id="${item.id}">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            `;
            container.append(html);
        });

        $('#cartTotal').text(`€${this.getTotal().toFixed(2)}`);
    }

    checkout() {
        if (this.cart.length === 0) return;

        const orderRequest = {
            items: this.cart.map(item => ({
                productId: item.id,
                quantity: item.quantity
            }))
        };

        $.ajax({
            url: '/api/orders',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(orderRequest),
            success: (response) => {
                // Success
                this.cart = [];
                this.saveCart();
                this.renderCart();

                const offcanvas = bootstrap.Offcanvas.getInstance('#cartOffcanvas');
                if (offcanvas) offcanvas.hide();

            },
            error: (xhr) => {
                console.error("Order creation failed:", xhr.responseText);
            },
            complete: () => {
                this.products.loadProducts();
            }
        });
    }

}