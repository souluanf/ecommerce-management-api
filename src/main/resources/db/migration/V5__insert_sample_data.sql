INSERT INTO users (id, email, password, role, created_at, updated_at) VALUES
('7d92f8ae-2259-43fc-92f8-ae225913fc86', 'joao.silva@email.com', '$2a$10$7j4ZNqKJ4W7vKkQ2r6Xh1.Iiq9K2JdJlL9eV7kF8QdX3KlS5WmE2i', 'USER', NOW(), NOW()),
('e3fafcfb-80f5-4921-80b8-2f31889d925d', 'maria.santos@email.com', '$2a$10$7j4ZNqKJ4W7vKkQ2r6Xh1.Iiq9K2JdJlL9eV7kF8QdX3KlS5WmE2i', 'USER', NOW(), NOW()),
('298c8205-c360-462a-8ce8-b836d601aa0e', 'carlos.pereira@email.com', '$2a$10$7j4ZNqKJ4W7vKkQ2r6Xh1.Iiq9K2JdJlL9eV7kF8QdX3KlS5WmE2i', 'USER', NOW(), NOW()),
('376a1dce-cf24-4b1e-b1a5-8425c06044b2', 'ana.oliveira@email.com', '$2a$10$7j4ZNqKJ4W7vKkQ2r6Xh1.Iiq9K2JdJlL9eV7kF8QdX3KlS5WmE2i', 'USER', NOW(), NOW()),
('802bc13c-f229-4d5f-906a-bb084de4db25', 'pedro.costa@email.com', '$2a$10$7j4ZNqKJ4W7vKkQ2r6Xh1.Iiq9K2JdJlL9eV7kF8QdX3KlS5WmE2i', 'USER', NOW(), NOW()),
('6e821abd-4709-4851-97b5-1e14469c6656', 'lucia.ferreira@email.com', '$2a$10$7j4ZNqKJ4W7vKkQ2r6Xh1.Iiq9K2JdJlL9eV7kF8QdX3KlS5WmE2i', 'USER', NOW(), NOW()),
('4e54cb57-7146-4152-9ebe-fb5c404163b5', 'ricardo.almeida@email.com', '$2a$10$7j4ZNqKJ4W7vKkQ2r6Xh1.Iiq9K2JdJlL9eV7kF8QdX3KlS5WmE2i', 'USER', NOW(), NOW()),
('b94fc9b6-7d8c-453a-8956-fd69eaee8417', 'fernanda.lima@email.com', '$2a$10$7j4ZNqKJ4W7vKkQ2r6Xh1.Iiq9K2JdJlL9eV7kF8QdX3KlS5WmE2i', 'USER', NOW(), NOW()),
('2af3e322-17ad-4404-b732-6bd8bba400de', 'roberto.souza@email.com', '$2a$10$7j4ZNqKJ4W7vKkQ2r6Xh1.Iiq9K2JdJlL9eV7kF8QdX3KlS5WmE2i', 'USER', NOW(), NOW()),
('bbbbcccc-dddd-eeee-ffff-gggggggggggg', 'patricia.rocha@email.com', '$2a$10$7j4ZNqKJ4W7vKkQ2r6Xh1.Iiq9K2JdJlL9eV7kF8QdX3KlS5WmE2i', 'USER', NOW(), NOW());

-- ============================================================================
-- PRODUCTS (20+ products across multiple categories)
-- ============================================================================

INSERT INTO products (id, name, description, price, category, stock_quantity, created_at, updated_at) VALUES 
-- Electronics
('cf918cf1-947c-4d93-bbb7-02aa6fd93199', 'Samsung Galaxy S24', 'Latest Samsung smartphone with amazing camera and 5G', 899.99, 'ELECTRONICS', 50, NOW(), NOW()),
('c6507373-fe6e-42a4-b2d3-99727d1c0767', 'MacBook Pro M3', 'High performance laptop for professionals', 2999.99, 'ELECTRONICS', 15, NOW(), NOW()),
('44b19a3c-5c21-4eb0-a759-f2a86214ba65', 'iPad Air', 'Perfect tablet for work and entertainment', 699.99, 'ELECTRONICS', 30, NOW(), NOW()),
('49fc79a5-ab56-4d36-9b1d-b007ce11b82d', 'AirPods Pro', 'Premium wireless earbuds with noise cancellation', 249.99, 'ELECTRONICS', 100, NOW(), NOW()),
('610ffc75-5ee6-47e5-9738-e69806ebd37d', 'Sony TV 55"', 'Smart TV with 4K resolution and HDR', 1299.99, 'ELECTRONICS', 20, NOW(), NOW()),
('2d56fb07-8991-4336-8bd4-1faebba29d2d', 'Nintendo Switch', 'Portable gaming console for all ages', 399.99, 'ELECTRONICS', 40, NOW(), NOW()),

