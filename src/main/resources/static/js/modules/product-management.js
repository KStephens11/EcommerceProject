export class ProductManagement {

    constructor() {

        this.tableBody = $("#managementProductTable");

        this.createBtn = $("#createProductBtn");
        this.saveBtn = $("#saveProductBtn");
        this.confirmDeleteBtn = $("#confirmDeleteBtn");

        this.productFormModal = new bootstrap.Modal(
            document.getElementById("productFormModal")
        );

        this.deleteModal = new bootstrap.Modal(
            document.getElementById("deleteProductModal")
        );

    }

    init() {
        this.bindEvents();
        this.loadProducts();
    }

    bindEvents() {

        // Create product modal open
        this.createBtn.click(() => {
            this.clearForm();
            $("#productFormTitle").text("Create Product");
            this.productFormModal.show();
        });

        // Save product (create/update)
        this.saveBtn.click(() => {
            this.saveProduct();
        });

        // Delete confirmation
        this.confirmDeleteBtn.click(() => {
            this.deleteProduct();
        });

        // Edit product button (delegated event)
        $(document).on("click", ".edit-product", (e) => {
            const id = $(e.currentTarget).data("id");
            this.editProduct(id);
        });

        // Delete product button
        $(document).on("click", ".delete-product", (e) => {
            const id = $(e.currentTarget).data("id");

            $("#deleteProductId").val(id);
            this.deleteModal.show();

        });
    }

    loadProducts() {

        $.get("/api/products?page=0&size=100", (page) => {   // explicit paging
            if (page && page.content) {
                this.renderTable(page.content);
            } else {
                console.error("Unexpected response format", page);
                this.tableBody.html("<tr><td colspan='7'>Error loading products</td></tr>");
            }
        });
    }

    renderTable(products) {

        this.tableBody.empty();

        products.forEach(p => {

            this.tableBody.append(`
                <tr>
                    <td>
                        <img src="${p.image}" style="height:50px; object-fit:contain" alt="">
                    </td>
                    <td>${p.name}</td>
                    <td>${p.brand}</td>
                    <td>${p.category}</td>
                    <td>€${p.price.toFixed(2)}</td>
                    <td>${p.quantity}</td>
                    <td class="text-center">
                        <button class="btn btn-warning btn-sm edit-product"
                                data-id="${p.id}">
                            Edit
                        </button>

                        <button class="btn btn-danger btn-sm delete-product"
                                data-id="${p.id}">
                            Delete
                        </button>
                    </td>
                </tr>
            `);
        });
    }

    clearForm() {
        $("#productId").val("");
        $("#productName").val("");
        $("#productBrand").val("");
        $("#productCategory").val("");
        $("#productPrice").val("");
        $("#productStock").val("");
        $("#productImage").val("");
    }

    editProduct(id) {

        $.get(`/api/products/${id}`, (product) => {

            $("#productId").val(product.id);
            $("#productName").val(product.name);
            $("#productBrand").val(product.brand);
            $("#productCategory").val(product.category);
            $("#productPrice").val(product.price);
            $("#productStock").val(product.quantity);
            $("#productImage").val(product.image);

            $("#productFormTitle").text("Update Product");

            this.productFormModal.show();

        });
    }

    saveProduct() {

        const product = {
            id: $("#productId").val() || null,
            name: $("#productName").val(),
            brand: $("#productBrand").val(),
            category: $("#productCategory").val(),
            price: parseFloat($("#productPrice").val()),
            quantity: parseInt($("#productStock").val()),
            image: $("#productImage").val()
        };

        const method = product.id ? "PUT" : "POST";
        const url = product.id
            ? `/api/products/${product.id}`
            : "/api/products";

        $.ajax({
            url,
            method,
            contentType: "application/json",
            data: JSON.stringify(product),
            success: () => {
                this.closeModal("productFormModal");
                this.loadProducts();
            },
            error: (xhr) => {
                console.error(xhr);
            }
        });
    }

    deleteProduct() {

        const id = $("#deleteProductId").val();

        $.ajax({
            url: `/api/products/${id}`,
            method: "DELETE",
            success: () => {
                this.closeModal("deleteProductModal");
                this.loadProducts();
            },
            error: (xhr) => {
                console.error(xhr);
            }
        });
    }

    closeModal(modalId) {

        const modalElement = document.getElementById(modalId);

        const modalInstance = bootstrap.Modal.getInstance(modalElement)
            || new bootstrap.Modal(modalElement);

        modalInstance.hide();

        // Safety cleanup
        document.querySelectorAll(".modal-backdrop")
            .forEach(el => el.remove());

        document.body.classList.remove("modal-open");
    }

}