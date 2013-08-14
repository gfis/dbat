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
UPDATE relatives SET birth = '1914-09-17'
, place = 'Berlin-Hermsdorf'
, decease = 1999
, user = 'unknown' 
WHERE name = 'Maria'
				and family = 'Ritter'
				and gender = 'F';
:SQL -->

		<h4>1 row(s) for key
			 <em> Maria</em>
			 <em> Ritter</em>
			 <em> F</em> updated
		</h4>
	


		<form method="post" action="servlet?spec=test/crud03"><input name="spec" type="hidden" value="test/crud03" />

			<input name="view" type="hidden" value="sear"></input>
			<table>
			<tr><td valign="top" title="name">Name:</td>
				<td><input name="search_name" size="40" init="" title="[\w\-%]*" onkeyup="this.form.name.className = (this.form.name.value.match('^[\\w\\-%]*$')) ? 'valid': 'invalid'" value=""></input> Letters and "-"
				</td></tr>
			<tr><td valign="top" title="family">Family:</td>
				<td><select name="search_family" init="" size="3">
				<option value="">(any)</option>
				<option value="Ritter" selected="yes">Ritter Family</option>
				<option value="Fischer">Fischer Family</option>
				</select>
				</td></tr>
			<tr><td valign="top" title="gender">Gender:</td>
				<td>
				<!-- SQL:
SELECT DISTINCT gender
, case 
						when gender = 'M' then 'male' 
						else                   'female' end 
FROM relatives 
ORDER BY 1;
:SQL -->

				<select name="search_gender" size="3">
<option value="">(any)</option>
<option value="F" selected="yes">female</option>
<option value="M">male</option>
</select>

				
				</td></tr>
			</table>
			<input name="birth" type="hidden" init="" value="1914-09-17"></input>
			<input name="place" type="hidden" init="" value="Berlin-Hermsdorf"></input>
			<input name="decease" type="hidden" init="" value="1999"></input>
			<input name="user" type="hidden" init="" value="testuser"></input>
			<input type="submit" value="Search"></input>
		</form><br />
		<form method="post" action="servlet?spec=test/crud03"><input name="spec" type="hidden" value="test/crud03" />

			<input name="view" type="hidden" value="ins"></input>
			<input name="search_name" type="hidden" init="" value=""></input>
			<input name="search_family" type="hidden" init="" value="Ritter"></input>
			<input name="search_gender" type="hidden" init="M" value=""></input>
			<input name="birth" type="hidden" init="" value="1914-09-17"></input>
			<input name="place" type="hidden" init="" value="Berlin-Hermsdorf"></input>
			<input name="decease" type="hidden" init="" value="1999"></input>
			<input name="user" type="hidden" init="" value="testuser"></input>
			<input type="submit" value="New Relative"></input>
		</form>
		<!-- SQL:
SELECT '' || '=' 
				 || 'Ritter' || '=' 
				 || '' || '=' 
				 || name || '=' 
				 || family || '=' 
				 || gender || '=' || 'upd'
, '' || '=' 
				 || 'Ritter' || '=' 
				 || '' || '=' 
				 || name || '=' 
				 || family || '=' 
				 || gender || '=' || 'del'
, name
, family
, gender
, birth
, place
, decease 
FROM relatives 
WHERE name like '%'
				and family like 'Ritter%'
				and gender like '%' 
ORDER BY 1,2;
:SQL -->
<table id="tab1"><!-- table_not_specified -->
<tr><th title="update">Upd.</th><th title="delete">Del.</th><th title="name">Name</th><th title="family">Family</th><th title="gender">Gender</th><th title="birth">Birthdate</th><th title="place">Place</th><th title="decease">Died</th></tr>
<tr><td align="center"><a href="servlet?spec=test/crud03&amp;search_name=&amp;search_family=Ritter&amp;search_gender=&amp;name=Ilse&amp;family=Ritter&amp;gender=F&amp;view=upd"><img src="img/upd.png" /></a></td><td align="center"><a href="servlet?spec=test/crud03&amp;search_name=&amp;search_family=Ritter&amp;search_gender=&amp;name=Ilse&amp;family=Ritter&amp;gender=F&amp;view=del"><img src="img/del.png" /></a></td><td>Ilse</td><td>Ritter</td><td align="center">F</td><td>1909-02-09</td><td>Lübars</td><td align="right">1983</td></tr>
<tr><td align="center"><a href="servlet?spec=test/crud03&amp;search_name=&amp;search_family=Ritter&amp;search_gender=&amp;name=Lucie&amp;family=Ritter&amp;gender=F&amp;view=upd"><img src="img/upd.png" /></a></td><td align="center"><a href="servlet?spec=test/crud03&amp;search_name=&amp;search_family=Ritter&amp;search_gender=&amp;name=Lucie&amp;family=Ritter&amp;gender=F&amp;view=del"><img src="img/del.png" /></a></td><td>Lucie</td><td>Ritter</td><td align="center">F</td><td>1887-07-09</td><td>Lübars</td><td align="right">1984</td></tr>
<tr><td align="center"><a href="servlet?spec=test/crud03&amp;search_name=&amp;search_family=Ritter&amp;search_gender=&amp;name=Maria&amp;family=Ritter&amp;gender=F&amp;view=upd"><img src="img/upd.png" /></a></td><td align="center"><a href="servlet?spec=test/crud03&amp;search_name=&amp;search_family=Ritter&amp;search_gender=&amp;name=Maria&amp;family=Ritter&amp;gender=F&amp;view=del"><img src="img/del.png" /></a></td><td>Maria</td><td>Ritter</td><td align="center">F</td><td>1914-09-17</td><td>Berlin-Hermsdorf</td><td align="right">1999</td></tr>
<tr><td class="counter" colspan="8">3 Persons</td></tr>
</table>

	

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/crud03.xml" type="text/plain">test/crud03</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;spec=test.crud03&amp;search_family=Ritter&amp;birth=1914-09-17&amp;family=Ritter&amp;name=Maria&amp;search_name=&amp;gender=F&amp;decease=1999&amp;place=Berlin-Hermsdorf&amp;user=testuser&amp;changed=current_timestamp&amp;lang=en&amp;update_count=1&amp;code=F&amp;code=M&amp;display=female&amp;display=male&amp;search_gender=">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test.crud03&amp;search_family=Ritter&amp;birth=1914-09-17&amp;family=Ritter&amp;name=Maria&amp;search_name=&amp;gender=F&amp;decease=1999&amp;place=Berlin-Hermsdorf&amp;user=testuser&amp;changed=current_timestamp&amp;lang=en&amp;update_count=1&amp;code=F&amp;code=M&amp;display=female&amp;display=male&amp;search_gender=">more</a>

</body></html>
