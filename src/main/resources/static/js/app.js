let stompClient = null; // Holds the WebSocket client connection

// Base URL for Spring Boot API (backend)
const API_URL = 'http://localhost:8080/api/inventory';

document.addEventListener("DOMContentLoaded", () => {
    //Intitial load of inventory when page is ready
    loadInventory();

    //attach event listener to sale form
    document.getElementById('saleForm').addEventListener('submit', handleSale);

    //Connect to WebSocket endpoint
    connectToWebSocket();
});

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
            row.id = `product-${product.sku}`; // e.g., id="product-LPRO-001"

            row.insertCell().textContent = product.name;
            row.insertCell().textContent = product.sku;
            row.insertCell().textContent = `$${product.price.toFixed(2)}`;

            // Give the stock cell a unique ID
            const stockCell = row.insertCell();
            stockCell.id = `stock-${product.sku}`; // e.g., id="stock-LPRO-001"
            stockCell.textContent = product.stockQuantity;
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
            // await loadInventory(); // Refresh inventory table to show updated stock
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

function connectToWebSocket() {
    // 1. Create a new SockJS client using the WebSocket endpoint defined in Spring Boot (WebSocketConfig.java)
    const socket = new SockJS('/ws-inventory');

    // 2. Create a STOMP client over the SockJS connection
    stompClient = Stomp.over(socket);

    // 3. Connect the STOMP broker
    stompClient.connect({},
        //on success callback
        (frame) => {
            console.log('Connected to WebSocket:', frame);

            // 4. Subscribe to the public topic for inventory updates
            // This destination must match the one defined in InventoryServiceImpl
            stompClient.subscribe('/topic/inventory-updates', (message) => {
                //When a message is received, parse it and update the table
                const updatedProduct = JSON.parse(message.body);
                updateInventoryTableRow(updatedProduct);
            });
        },
        //on error callback
        (error) => {
            console.error('WebSocket connection error:', error);
            displayMessage('Real-time connection failed! Please refresh the page.', 'error');
        }
    );
}

/**
 * Updates a single row in the inventory table based on the real-time message.
 * @param {object} product - The updated product object from the server.
 */
function updateInventoryTableRow(product) {
    // 1. Find the specific stock cell by its ID
    const stockCell = document.getElementById(`stock-${product.sku}`);

    if(stockCell){
        // 2. Update the text content
        stockCell.textContent = product.stockQuantity;

        // 3. Add a visual flash effect to highlight the change
        const row = document.getElementById(`product-${product.sku}`);
        row.style.backgroundColor = '#d4edda'; //Light green flash
        setTimeout(() => {
            row.style.backgroundColor = ''; //Reset background
        }, 1000) //Remove flash after 1 second
    }
    else {
        console.warn(`Could not find table row for SKU: ${product.sku} to update.`);
        // This might happen if a new product is added and the page hasn't been refreshed.
        // For a full app, you might want to add a new row here.
    }
}