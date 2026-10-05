CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    isbn VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    total_quantity INT NOT NULL DEFAULT 0,
    available_quantity INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT chk_books_total_quantity CHECK (total_quantity >= 0),
    CONSTRAINT chk_books_available_quantity CHECK (available_quantity >= 0 AND available_quantity <= total_quantity),
    CONSTRAINT fk_book_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE borrowings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    returned_date DATE NULL,
    status VARCHAR(50) NOT NULL,
    total_fine DECIMAL(12, 2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_borrowing_member FOREIGN KEY (member_id) REFERENCES members (id)
);

CREATE TABLE borrowing_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    borrowing_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    returned_quantity INT NOT NULL DEFAULT 0,
    fine_amount DECIMAL(12, 2) NOT NULL DEFAULT 0,
    CONSTRAINT chk_detail_quantity CHECK (quantity > 0),
    CONSTRAINT chk_detail_returned_quantity CHECK (returned_quantity >= 0 AND returned_quantity <= quantity),
    CONSTRAINT fk_detail_borrowing FOREIGN KEY (borrowing_id) REFERENCES borrowings (id),
    CONSTRAINT fk_detail_book FOREIGN KEY (book_id) REFERENCES books (id)
);

INSERT INTO categories (name, description, active) VALUES
('Java', 'Sach lap trinh Java va Spring', true),
('Database', 'Sach ve SQL va thiet ke co so du lieu', true),
('Web', 'Sach HTML CSS JavaScript va ung dung web', true),
('Software Engineering', 'Sach ve kien truc va quy trinh phan mem', true),
('Fiction', 'Sach van hoc va tieu thuyet', true);

INSERT INTO books (category_id, isbn, title, author, total_quantity, available_quantity, status) VALUES
(1, '9780134685991', 'Effective Java', 'Joshua Bloch', 5, 3, 'ACTIVE'),
(1, '9781617297571', 'Spring in Action', 'Craig Walls', 6, 5, 'ACTIVE'),
(1, '9781492078005', 'Java Cookbook', 'Ian Darwin', 4, 4, 'ACTIVE'),
(1, '9780135166307', 'Core Java Volume I', 'Cay Horstmann', 4, 4, 'ACTIVE'),
(2, '9780321127426', 'Patterns of Enterprise Application Architecture', 'Martin Fowler', 3, 2, 'ACTIVE'),
(2, '9781449373320', 'Designing Data Intensive Applications', 'Martin Kleppmann', 5, 4, 'ACTIVE'),
(2, '9780596523060', 'SQL Cookbook', 'Anthony Molinaro', 4, 4, 'ACTIVE'),
(2, '9781492057611', 'Database Internals', 'Alex Petrov', 3, 3, 'ACTIVE'),
(3, '9781492056355', 'Learning React', 'Alex Banks', 5, 5, 'ACTIVE'),
(3, '9781491952023', 'JavaScript The Definitive Guide', 'David Flanagan', 5, 5, 'ACTIVE'),
(3, '9781617297632', 'CSS in Depth', 'Keith Grant', 4, 4, 'ACTIVE'),
(3, '9781492045526', 'Flask Web Development', 'Miguel Grinberg', 2, 2, 'INACTIVE'),
(4, '9780132350884', 'Clean Code', 'Robert C Martin', 6, 5, 'ACTIVE'),
(4, '9780201616224', 'The Pragmatic Programmer', 'Andrew Hunt', 6, 6, 'ACTIVE'),
(4, '9780134494166', 'Clean Architecture', 'Robert C Martin', 5, 5, 'ACTIVE'),
(4, '9780321601919', 'Continuous Delivery', 'Jez Humble', 3, 3, 'ACTIVE'),
(5, '9780141439600', 'A Tale of Two Cities', 'Charles Dickens', 5, 5, 'ACTIVE'),
(5, '9780141439518', 'Pride and Prejudice', 'Jane Austen', 5, 5, 'ACTIVE'),
(5, '9780451524935', '1984', 'George Orwell', 5, 5, 'ACTIVE'),
(5, '9780061120084', 'To Kill a Mockingbird', 'Harper Lee', 5, 5, 'ACTIVE');

INSERT INTO members (full_name, email, phone, status, created_at) VALUES
('Nguyen An', 'an.nguyen@example.com', '0901000001', 'ACTIVE', CURRENT_TIMESTAMP),
('Tran Binh', 'binh.tran@example.com', '0901000002', 'ACTIVE', CURRENT_TIMESTAMP),
('Le Chi', 'chi.le@example.com', '0901000003', 'BLOCKED', CURRENT_TIMESTAMP),
('Pham Dung', 'dung.pham@example.com', '0901000004', 'ACTIVE', CURRENT_TIMESTAMP),
('Hoang Em', 'em.hoang@example.com', '0901000005', 'ACTIVE', CURRENT_TIMESTAMP),
('Vo Giang', 'giang.vo@example.com', '0901000006', 'ACTIVE', CURRENT_TIMESTAMP),
('Dang Hoa', 'hoa.dang@example.com', '0901000007', 'BLOCKED', CURRENT_TIMESTAMP),
('Bui Khanh', 'khanh.bui@example.com', '0901000008', 'ACTIVE', CURRENT_TIMESTAMP),
('Do Linh', 'linh.do@example.com', '0901000009', 'ACTIVE', CURRENT_TIMESTAMP),
('Mai Minh', 'minh.mai@example.com', '0901000010', 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO borrowings (member_id, borrow_date, due_date, returned_date, status, total_fine) VALUES
(1, DATE_SUB(CURRENT_DATE, INTERVAL 20 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY), NULL, 'OVERDUE', 0),
(2, DATE_SUB(CURRENT_DATE, INTERVAL 4 DAY), DATE_ADD(CURRENT_DATE, INTERVAL 10 DAY), NULL, 'BORROWED', 0),
(4, DATE_SUB(CURRENT_DATE, INTERVAL 18 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 4 DAY), CURRENT_DATE, 'RETURNED', 40000),
(5, DATE_SUB(CURRENT_DATE, INTERVAL 8 DAY), DATE_ADD(CURRENT_DATE, INTERVAL 6 DAY), NULL, 'PARTIALLY_RETURNED', 0),
(6, DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), DATE_ADD(CURRENT_DATE, INTERVAL 13 DAY), NULL, 'BORROWED', 0);

INSERT INTO borrowing_details (borrowing_id, book_id, quantity, returned_quantity, fine_amount) VALUES
(1, 1, 2, 0, 0),
(1, 5, 1, 0, 0),
(2, 2, 1, 0, 0),
(3, 13, 2, 2, 40000),
(4, 6, 1, 1, 0),
(4, 1, 1, 0, 0),
(5, 19, 1, 0, 0);