('gggggggg-gggg-gggg-gggg-gggggggggggg', 'Nespresso Coffee Machine', 'Premium coffee maker with milk frother', 299.99, 'KITCHEN', 25, NOW(), NOW()),
('hhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh', 'KitchenAid Stand Mixer', 'Professional stand mixer for baking', 449.99, 'KITCHEN', 15, NOW(), NOW()),
('iiiiiiii-iiii-iiii-iiii-iiiiiiiiiiii', 'Dyson V15 Vacuum', 'Cordless vacuum with laser detection', 749.99, 'HOME', 18, NOW(), NOW()),
('jjjjjjjj-jjjj-jjjj-jjjj-jjjjjjjjjjjj', 'Air Fryer Philips', 'Healthy cooking with rapid air technology', 199.99, 'KITCHEN', 35, NOW(), NOW()),


('kkkkkkkk-kkkk-kkkk-kkkk-kkkkkkkkkkkk', 'Nike Air Max', 'Premium running shoes for athletes', 159.99, 'SPORTS', 80, NOW(), NOW()),
('llllllll-llll-llll-llll-llllllllllll', 'Adidas Ultra Boost', 'Energy return running shoes', 189.99, 'SPORTS', 60, NOW(), NOW()),
('mmmmmmmm-mmmm-mmmm-mmmm-mmmmmmmmmmmm', 'Yoga Mat Premium', 'Non-slip yoga mat for home practice', 49.99, 'SPORTS', 120, NOW(), NOW()),
('nnnnnnnn-nnnn-nnnn-nnnn-nnnnnnnnnnnn', 'Levi''s Jeans 501', 'Classic straight leg jeans', 89.99, 'FASHION', 90, NOW(), NOW()),
('oooooooo-oooo-oooo-oooo-oooooooooooo', 'Ralph Lauren Polo', 'Classic polo shirt 100% cotton', 79.99, 'FASHION', 75, NOW(), NOW()),

('pppppppp-pppp-pppp-pppp-pppppppppppp', 'Clean Code Book', 'Essential reading for software developers', 45.99, 'BOOKS', 50, NOW(), NOW()),
('qqqqqqqq-qqqq-qqqq-qqqq-qqqqqqqqqqqq', 'Java Programming Course', 'Complete Java programming online course', 199.99, 'EDUCATION', 999, NOW(), NOW()),

('rrrrrrrr-rrrr-rrrr-rrrr-rrrrrrrrrrrr', 'Skincare Set Neutrogena', 'Complete skincare routine for healthy skin', 89.99, 'BEAUTY', 45, NOW(), NOW()),
('ssssssss-ssss-ssss-ssss-ssssssssssss', 'Protein Whey Premium', 'High quality protein powder for athletes', 59.99, 'HEALTH', 60, NOW(), NOW());


INSERT INTO orders (id, user_id, status, total_amount, created_at, updated_at) VALUES 
('1477be7e-e521-428c-8ed5-6e928722c261', '7d92f8ae-2259-43fc-92f8-ae225913fc86', 'PAID', 1149.98, NOW(), NOW()),
('5dc3c972-5edd-4d86-b695-2578b05ab2cd', '7d92f8ae-2259-43fc-92f8-ae225913fc86', 'PAID', 899.99, NOW(), NOW()),
('02b0e4ee-1a55-4277-b83e-93f82c6e5d6d', '7d92f8ae-2259-43fc-92f8-ae225913fc86', 'PAID', 749.99, NOW(), NOW()),
('7fa4780a-98ef-421d-86f1-87bff82a30eb', '7d92f8ae-2259-43fc-92f8-ae225913fc86', 'PAID', 449.99, NOW(), NOW()),
('aba80420-00c2-464b-a2c6-ff518438fafc', '7d92f8ae-2259-43fc-92f8-ae225913fc86', 'PAID', 299.99, NOW(), NOW()),
('6cd0f78e-9330-4513-9ddb-552fbb566e28', '7d92f8ae-2259-43fc-92f8-ae225913fc86', 'PAID', 189.99, NOW(), NOW()),
('e581dab9-4723-49c5-ac91-76e298c41f71', '7d92f8ae-2259-43fc-92f8-ae225913fc86', 'PAID', 159.99, NOW(), NOW()),
('529b455b-ec28-468c-b36a-2d4e2195a455', '7d92f8ae-2259-43fc-92f8-ae225913fc86', 'PENDING', 399.99, NOW(), NOW());

