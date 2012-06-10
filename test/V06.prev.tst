wget -q -O - "http://localhost:8080/dbat/servlet?spec=test.crud03&view=ins&search_crit_1=&family=Ritter&birth=1914-09-17&gender=F&place=Berlin-Hermsdorf&decease=1999&changed=current_timestamp&user=testuser"
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

		<h4>Enter a new Person</h4>
		<form name="form1" method="post" action="servlet?spec=test/crud03"><input name="spec" type="hidden" value="test/crud03" />

			<input name="view" type="hidden" value="ins2"></input>
			<input name="opcode" type="hidden" value="ins"></input>
			<input name="search_crit_1" type="hidden" init="" value=""></input>
			<input name="search_crit_2" type="hidden" init="" value=""></input>
			<input name="search_crit_3" type="hidden" init="M" value="M"></input>
			<table>
			<tr><td valign="top" title="name">Name:</td><td><input name="name" size="40" init="" title="[\w\-%]*" onkeyup="this.form.name.className = (this.form.name.value.match('^[\\w\\-%]*$')) ? 'valid': 'invalid'" value=""></input> Letters and "-"</td></tr>
			<tr><td valign="top" title="family">Family:</td><td><select name="family" init="" size="2">
				<option value="Ritter" selected="yes">Ritter Family</option>
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

				<select name="gender" size="2">
<option value="F" selected="yes">female</option>
<option value="M">male</option>
</select>

			</td></tr>
			<tr><td valign="top" title="birth">Birthdate:</td><td><input name="birth" size="10" init="" title="\d{4}\-\d{2}\-\d{2}" onkeyup="this.form.birth.className = (this.form.birth.value.match('^\\d{4}\\-\\d{2}\\-\\d{2}$')) ? 'valid': 'invalid'" value="1914-09-17"></input> yyyy-mm-dd</td></tr>
			<tr><td valign="top" title="place">Place:</td><td><textarea name="place" cols="20" rows="5" init="" title="\w[\w \.\-\(\)]*" onkeyup="this.form.place.className = (this.form.place.value.match('^\\w[\\w \\.\\-\\(\\)]*$')) ? 'valid': 'invalid'" value="Berlin-Hermsdorf">Berlin-Hermsdorf</textarea> Letters space . - ( )</td></tr>
			<tr><td valign="top" title="decease">Died:</td><td><input name="decease" size="4" init="" title="\d{4}" onkeyup="this.form.decease.className = (this.form.decease.value.match('^\\d{4}$')) ? 'valid': 'invalid'" value="1999"></input> Year yyyy</td></tr>
			</table>
			<input type="submit" value="Save"></input> <a href="servlet?spec=test/crud03">Back</a> to the search form
		</form>
	



<br />Output on 2012-06-01 22:51:22.437 by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/crud03.xml" type="text/plain">test/crud03</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;spec=test.crud03&amp;birth=1914-09-17&amp;search_crit_2=&amp;search_crit_3=M&amp;search_crit_1=&amp;display=female&amp;display=male&amp;code=F&amp;code=M&amp;lang=en&amp;changed=current_timestamp&amp;family=Ritter&amp;name=&amp;gender=F&amp;decease=1999&amp;place=Berlin-Hermsdorf&amp;user=testuser">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test.crud03&amp;birth=1914-09-17&amp;search_crit_2=&amp;search_crit_3=M&amp;search_crit_1=&amp;display=female&amp;display=male&amp;code=F&amp;code=M&amp;lang=en&amp;changed=current_timestamp&amp;family=Ritter&amp;name=&amp;gender=F&amp;decease=1999&amp;place=Berlin-Hermsdorf&amp;user=testuser">more</a>

</body></html>
