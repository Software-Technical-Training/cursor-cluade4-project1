-- Grocery Automation Backend - Initial Data Setup for PostgreSQL
-- This script initializes the database with mock data for development and testing

-- Insert grocery items catalog (ignore if already exists)
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
(10, 'Pasta', 'Pantry', 'box', '123456789010', 'PST-PEN-16', 'Italian Select', 2.0, true, 'Penne pasta', null, 'Wheat, Gluten', 131)
ON CONFLICT (id) DO NOTHING;

-- Insert test user
INSERT INTO users (id, name, email, password, phone, address, latitude, longitude, active, created_at, updated_at) VALUES
(1, 'John Doe', 'john.doe@example.com', 'Test@123', '+1234567890', '123 Main Street, Apt 4B, San Francisco, CA 94105', 37.7749, -122.4194, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Insert stores
INSERT INTO stores (id, name, address, latitude, longitude, phone, email, opening_time, closing_time, has_delivery, has_pickup, delivery_fee, minimum_order_amount, active, accepting_orders) VALUES
(1, 'Fresh Mart Downtown', '399 4th Street, San Francisco, CA 94107', 37.7816, -122.3988, '(415) 555-0101', 'downtown@freshmart.com', '07:00:00', '22:00:00', true, true, 5.99, 25.00, true, true),
(2, 'QuickShop Express', '567 Market Street, San Francisco, CA 94105', 37.7749, -122.4194, '(415) 555-0102', 'info@quickshop.com', '07:00:00', '23:00:00', true, true, 4.99, 15.00, true, true)
ON CONFLICT (id) DO NOTHING;

-- Insert device
INSERT INTO devices (id, device_id, name, user_id, online, active, last_sync, mock_data_interval_seconds, mock_consumption_rate, created_at) VALUES
(1, 'FRIDGE-001', 'Kitchen Smart Fridge', 1, true, true, CURRENT_TIMESTAMP, 30, 0.05, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Insert user-store relationships
INSERT INTO user_stores (id, user_id, store_id, priority, is_active, max_delivery_fee, max_distance_miles, notes, added_at) VALUES
(1, 1, 1, 1, true, 10.00, 5.0, 'Primary store selected during registration', CURRENT_TIMESTAMP),
(2, 1, 2, 2, true, 10.00, 5.0, 'Backup store option', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Insert inventory items with various statuses (using PostgreSQL interval syntax)
INSERT INTO inventory_items (id, device_id, grocery_item_id, quantity, threshold_quantity, status, added_at, last_updated, expiration_date) VALUES
(1, 1, 1, 0.3, 0.5, 'LOW', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '7 days'),
(2, 1, 2, 0.0, 0.5, 'OUT_OF_STOCK', CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '14 days'),
(3, 1, 3, 4.0, 2.0, 'SUFFICIENT', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '10 days'),
(4, 1, 4, 0.5, 2.0, 'CRITICAL', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '3 days'),
(5, 1, 5, 5.0, 3.0, 'SUFFICIENT', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '14 days'),
(6, 1, 6, 0.8, 1.0, 'LOW', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '5 days'),
(7, 1, 7, 2.0, 1.0, 'SUFFICIENT', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '7 days'),
(8, 1, 8, 0.4, 1.0, 'LOW', CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '14 days')
ON CONFLICT (id) DO NOTHING;

-- Insert sample orders
-- Past delivered order
INSERT INTO orders (id, order_number, user_id, store_id, status, subtotal, delivery_fee, tax, total_amount, estimated_total, final_total, delivery_address, scheduled_delivery_time, actual_delivery_time, submitted_at, external_order_id, notification_sent, tracking_number, delivery_person_name, delivery_person_phone, created_at, updated_at) VALUES
(1, 'ORD-1691234567890', 1, 1, 'DELIVERED', 45.67, 5.99, 3.65, 55.31, 55.31, 55.31, '123 Main Street, Apt 4B, San Francisco, CA 94105', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days' + INTERVAL '30 minutes', CURRENT_TIMESTAMP - INTERVAL '3 days', 'EXT-1-1691061367890', true, 'TRK123456789', 'Mike Johnson', '+1-555-0123', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Draft order awaiting approval
INSERT INTO orders (id, order_number, user_id, store_id, status, subtotal, delivery_fee, tax, total_amount, estimated_total, draft_created_at, delivery_address, notification_sent, delivery_instructions, created_at, updated_at) VALUES
(2, 'DRAFT-1691241567890', 1, 1, 'DRAFT', 23.45, 5.99, 1.88, 31.32, 31.32, CURRENT_TIMESTAMP - INTERVAL '2 hours', '123 Main Street, Apt 4B, San Francisco, CA 94105', true, 'Please leave at front door if no answer', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Insert order items for past order
INSERT INTO order_items (id, order_id, grocery_item_id, quantity, price, price_at_creation, current_price, original_quantity, notes, created_at, updated_at) VALUES
(1, 1, 1, 1.0, 4.99, 4.99, 4.99, 1.0, 'Fresh organic milk', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP),
(2, 1, 2, 2.0, 5.99, 5.99, 5.99, 2.0, null, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP),
(3, 1, 4, 3.0, 0.59, 0.59, 0.59, 3.0, null, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP),
(4, 1, 8, 1.0, 4.49, 4.49, 4.49, 1.0, null, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Insert order items for draft order
INSERT INTO order_items (id, order_id, grocery_item_id, quantity, price, price_at_creation, current_price, original_quantity, price_changed, quantity_modified, created_at, updated_at) VALUES
(5, 2, 1, 1.0, 5.29, 4.99, 5.29, 1.0, true, false, CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP),
(6, 2, 2, 1.0, 5.99, 5.99, 5.99, 2.0, false, true, CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP),
(7, 2, 6, 2.0, 8.99, 8.99, 8.99, 2.0, false, false, CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Reset sequences to continue from inserted IDs
SELECT setval('grocery_items_id_seq', (SELECT MAX(id) FROM grocery_items));
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('stores_id_seq', (SELECT MAX(id) FROM stores));
SELECT setval('devices_id_seq', (SELECT MAX(id) FROM devices));
SELECT setval('user_stores_id_seq', (SELECT MAX(id) FROM user_stores));
SELECT setval('inventory_items_id_seq', (SELECT MAX(id) FROM inventory_items));
SELECT setval('orders_id_seq', (SELECT MAX(id) FROM orders));
SELECT setval('order_items_id_seq', (SELECT MAX(id) FROM order_items));