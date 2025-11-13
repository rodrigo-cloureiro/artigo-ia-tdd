DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS books;

CREATE TABLE books (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255) NOT NULL,
                       isbn VARCHAR(13) NOT NULL UNIQUE
);

CREATE TABLE loans (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       book_id BIGINT NOT NULL,
                       borrower VARCHAR(255) NOT NULL,
                       loan_date DATE NOT NULL,
                       due_date DATE NOT NULL,
                       return_date DATE,
                       fine_paid BOOLEAN DEFAULT FALSE,
                       CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES books(id)
);
