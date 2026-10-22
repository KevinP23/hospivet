ALTER TABLE movimiento_inventario ADD COLUMN stock_anterior BIGINT;
ALTER TABLE movimiento_inventario ADD COLUMN stock_resultante BIGINT;

-- Los movimientos previos guardaban ambas existencias en el motivo: 'Stock actualizado de X a Y'.
UPDATE movimiento_inventario
SET stock_anterior = CAST(SUBSTRING(motivo FROM 22 FOR POSITION(' a ' IN motivo) - 22) AS BIGINT),
    stock_resultante = CAST(SUBSTRING(motivo FROM POSITION(' a ' IN motivo) + 3) AS BIGINT)
WHERE motivo LIKE 'Stock actualizado de % a %';

ALTER TABLE movimiento_inventario ALTER COLUMN stock_anterior SET NOT NULL;
ALTER TABLE movimiento_inventario ALTER COLUMN stock_resultante SET NOT NULL;

ALTER TABLE movimiento_inventario
    ADD CONSTRAINT ck_movimiento_stock_anterior CHECK (stock_anterior >= 0);
ALTER TABLE movimiento_inventario
    ADD CONSTRAINT ck_movimiento_stock_resultante CHECK (stock_resultante >= 0);
