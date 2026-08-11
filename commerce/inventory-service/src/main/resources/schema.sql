CREATE TABLE IF NOT EXISTS inventories (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id BIGINT,
    quantity INTEGER,
    reserved_quantity INTEGER,
    available_quantity INTEGER,
    version BIGINT
);