INSERT INTO orders (id, user_id, status, total_amount, created_at, updated_at) VALUES
('5da35460-e98c-4645-a94d-3ae1ea1c22a5', 'e3fafcfb-80f5-4921-80b8-2f31889d925d', 'PAID', 2999.99, NOW(), NOW()),
('7bbd3681-560d-4a99-b175-a8ba9521404d', 'e3fafcfb-80f5-4921-80b8-2f31889d925d', 'PAID', 699.99, NOW(), NOW()),
('42811af6-fa2f-4ac8-b3e4-aeeeb4fe9b0a', 'e3fafcfb-80f5-4921-80b8-2f31889d925d', 'PAID', 249.99, NOW(), NOW()),
('9e632742-54f0-42d4-bdb9-25097a63b58d', 'e3fafcfb-80f5-4921-80b8-2f31889d925d', 'PAID', 1299.99, NOW(), NOW()),
('6571a204-2258-45dc-b4cc-085c04b2b922', 'e3fafcfb-80f5-4921-80b8-2f31889d925d', 'PAID', 89.99, NOW(), NOW()),
('77798f3a-1079-4418-81db-9263fce88676', 'e3fafcfb-80f5-4921-80b8-2f31889d925d', 'CANCELLED', 199.99, NOW(), NOW());

INSERT INTO orders (id, user_id, status, total_amount, created_at, updated_at) VALUES
('c898d975-f627-45f5-83fd-a6e4f2f63a04', '298c8205-c360-462a-8ce8-b836d601aa0e', 'PAID', 399.99, NOW(), NOW()),
('cc9eb864-511a-4fb7-bbf6-aef664e19cf4', '298c8205-c360-462a-8ce8-b836d601aa0e', 'PAID', 449.99, NOW(), NOW()),
('4d2ef657-f2ae-45eb-8ea9-a8d32269b5d2', '298c8205-c360-462a-8ce8-b836d601aa0e', 'PAID', 749.99, NOW(), NOW()),
('c919c45a-149b-41bf-8be7-7b95d9236494', '298c8205-c360-462a-8ce8-b836d601aa0e', 'PAID', 299.99, NOW(), NOW()),
('878c024d-78c3-439c-9cca-0af95f8ca439', '298c8205-c360-462a-8ce8-b836d601aa0e', 'PAID', 189.99, NOW(), NOW());

INSERT INTO orders (id, user_id, status, total_amount, created_at, updated_at) VALUES
('53a5e62e-dc2f-4a44-bdc9-2e43e49170dc', '376a1dce-cf24-4b1e-b1a5-8425c06044b2', 'PAID', 899.99, NOW(), NOW()),
('fdb9ec40-d7e7-4969-9183-9170073fa0b0', '376a1dce-cf24-4b1e-b1a5-8425c06044b2', 'PAID', 449.99, NOW(), NOW()),
('18c954c4-c669-4f70-ab15-bf16835d19e4', '376a1dce-cf24-4b1e-b1a5-8425c06044b2', 'PAID', 249.99, NOW(), NOW()),
('802ac3b5-e482-4911-91fb-76a3ad1fc048', '376a1dce-cf24-4b1e-b1a5-8425c06044b2', 'PAID', 199.99, NOW(), NOW());

INSERT INTO orders (id, user_id, status, total_amount, created_at, updated_at) VALUES
('560289c7-21e4-42d2-99c7-e86be9c667ca', '802bc13c-f229-4d5f-906a-bb084de4db25', 'PAID', 699.99, NOW(), NOW()),
('bed3d063-3ea2-42ab-9415-00aa348ee290', '802bc13c-f229-4d5f-906a-bb084de4db25', 'PAID', 299.99, NOW(), NOW()),
('792ad542-8306-4565-a95a-941ee57b41fe', '802bc13c-f229-4d5f-906a-bb084de4db25', 'PAID', 159.99, NOW(), NOW());

