wget -q -O - "http://localhost:8080/dbat/servlet?spec=test.crud03&search_value_1=&search_value_2=Ritter&name=Teherba&family=Ritter&view=del"
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>test/crud03</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

	
	
	<h2><a href="servlet?spec=test.index">Test</a> Form - Relatives</h2>

		<!-- SQL:
SELECT name
, family
, gender
, birth
, place
, decease 
FROM relatives 
WHERE name = 'Teherba'
				and family = 'Ritter'
				and gender = ''''
:SQL -->
<table id="tab1"><!-- table_not_specified -->
</table>

		<form method="post" action="servlet?spec=test/crud03"><input name="spec" type="hidden" value="test/crud03" />

			<input name="view" type="hidden" value="del2"></input>
			<input name="opcode" type="hidden" value="del"></input>
			<input name="search_crit_1" type="hidden" init="" value=""></input>
			<input name="search_crit_2" type="hidden" init="" value=""></input>
			<input name="search_crit_3" type="hidden" init="M" value="M"></input>
			<input name="name" type="hidden" init="" value="Teherba"></input>
			<input name="family" type="hidden" init="" value="Ritter"></input>
			<input name="gender" type="hidden" init="M" value="M"></input>
			<input name="birth" type="hidden" init="" value=""></input>
			<input name="place" type="hidden" init="" value=""></input>
			<input name="decease" type="hidden" init="" value=""></input>
			<input name="user" type="hidden" init="" value=""></input>
			<input type="submit" value="Delete"></input> <a href="servlet?spec=test/crud03">Back</a> to the search form
		</form>
	



<br />Output on 2012-06-01 22:56:55.595 by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/crud03.xml" type="text/plain">test/crud03</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;spec=test.crud03&amp;search_crit_2=&amp;birth=&amp;search_crit_3=M&amp;search_crit_1=&amp;lang=en&amp;family=Ritter&amp;name=Teherba&amp;gender=M&amp;search_value_2=Ritter&amp;decease=&amp;search_value_1=&amp;place=&amp;user=">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test.crud03&amp;search_crit_2=&amp;birth=&amp;search_crit_3=M&amp;search_crit_1=&amp;lang=en&amp;family=Ritter&amp;name=Teherba&amp;gender=M&amp;search_value_2=Ritter&amp;decease=&amp;search_value_1=&amp;place=&amp;user=">more</a>

</body></html>
