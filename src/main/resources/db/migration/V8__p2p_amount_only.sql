ALTER TABLE p2p_orders
    ALTER COLUMN quantity_usdt DROP NOT NULL,
    ALTER COLUMN unit_price_tnd DROP NOT NULL;