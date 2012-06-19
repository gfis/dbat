wget -q -O - "http://localhost:8080/dbat/servlet?spec=test.crud03&search_value_1=M&search_gender='&name=Maria&family=Ritter&view=upd"
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
SELECT birth
, place
, decease 
FROM relatives 
WHERE name = 'Maria'
				and family = 'Ritter'
				and gender = '''';
:SQL -->

		<h4>Update Person</h4>
		<form name="form1" method="post" action="servlet?spec=test/crud03"><input name="spec" type="hidden" value="test/crud03" />

			<input name="view" type="hidden" value="upd2"></input>
			<input name="opcode" type="hidden" value="upd"></input>
			<input name="search_name" type="hidden" init="" value=""></input>
			<input name="search_family" type="hidden" init="" value=""></input>
			<input name="search_gender" type="hidden" init="M" value="'"></input>
			<table>
			<input name="name" type="hidden" init="" value="Maria"></input>
			<input name="family" type="hidden" init="" value="Ritter"></input>
			<input name="gender" type="hidden" init="M" value="M"></input>
			<tr><td valign="top" title="name">Name:</td>
				<td>Maria
				</td></tr>
			<tr><td valign="top" title="family">Family:</td>
				<td>Ritter
				</td></tr>
			<tr><td valign="top" title="gender">Gender:</td>
				<td>M
				</td></tr>
			<tr><td valign="top" title="birth">Birthdate:</td>
				<td><input name="birth" size="10" init="" title="\d{4}\-\d{2}\-\d{2}" onkeyup="this.form.birth.className = (this.form.birth.value.match('^\\d{4}\\-\\d{2}\\-\\d{2}$')) ? 'valid': 'invalid'" value=""></input> yyyy-mm-dd
				</td></tr>
			<tr><td valign="top" title="place">Place:</td>
				<td><textarea name="place" cols="20" rows="5" init="" title="\w[\w \.\-\(\)]*" onkeyup="this.form.place.className = (this.form.place.value.match('^\\w[\\w \\.\\-\\(\\)]*$')) ? 'valid': 'invalid'" value=""></textarea> Letters space . - ( )
				</td></tr>
			<tr><td valign="top" title="decease">Died:</td>
				<td><input name="decease" size="4" init="" title="\d{4}" onkeyup="this.form.decease.className = (this.form.decease.value.match('^\\d{4}$')) ? 'valid': 'invalid'" value=""></input> Year yyyy
				</td></tr>
			</table>
			<input type="submit" value="Update"></input> <a href="servlet?spec=test/crud03">Back</a> to the search form
		</form>
	



<br />Output on 2012-06-19 13:08:46.096 by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/crud03.xml" type="text/plain">test/crud03</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;spec=test.crud03&amp;birth=&amp;search_name=&amp;lang=en&amp;search_family=&amp;family=Ritter&amp;name=Maria&amp;gender=M&amp;search_value_1=M&amp;decease=&amp;place=&amp;search_gender=%27">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test.crud03&amp;birth=&amp;search_name=&amp;lang=en&amp;search_family=&amp;family=Ritter&amp;name=Maria&amp;gender=M&amp;search_value_1=M&amp;decease=&amp;place=&amp;search_gender=%27">more</a>

</body></html>
