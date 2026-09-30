-- =============================================================================
-- V2__ledger_void_and_transfer.sql
-- Ajout : annulation de transaction (VOID) + transfert interne (group ID)
-- =============================================================================

-- Champs VOID sur ledger_transactions (jamais de suppression physique)
ALTER TABLE ledger_transactions
    ADD COLUMN IF NOT EXISTS is_void       BOOLEAN      NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS void_reason   VARCHAR(255) NULL,
    ADD COLUMN IF NOT EXISTS voided_at     TIMESTAMPTZ  NULL,
    ADD COLUMN IF NOT EXISTS voided_by     UUID         NULL;

-- Lien pour les transferts internes (les deux legs partagent le même transfer_group_id)
ALTER TABLE ledger_transactions
    ADD COLUMN IF NOT EXISTS transfer_group_id UUID NULL;

-- Contrainte FK sur voided_by -> users (optionnelle, ON DELETE SET NULL)
ALTER TABLE ledger_transactions
    ADD CONSTRAINT fk_ledger_txns_voided_by
        FOREIGN KEY (voided_by) REFERENCES users(id) ON DELETE SET NULL;

-- Index utiles pour les requêtes de filtrage VOID / transfert
CREATE INDEX IF NOT EXISTS idx_ledger_txns_is_void         ON ledger_transactions(is_void);
CREATE INDEX IF NOT EXISTS idx_ledger_txns_transfer_group  ON ledger_transactions(transfer_group_id);
CREATE INDEX IF NOT EXISTS idx_ledger_txns_voided_at       ON ledger_transactions(voided_at);
