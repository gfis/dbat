-- SQL generated by Dbat on yyyy-mm-dd hh:mm:ss
-- MySQL with MySQL-AB JDBC Driver
DROP   TABLE product_order;
CREATE TABLE product_order
	( numb	INT NOT NULL
	, product_category	INT NOT NULL
	, product_id	CHAR(8) NOT NULL
	, customer_id	CHAR(8) NOT NULL
	, CONSTRAINT PK29 PRIMARY KEY (numb)
	);
CREATE UNIQUE INDEX FK2 ON product_order
	( customer_id	ASC
	);
CREATE INDEX UK3 ON product_order
	( product_id	ASC
	, product_category	ASC
	);
ALTER TABLE product_order
	ADD CONSTRAINT FK1 FOREIGN KEY (product_id, product_category)
		REFERENCES product(id, category)
		ON UPDATE CASCADE
		ON DELETE NO ACTION
	;
ALTER TABLE product_order
	ADD CONSTRAINT FK2 FOREIGN KEY (customer_id)
		REFERENCES customer(id)
		ON UPDATE NO ACTION
		ON DELETE NO ACTION
	;
COMMIT;
