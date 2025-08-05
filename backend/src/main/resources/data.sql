-- Grocery Automation Backend - Initial Data Setup
-- This script initializes the database with mock data for development and testing

-- Insert grocery items catalog
INSERT INTO grocery_items (id, name, category, unit, barcode, sku, brand, default_threshold, active, description, image_url, allergens, calories_per_unit) VALUES
(1, 'Whole Milk', 'Dairy', 'gallon', '123456789001', 'MILK-WH-GAL', 'Farm Fresh', 0.5, true, 'Fresh whole milk', null, null, 150),
(2, 'Eggs', 'Dairy', 'dozen', '123456789002', 'EGG-LG-DZ', 'Happy Hens', 0.5, true, 'Large brown eggs', null, null, 70),
(3, 'Greek Yogurt', 'Dairy', 'container', '123456789003', 'YOG-GRK-32', 'Probiotic Plus', 2.0, true, 'Plain Greek yogurt', null, 'Milk', 100),
(4, 'Bananas', 'Produce', 'pound', '123456789004', 'BAN-YEL-LB', 'Tropical', 2.0, true, 'Fresh bananas', null, null, 89),
(5, 'Apples', 'Produce', 'pound', '123456789005', 'APL-RED-LB', 'Orchard Fresh', 3.0, true, 'Red delicious apples', null, null, 52),
(6, 'Chicken Breast', 'Meat', 'pound', '123456789006', 'CHK-BRS-LB', 'Free Range', 1.0, true, 'Boneless chicken breast', null, null, 165),
(7, 'Whole Wheat Bread', 'Bakery', 'loaf', '123456789007', 'BRD-WW-LF', 'Artisan Bakery', 1.0, true, 'Fresh whole wheat bread', null, 'Wheat, Gluten', 69),
(8, 'Orange Juice', 'Beverages', 'half gallon', '123456789008', 'JUC-ORG-HG', 'Sunny Grove', 1.0, true, 'Fresh orange juice', null, null, 112),
(9, 'Sparkling Water', 'Beverages', '12-pack', '123456789009', 'WTR-SPK-12', 'Crystal Springs', 1.0, true, 'Sparkling water 12-pack', null, null, 0),
(10, 'Pasta', 'Pantry', 'box', '123456789010', 'PST-PEN-16', 'Italian Select', 2.0, true, 'Penne pasta', null, 'Wheat, Gluten', 131);

