-- V4: Align schema with agent/plan.md (Extensions and precise naming)

-- 1. Rename description to synopsis
ALTER TABLE item_media RENAME COLUMN description TO synopsis;

-- 2. Change rating from DOUBLE to INT. Since it was DOUBLE in V1, let's modify it.
ALTER TABLE item_media MODIFY COLUMN rating INT;

-- 3. Add missing extension columns
ALTER TABLE item_media ADD COLUMN poster_url VARCHAR(500);
ALTER TABLE item_media ADD COLUMN external_id VARCHAR(50);
ALTER TABLE item_media ADD COLUMN comment TEXT;

-- 4. Add missing indices
CREATE INDEX idx_title ON item_media(title);
CREATE INDEX idx_author_director ON item_media(author_director);
