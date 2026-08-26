INSERT INTO products (id, name, price, stock)
SELECT 'hoodie', 'Pipeline Hoodie', 49.99, 20
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 'hoodie');
INSERT INTO products (id, name, price, stock)
SELECT 'mug', 'Build Green Mug', 14.50, 35
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 'mug');
INSERT INTO products (id, name, price, stock)
SELECT 'sticker', 'Ship It Sticker Pack', 6.00, 100
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 'sticker');
INSERT INTO products (id, name, price, stock)
SELECT 'keyboard', 'Deploy Mechanical Keyboard', 89.00, 8
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 'keyboard');
