ALTER TABLE cards
    ALTER COLUMN expiration_date TYPE VARCHAR(5)
    USING CASE
        WHEN expiration_date IS NULL THEN NULL
        ELSE TO_CHAR(expiration_date, 'MM/YY')
    END;