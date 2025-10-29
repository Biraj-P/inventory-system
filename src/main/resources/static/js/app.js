document.addEventListener("DOMContentLoaded", () => {
    //Intitial load of inventory when page is ready
    loadInventory();

    //attach event listener to sale form
    document.getElementById('saleForm').addEventListener('submit', handleSale);
});

// Base URL for Spring Boot API (backend)
const API_URL = 'http://localhost:8080/api/inventory';
const messageElement = document.getElementById('message');

/*
* Displays a temporary message(success or failure) to the user
* @param {string} msg - The message to display
* @param {string} type - The type of message ('success' or 'error')
 */
function displayMessage(text, type) {
    messageElement.textContent = text;
    messageElement.className = type;
    setTimeout(() => {
        messageElement.textContent = '';
        messageElement.className = '';
    }, 4000); // Clear message after 4 seconds
}

/*
* Fetches inventory data from the backend(Spring Boot API) and populates/updates the HTML table
 */
async function loadInventory() {
    const tableBody = document.getElementById('inventoryTableBody');
    tableBody.innerHTML = '<tr><td colspan="4"> Loading Inventory...</td></tr>';

    try{
        const response = await fetch(API_URL);
        const products = await response.json();

        tableBody.innerHTML = ''; //Clear loading message

        products.forEach(product => {
            const row = tableBody.insertRow();
            row.insertCell().textContent = product.name;
            row.insertCell().textContent = product.sku;
            row.insertCell().textContent = `$${product.price.toFixed(2)}`;
            row.insertCell().textContent = product.stockQuantity;
        });
    } catch (error) {
        tableBody.innerHTML = '<tr><td colspan="4">Error loading data from the sever. Is the Spring Boot API running?</td></tr>';
        console.error('Error fetching inventory:', error);
    }
}

/*
* Handles the submission of the sale form
* @param {Event} event - The form submission event
 */
async function handleSale(event) {
    event.preventDefault(); //prevent page reload

    const skuInput = document.getElementById('saleSku').value;
    const quantityInput = parseInt(document.getElementById('saleQuantity').value);

    if(!skuInput || quantityInput < 1){
        displayMessage('Please enter a valid SKU and quantity.', 'error');
        return;
    }

    const saleRequest = {
        sku: skuInput,
        quantitySold: quantityInput
    };

    try{
        const response = await fetch(`${API_URL}/sale`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(saleRequest)
        });
        if(response.ok) {
            // Sale successful (HTTP 200 OK)
            displayMessage(`Sale recorded successfully! Stock for ${skuInput} updated`, 'success');
            await loadInventory(); // Refresh inventory table to show updated stock
        } else {
            // Handle bad requests like insufficient stock or invalid input
            const errorText = await response.text();
            displayMessage(`Error recording sale: ${errorText}`, 'error');
        }
    } catch (error) {
        displayMessage('Error connecting to the server. Please try again later.', 'error');
        console.error('Error processing sale:', error);
    }
}