-- Insert test user
INSERT INTO users (id, name, email, password, phone, address, latitude, longitude, active, created_at, updated_at) VALUES
(1, 'John Doe', 'john.doe@example.com', 'Test@123', '+1234567890', '123 Main Street, Apt 4B, San Francisco, CA 94105', 37.7749, -122.4194, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert stores
INSERT INTO stores (id, name, address, latitude, longitude, phone, email, opening_time, closing_time, has_delivery, has_pickup, delivery_fee, minimum_order_amount, active, accepting_orders) VALUES
(1, 'Fresh Mart Downtown', '399 4th Street, San Francisco, CA 94107', 37.7816, -122.3988, '(415) 555-0101', 'downtown@freshmart.com', '07:00:00', '22:00:00', true, true, 5.99, 25.00, true, true),
(2, 'QuickShop Express', '567 Market Street, San Francisco, CA 94105', 37.7749, -122.4194, '(415) 555-0102', 'info@quickshop.com', '07:00:00', '23:00:00', true, true, 4.99, 15.00, true, true);

-- Insert device
INSERT INTO devices (id, device_id, name, user_id, online, active, last_sync, mock_data_interval_seconds, mock_consumption_rate, created_at) VALUES
(1, 'FRIDGE-001', 'Kitchen Smart Fridge', 1, true, true, CURRENT_TIMESTAMP, 30, 0.05, CURRENT_TIMESTAMP);

-- Insert user-store relationships
INSERT INTO user_stores (id, user_id, store_id, priority, is_active, max_delivery_fee, max_distance_miles, notes, added_at) VALUES
(1, 1, 1, 1, true, 10.00, 5.0, 'Primary store selected during registration', CURRENT_TIMESTAMP),
(2, 1, 2, 2, true, 10.00, 5.0, 'Backup store option', CURRENT_TIMESTAMP);

-- Insert inventory items with various statuses (using H2-compatible date functions)
INSERT INTO inventory_items (id, device_id, grocery_item_id, quantity, threshold_quantity, status, added_at, last_updated, expiration_date) VALUES
(1, 1, 1, 0.3, 0.5, 'LOW', CURRENT_TIMESTAMP - INTERVAL '5' DAY, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '7' DAY),
(2, 1, 2, 0.0, 0.5, 'OUT_OF_STOCK', CURRENT_TIMESTAMP - INTERVAL '7' DAY, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '14' DAY),
(3, 1, 3, 4.0, 2.0, 'SUFFICIENT', CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '10' DAY),
(4, 1, 4, 0.5, 2.0, 'CRITICAL', CURRENT_TIMESTAMP - INTERVAL '3' DAY, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '3' DAY),
(5, 1, 5, 5.0, 3.0, 'SUFFICIENT', CURRENT_TIMESTAMP - INTERVAL '1' DAY, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '14' DAY),
(6, 1, 6, 0.8, 1.0, 'LOW', CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '5' DAY),
(7, 1, 7, 2.0, 1.0, 'SUFFICIENT', CURRENT_TIMESTAMP - INTERVAL '1' DAY, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '7' DAY),
(8, 1, 8, 0.4, 1.0, 'LOW', CURRENT_TIMESTAMP - INTERVAL '4' DAY, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '14' DAY);

-- Insert sample orders (using H2-compatible date functions)
-- Past delivered order
INSERT INTO orders (id, order_number, user_id, store_id, status, subtotal, delivery_fee, tax, total_amount, estimated_total, final_total, delivery_address, scheduled_delivery_time, actual_delivery_time, submitted_at, external_order_id, notification_sent, tracking_number, delivery_person_name, delivery_person_phone, is_paid, created_at, updated_at) VALUES
(1, 'ORD-1691234567890', 1, 1, 'DELIVERED', 45.67, 5.99, 3.65, 55.31, 55.31, 55.31, '123 Main Street, Apt 4B, San Francisco, CA 94105', CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP - INTERVAL '2' DAY + INTERVAL '30' MINUTE, CURRENT_TIMESTAMP - INTERVAL '3' DAY, 'EXT-1-1691061367890', true, 'TRK123456789', 'Mike Johnson', '+1-555-0123', true, CURRENT_TIMESTAMP - INTERVAL '3' DAY, CURRENT_TIMESTAMP);

-- Draft order awaiting approval
INSERT INTO orders (id, order_number, user_id, store_id, status, subtotal, delivery_fee, tax, total_amount, estimated_total, draft_created_at, delivery_address, notification_sent, delivery_instructions, is_paid, created_at, updated_at) VALUES
(2, 'DRAFT-1691241567890', 1, 1, 'DRAFT', 23.45, 5.99, 1.88, 31.32, 31.32, CURRENT_TIMESTAMP - INTERVAL '2' HOUR, '123 Main Street, Apt 4B, San Francisco, CA 94105', true, 'Please leave at front door if no answer', false, CURRENT_TIMESTAMP - INTERVAL '2' HOUR, CURRENT_TIMESTAMP);

-- Insert order items for past order
INSERT INTO order_items (id, order_id, grocery_item_id, quantity, price, price_at_creation, current_price, original_quantity, price_changed, quantity_modified, user_removed, subtotal, notes) VALUES
(1, 1, 1, 1.0, 4.99, 4.99, 4.99, 1.0, false, false, false, 4.99, 'Fresh organic milk'),
(2, 1, 2, 2.0, 5.99, 5.99, 5.99, 2.0, false, false, false, 11.98, null),
(3, 1, 4, 3.0, 0.59, 0.59, 0.59, 3.0, false, false, false, 1.77, null),
(4, 1, 8, 1.0, 4.49, 4.49, 4.49, 1.0, false, false, false, 4.49, null);

-- Insert order items for draft order
INSERT INTO order_items (id, order_id, grocery_item_id, quantity, price, price_at_creation, current_price, original_quantity, price_changed, quantity_modified, user_removed, subtotal) VALUES
(5, 2, 1, 1.0, 5.29, 4.99, 5.29, 1.0, true, false, false, 5.29),
(6, 2, 2, 1.0, 5.99, 5.99, 5.99, 2.0, false, true, false, 5.99),
(7, 2, 6, 2.0, 8.99, 8.99, 8.99, 2.0, false, false, false, 17.98);