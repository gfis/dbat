wget -q -O - "http://localhost:8080/dbat/servlet?spec=test.crud03&view=sear&search_crit_1=M&family=Ritter&birth=&gender=M&place=&decease="
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

	


		<form method="post" action="servlet?spec=test/crud03"><input name="spec" type="hidden" value="test/crud03" />

			<input name="view" type="hidden" value="sear"></input>
			<table>
			<tr><td valign="top" title="name">Name:</td><td><input name="search_crit_1" size="40" init="" title="[\w\-%]*" onkeyup="this.form.name.className = (this.form.name.value.match('^[\\w\\-%]*$')) ? 'valid': 'invalid'" value="M"></input> Letters and "-"</td></tr>
			<tr><td valign="top" title="family">Family:</td><td><select name="search_crit_2" init="" size="3">
				<option value="" selected="yes">(any)</option>
				<option value="Ritter">Ritter Family</option>
				<option value="Fischer">Fischer Family</option>
				</select></td></tr>
			<tr><td valign="top" title="gender">Gender:</td><td>
				<!-- SQL:
SELECT DISTINCT gender
, case 
						when gender = 'M' then 'male' 
						else                   'female' end 
FROM relatives 
ORDER BY 1
:SQL -->

				<select name="search_crit_3" size="3">
<option value="">(any)</option>
<option value="F">female</option>
<option value="M">male</option>
</select>

			</td></tr>
			</table>
			<input name="birth" type="hidden" init="" value=""></input>
			<input name="place" type="hidden" init="" value=""></input>
			<input name="decease" type="hidden" init="" value=""></input>
			<input name="user" type="hidden" init="" value=""></input>
			<input type="submit" value="Search"></input>
		</form><br />
		<form method="post" action="servlet?spec=test/crud03"><input name="spec" type="hidden" value="test/crud03" />

			<input name="view" type="hidden" value="ins"></input>
			<input name="search_crit_1" type="hidden" init="" value="M"></input>
			<input name="search_crit_2" type="hidden" init="" value=""></input>
			<input name="search_crit_3" type="hidden" init="M" value=""></input>
			<input name="birth" type="hidden" init="" value=""></input>
			<input name="place" type="hidden" init="" value=""></input>
			<input name="decease" type="hidden" init="" value=""></input>
			<input name="user" type="hidden" init="" value=""></input>
			<input type="submit" value="New Person"></input>
		</form>
		<!-- SQL:
SELECT 'M' || '=' 
				 || '' || '=' 
				 || '' || '=' 
				 || name || '=' 
				 || family || '=' 
				 || gender || '=' || 'upd'
, 'M' || '=' 
				 || '' || '=' 
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
WHERE name like 'M%'
				and family like '%'
				and gender like '%' 
ORDER BY 1,2
:SQL -->
<table id="tab1"><!-- table_not_specified -->
<tr><th title="'M' || '=' 
				 || '' || '=' 
				 || '' || '=' 
				 || name || '=' 
				 || family || '=' 
				 || gender || '=' || 'upd'">Upd.</th><th title="'M' || '=' 
				 || '' || '=' 
				 || '' || '=' 
				 || name || '=' 
				 || family || '=' 
				 || gender || '=' || 'del'">Del.</th><th title="name">Name</th><th title="family">Family</th><th title="gender">Gender</th><th title="birth">Birthdate</th><th title="place">Place</th><th title="decease">Died</th></tr>
<tr><td align="center"><a href="servlet?spec=test/crud03&amp;search_crit_1=M&amp;search_crit_2=&amp;search_crit_3=&amp;name=Maria&amp;family=Ritter&amp;gender=F&amp;view=upd"><img src="img/upd.png" /></a></td><td align="center"><a href="servlet?spec=test/crud03&amp;search_crit_1=M&amp;search_crit_2=&amp;search_crit_3=&amp;name=Maria&amp;family=Ritter&amp;gender=F&amp;view=del"><img src="img/del.png" /></a></td><td>Maria</td><td>Ritter</td><td align="center">F</td><td>1914-09-17</td><td>Hermsdorf</td><td align="right">1999</td></tr>
<tr><td align="center"><a href="servlet?spec=test/crud03&amp;search_crit_1=M&amp;search_crit_2=&amp;search_crit_3=&amp;name=Martha&amp;family=Fischer&amp;gender=F&amp;view=upd"><img src="img/upd.png" /></a></td><td align="center"><a href="servlet?spec=test/crud03&amp;search_crit_1=M&amp;search_crit_2=&amp;search_crit_3=&amp;name=Martha&amp;family=Fischer&amp;gender=F&amp;view=del"><img src="img/del.png" /></a></td><td>Martha</td><td>Fischer</td><td align="center">F</td><td>1909-11-17</td><td>Freiburg</td><td align="right">1999</td></tr>
<tr><td class="counter" colspan="8">2 Persons</td></tr>
</table>

	
		<h3>Help text follows here</h3>
	

<br />Output on 2012-06-01 22:51:22.344 by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/crud03.xml" type="text/plain">test/crud03</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;spec=test.crud03&amp;birth=&amp;search_crit_2=&amp;search_crit_3=&amp;search_crit_1=M&amp;display=female&amp;display=male&amp;code=F&amp;code=M&amp;lang=en&amp;family=Ritter&amp;gender=M&amp;decease=&amp;place=&amp;user=">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test.crud03&amp;birth=&amp;search_crit_2=&amp;search_crit_3=&amp;search_crit_1=M&amp;display=female&amp;display=male&amp;code=F&amp;code=M&amp;lang=en&amp;family=Ritter&amp;gender=M&amp;decease=&amp;place=&amp;user=">more</a>

</body></html>