INSERT INTO orders (id, user_id, status, total_amount, created_at, updated_at) VALUES
('2e8ca2f7-334c-461f-8e1a-ce36818106ed', '6e821abd-4709-4851-97b5-1e14469c6656', 'PAID', 89.99, NOW(), NOW()),
('99c2cf64-b746-408c-a619-70ef091aa011', '6e821abd-4709-4851-97b5-1e14469c6656', 'PAID', 45.99, NOW(), NOW()),
('9860f0a3-f68c-47a7-8128-139e0a23c4c2', '4e54cb57-7146-4152-9ebe-fb5c404163b5', 'PAID', 199.99, NOW(), NOW()),
('1827c4f3-63be-42ba-b7ee-a8ed3274d747', 'b94fc9b6-7d8c-453a-8956-fd69eaee8417', 'PAID', 79.99, NOW(), NOW()),
('24f7dc0e-9330-464a-8139-b7f4f641e584', '2af3e322-17ad-4404-b732-6bd8bba400de', 'PAID', 249.99, NOW(), NOW()),
('41e65d4e-59c0-4c09-a9a9-52a62748b322', 'bbbbcccc-dddd-eeee-ffff-gggggggggggg', 'CANCELLED', 399.99, NOW(), NOW());

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES 
('i0010101-0101-0101-0101-010101010101', '1477be7e-e521-428c-8ed5-6e928722c261', 'cf918cf1-947c-4d93-bbb7-02aa6fd93199', 'Samsung Galaxy S24', 1, 899.99, 899.99),
('i0020101-0101-0101-0101-010101010101', '1477be7e-e521-428c-8ed5-6e928722c261', '49fc79a5-ab56-4d36-9b1d-b007ce11b82d', 'AirPods Pro', 1, 249.99, 249.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0030202-0202-0202-0202-020202020202', '5dc3c972-5edd-4d86-b695-2578b05ab2cd', 'cf918cf1-947c-4d93-bbb7-02aa6fd93199', 'Samsung Galaxy S24', 1, 899.99, 899.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0040303-0303-0303-0303-030303030303', '02b0e4ee-1a55-4277-b83e-93f82c6e5d6d', 'iiiiiiii-iiii-iiii-iiii-iiiiiiiiiiii', 'Dyson V15 Vacuum', 1, 749.99, 749.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0050404-0404-0404-0404-040404040404', '7fa4780a-98ef-421d-86f1-87bff82a30eb', 'hhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh', 'KitchenAid Stand Mixer', 1, 449.99, 449.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0060505-0505-0505-0505-050505050505', 'aba80420-00c2-464b-a2c6-ff518438fafc', 'gggggggg-gggg-gggg-gggg-gggggggggggg', 'Nespresso Coffee Machine', 1, 299.99, 299.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0070606-0606-0606-0606-060606060606', '6cd0f78e-9330-4513-9ddb-552fbb566e28', 'llllllll-llll-llll-llll-llllllllllll', 'Adidas Ultra Boost', 1, 189.99, 189.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0080707-0707-0707-0707-070707070707', 'e581dab9-4723-49c5-ac91-76e298c41f71', 'kkkkkkkk-kkkk-kkkk-kkkk-kkkkkkkkkkkk', 'Nike Air Max', 1, 159.99, 159.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0090808-0808-0808-0808-080808080808', '529b455b-ec28-468c-b36a-2d4e2195a455', '2d56fb07-8991-4336-8bd4-1faebba29d2d', 'Nintendo Switch', 1, 399.99, 399.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0100909-0909-0909-0909-090909090909', '5da35460-e98c-4645-a94d-3ae1ea1c22a5', 'c6507373-fe6e-42a4-b2d3-99727d1c0767', 'MacBook Pro M3', 1, 2999.99, 2999.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0111010-1010-1010-1010-101010101010', '7bbd3681-560d-4a99-b175-a8ba9521404d', '44b19a3c-5c21-4eb0-a759-f2a86214ba65', 'iPad Air', 1, 699.99, 699.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0121111-2222-3333-4444-555555555555', '42811af6-fa2f-4ac8-b3e4-aeeeb4fe9b0a', '49fc79a5-ab56-4d36-9b1d-b007ce11b82d', 'AirPods Pro', 1, 249.99, 249.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0131212-1212-1212-1212-121212121212', '9e632742-54f0-42d4-bdb9-25097a63b58d', '610ffc75-5ee6-47e5-9738-e69806ebd37d', 'Sony TV 55"', 1, 1299.99, 1299.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0141313-1313-1313-1313-131313131313', '6571a204-2258-45dc-b4cc-085c04b2b922', 'nnnnnnnn-nnnn-nnnn-nnnn-nnnnnnnnnnnn', 'Levi''s Jeans 501', 1, 89.99, 89.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0151414-1414-1414-1414-141414141414', '77798f3a-1079-4418-81db-9263fce88676', 'jjjjjjjj-jjjj-jjjj-jjjj-jjjjjjjjjjjj', 'Air Fryer Philips', 1, 199.99, 199.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0161515-1515-1515-1515-151515151515', 'c898d975-f627-45f5-83fd-a6e4f2f63a04', '2d56fb07-8991-4336-8bd4-1faebba29d2d', 'Nintendo Switch', 1, 399.99, 399.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0171616-1616-1616-1616-161616161616', 'cc9eb864-511a-4fb7-bbf6-aef664e19cf4', 'hhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh', 'KitchenAid Stand Mixer', 1, 449.99, 449.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0181717-1717-1717-1717-171717171717', '4d2ef657-f2ae-45eb-8ea9-a8d32269b5d2', 'iiiiiiii-iiii-iiii-iiii-iiiiiiiiiiii', 'Dyson V15 Vacuum', 1, 749.99, 749.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0191818-1818-1818-1818-181818181818', 'c919c45a-149b-41bf-8be7-7b95d9236494', 'gggggggg-gggg-gggg-gggg-gggggggggggg', 'Nespresso Coffee Machine', 1, 299.99, 299.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0201919-1919-1919-1919-191919191919', '878c024d-78c3-439c-9cca-0af95f8ca439', 'llllllll-llll-llll-llll-llllllllllll', 'Adidas Ultra Boost', 1, 189.99, 189.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0212020-2020-2020-2020-202020202020', '53a5e62e-dc2f-4a44-bdc9-2e43e49170dc', 'cf918cf1-947c-4d93-bbb7-02aa6fd93199', 'Samsung Galaxy S24', 1, 899.99, 899.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0222121-2121-2121-2121-212121212121', 'fdb9ec40-d7e7-4969-9183-9170073fa0b0', 'hhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh', 'KitchenAid Stand Mixer', 1, 449.99, 449.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0232222-3333-4444-5555-666666666666', '18c954c4-c669-4f70-ab15-bf16835d19e4', '49fc79a5-ab56-4d36-9b1d-b007ce11b82d', 'AirPods Pro', 1, 249.99, 249.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0242323-2323-2323-2323-232323232323', '802ac3b5-e482-4911-91fb-76a3ad1fc048', 'jjjjjjjj-jjjj-jjjj-jjjj-jjjjjjjjjjjj', 'Air Fryer Philips', 1, 199.99, 199.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0252424-2424-2424-2424-242424242424', '560289c7-21e4-42d2-99c7-e86be9c667ca', '44b19a3c-5c21-4eb0-a759-f2a86214ba65', 'iPad Air', 1, 699.99, 699.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0262525-2525-2525-2525-252525252525', 'bed3d063-3ea2-42ab-9415-00aa348ee290', 'gggggggg-gggg-gggg-gggg-gggggggggggg', 'Nespresso Coffee Machine', 1, 299.99, 299.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0272626-2626-2626-2626-262626262626', '792ad542-8306-4565-a95a-941ee57b41fe', 'kkkkkkkk-kkkk-kkkk-kkkk-kkkkkkkkkkkk', 'Nike Air Max', 1, 159.99, 159.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0282727-2727-2727-2727-272727272727', '2e8ca2f7-334c-461f-8e1a-ce36818106ed', 'nnnnnnnn-nnnn-nnnn-nnnn-nnnnnnnnnnnn', 'Levi''s Jeans 501', 1, 89.99, 89.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0292828-2828-2828-2828-282828282828', '99c2cf64-b746-408c-a619-70ef091aa011', 'pppppppp-pppp-pppp-pppp-pppppppppppp', 'Clean Code Book', 1, 45.99, 45.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0302929-2929-2929-2929-292929292929', '9860f0a3-f68c-47a7-8128-139e0a23c4c2', 'jjjjjjjj-jjjj-jjjj-jjjj-jjjjjjjjjjjj', 'Air Fryer Philips', 1, 199.99, 199.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0313030-3030-3030-3030-303030303030', '1827c4f3-63be-42ba-b7ee-a8ed3274d747', 'oooooooo-oooo-oooo-oooo-oooooooooooo', 'Ralph Lauren Polo', 1, 79.99, 79.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0323131-3131-3131-3131-313131313131', '24f7dc0e-9330-464a-8139-b7f4f641e584', '49fc79a5-ab56-4d36-9b1d-b007ce11b82d', 'AirPods Pro', 1, 249.99, 249.99);

INSERT INTO order_items (id, order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
('i0333232-3232-3232-3232-323232323232', '41e65d4e-59c0-4c09-a9a9-52a62748b322', '2d56fb07-8991-4336-8bd4-1faebba29d2d', 'Nintendo Switch', 1, 399.99, 399.99);