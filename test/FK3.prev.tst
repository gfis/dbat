<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>DDL of a Table/View</title>
<link rel="stylesheet" type="text/css" href="spec/stylesheet.css" />
</head><body>

    
    <!--Gibt die DDL-Beschreibung einer Tabelle oder View aus-->
    <!--Shows the DDL Description of a Table or View-->

    <h3><a href="index.html">Dbat</a> - DDL of Table/View(s) <em>product_order%</em></h3>
    <p></p>
    
    <form method="get" action="servlet?spec=describe"><input name="spec" type="hidden" value="describe" />

        Name: <input name="table" maxsize="64" size="32" init="spec_ind" value="product_order"></input>% <br />
        <input type="submit" value="Show DDL"></input> 
          -> <a href="servlet?spec=describe&amp;mode=spec&amp;table=product_order&amp;dummy=Dbat+Spec.">Dbat Spec.</a>
          -> <a href="servlet?spec=describe&amp;mode=dbiv&amp;table=product_order&amp;dummy=Dbiv+Spec.">Dbiv Spec.</a>
    </form>
    
    
<table><tr><td><pre>-- MySQL with MySQL-AB JDBC Driver
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
</pre></td></tr></table>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/describe.xml" type="text/plain">describe</a>,
<a target="_blank" href="servlet?&amp;mode=xlsx&amp;spec=describe&amp;table=product_order&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=describe&amp;table=product_order&amp;lang=en">more</a>

</body></html>
