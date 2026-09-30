ALTER TABLE ledger_transactions
    DROP CONSTRAINT IF EXISTS chk_ledger_txns_type;

ALTER TABLE ledger_transactions
    ADD CONSTRAINT chk_ledger_txns_type CHECK (type IN (
        'INITIAL_BALANCE',
        'RECEIVE',
        'SEND',
        'ATM_WITHDRAWAL',
        'ONLINE_PAYMENT',
        'PHONE_RECHARGE',
        'CARD_PAYMENT',
        'P2P_BUY',
        'P2P_SELL',
        'FEE',
        'INTERNAL_TRANSFER',
        'ADJUSTMENT'
    ));