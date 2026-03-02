export class Products {

    constructor() {
        this.allProducts = [];
    }

    init() {
        this.bindEvents();
        this.loadProducts();
    }

    bindEvents() {
        $(document).on('click', '.product-card', (e) => {

            const productId = Number($(e.currentTarget).data('id'));
            const product = this.allProducts.find(p => p.id === productId);

            if (!product) return;

            this.showModal(product);

        });

        $(document).on('click', '#addToCartBtn', function() {
            const productId = Number($(this).data('product-id'));

            if (!productId || isNaN(productId)) {
                console.warn("No valid product ID found for add to cart");
                return;
            }

            if (window.cartInstance) {
                window.cartInstance.addToCart(productId, 1);

                const $btn = $(this);
                const originalText = $btn.html();
                $btn.html('<i class="bi bi-check-lg"></i> Added!')
                    .prop('disabled', true)
                    .addClass('btn-success');

                setTimeout(() => {
                    $btn.html(originalText)
                        .prop('disabled', false)
                        .removeClass('btn-success');
                }, 800);

            } else {
                console.error("Cart instance not available");
            }
        });

        $("#searchForm").submit((e) => {
            e.preventDefault();

            const searchInput = $("#searchInput").val().trim();

            if (!searchInput) {
                this.loadProducts();
                return;
            }

            $.ajax({
                url: "/api/products/name",
                method: "GET",
                dataType: "json",
                data: {
                    name: searchInput,
                    page: 0,
                    size: 10
                },
                success: (products) => {

                    this.renderProducts(products.content);

                },
                error: (xhr, status, error) => {
                    console.error("Error:", error);
                }

            });

        })

    }

    loadProducts() {
        $.ajax({
            url: '/api/products',
            method: 'GET',
            dataType: 'json',
            data: { page: 0, size: 10 },

            success: (products) => {

                this.allProducts = products.content;

                this.renderProducts(products.content);
            },

            error: (xhr, status, error) => {
                $('#error')
                    .text('Failed to load products: ' + error)
                    .show();

                console.error('Error:', error);
            }
        });
    }

    renderProducts(products) {
        const container = $('#products');
        container.empty();

        if (!products || products.length === 0) {
            container.html(
                '<p class="text-center text-muted">No products found.</p>'
            );
        }
        else {
            products.forEach(product => {

                const card = `
                <div class="col-md-4 mb-4 product-card clickable"
                     data-id="${product.id}"
                     role="button" tabindex="0">
                    <div class="card h-100 shadow-sm">
                        <img src="${product.image}" class="card-img-top"
                             alt="${product.name}"
                             style="height: 200px; object-fit: contain; padding: 10px;">
                        <div class="card-body text-center">
                            <h5 class="card-title">${product.name}</h5>
                            <p class="card-text text-danger fw-bold">
                                €${product.price.toFixed(2)}
                            </p>
                        </div>
                    </div>
                </div>
            `;

                container.append(card);
            });
        }

    }

    showModal(product) {

        const modalContent = `
        <div class="row">
            <div class="col-md-5 text-center">
                <img src="${product.image}" alt="${product.name}"
                     class="img-fluid rounded mb-3"
                     style="max-height: 350px; object-fit: contain;">
            </div>
            <div class="col-md-7">
                <h3>${product.name}</h3>
                <p class="text-muted mb-1"><strong>Brand:</strong> ${product.brand}</p>
                <p class="text-muted mb-1"><strong>Category:</strong> ${product.category}</p>
                <p class="text-muted mb-3"><strong>Stock:</strong> ${product.quantity} units</p>
                <h4 class="text-danger">€${product.price.toFixed(2)}</h4>
                <hr>
                <h5>Description</h5>
                <p>${product.description || 'No description available.'}</p>
            </div>
        </div>
    `;

        $('#modalBody').html(modalContent);
        $('#productModalLabel').text(product.name);

        // ─── Important ───
        // Store the product ID so the "Add to Cart" button knows what to add
        $('#addToCartBtn').data('product-id', product.id);

        const modal = new bootstrap.Modal(
            document.getElementById('productModal')
        );

        modal.show();
    }
}