-- Remove itens com o tipo BOOK (como é um ENUM lógico no Java, se tiver registros no banco que não têm mais classe correspondente, a aplicação irá lançar erros)
DELETE FROM item_media WHERE media_type = 'BOOK';

-- Remove a coluna external_id
ALTER TABLE item_media DROP COLUMN external_id;

-- Substitui os índices simples pelo FULLTEXT INDEX
ALTER TABLE item_media DROP INDEX idx_title;
ALTER TABLE item_media DROP INDEX idx_author_director;
ALTER TABLE item_media ADD FULLTEXT INDEX idx_search (title, author_director);
