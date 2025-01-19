ALTER TABLE books
ADD CONSTRAINT fk_author_book FOREIGN KEY (author_id) 
REFERENCES authors (id);