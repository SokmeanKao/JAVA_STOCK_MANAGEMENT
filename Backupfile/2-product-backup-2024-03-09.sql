DROP TABLE products;

CREATE TABLE if not EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    stock_quantity INTEGER NOT NULL,
    imported_date DATE NOT NULL
);
INSERT INTO products (name, unit_price, stock_quantity, imported_date) VALUES ('Widget A', 12.990000, 100, '2024-02-16');
INSERT INTO products (name, unit_price, stock_quantity, imported_date) VALUES ('Gadget B', 24.500000, 75, '2024-02-16');
INSERT INTO products (name, unit_price, stock_quantity, imported_date) VALUES ('Tool C', 9.990000, 150, '2024-02-16');
INSERT INTO products (name, unit_price, stock_quantity, imported_date) VALUES ('Device D', 49.990000, 50, '2024-02-16');
INSERT INTO products (name, unit_price, stock_quantity, imported_date) VALUES ('Appliance E', 89.990000, 25, '2024-02-16');
