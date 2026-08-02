CREATE TABLE IF NOT EXISTS orders (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_name VARCHAR,
    customer_email VARCHAR,
    status VARCHAR,
    status_details TEXT,
    created_at TIMESTAMP DEFAULT now(),

);

CREATE TABLE IF NOT EXISTS orders_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id BIGINT,
    product_name VARCHAR,
    quantity INTEGER,
    price NUMERIC,
    order_id BIGINT REFERENCES orders(id)
);