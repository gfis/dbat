<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>ajax01</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
<script src="spec/test/../http_request.js" type="text/javascript"></script>
</head><body>

    <!--First  Ajax Test-->
    <!--Erster Ajax-Test-->

    <h2><a href="servlet?spec=test.index">Test</a> Form - Relatives (with AJAX)</h2>

    <h4>Enter a new Person</h4>
    <form name="form1" method="post" action="servlet?spec=test/ajax01"><input name="spec" type="hidden" value="test/ajax01" />

        <input name="view" type="hidden" value="ins2"></input>
        <input name="opcode" type="hidden" value="ins"></input>
        <input name="search_crit_1" type="hidden" init="" value=""></input>
        <input name="search_crit_2" type="hidden" init="" value=""></input>
        <input name="search_crit_3" type="hidden" init="" value=""></input>
        <table>
        <tr><td valign="top" title="name" id="a0">Name:</td>
        <td id="a1"><input id="a2" name="name" size="16" class="blu" onblur="ajaxRequest('test.ajax02&name=' + this.form.name.value);" value="Ilse"></input>
                ?</td></tr>

        <tr><td colspan="2" id="b0">the location goes here</td></tr>

        <tr><td valign="top" title="decease">Died:</td>
        <td><input name="decease" size="4" init="" title="\d{4}" onkeyup="this.form.decease.className = (this.form.decease.value.match('^\\d{4}$')) ? 'valid': 'invalid'" value=""></input>
                yyyy</td></tr>

        <tr><td valign="top" title="family">Family:</td>
        <td><select name="family" init="" height="2">
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
ORDER BY 1;
:SQL -->

            <select name="gender" size="2">
<option value="F">female</option>
<option value="M" selected="yes">male</option>
</select>

        </td></tr>
        <tr><td valign="top" title="birth">Birthdate:</td><td>
        <input name="birth" size="10" init="" title="\d{4}\-\d{2}\-\d{2}" onkeyup="this.form.birth.className = (this.form.birth.value.match('^\\d{4}\\-\\d{2}\\-\\d{2}$')) ? 'valid': 'invalid'" value=""></input> yyyy-mm-dd</td></tr>
        <tr><td valign="top" title="place">Place:</td>
        <td><textarea name="place" cols="20" rows="5" init="" title="\w[\w \.\-\(\)]*" onkeyup="this.form.place.className = (this.form.place.value.match('^\\w[\\w \\.\\-\\(\\)]*$')) ? 'valid': 'invalid'" value=""></textarea> Letters space . - ( )</td></tr>
        <tr><td valign="top" title="changed">Changed:</td><td></td></tr>
        </table>
        <input type="submit" value="Save"></input>
        &#xa0;&#xa0;<a href="servlet?spec=test/crud01">Back</a> to the search form
    </form>
<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/ajax01.xml" type="text/plain">test/ajax01</a>,
<a target="_blank" href="servlet?&amp;mode=xlsx&amp;name=Ilse&amp;spec=test.ajax01&amp;lang=en&amp;search_crit_1=&amp;search_crit_2=&amp;search_crit_3=&amp;decease=&amp;family=&amp;code=F&amp;code=M&amp;display=female&amp;display=male&amp;gender=M&amp;birth=&amp;place=">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;name=Ilse&amp;spec=test.ajax01&amp;lang=en&amp;search_crit_1=&amp;search_crit_2=&amp;search_crit_3=&amp;decease=&amp;family=&amp;code=F&amp;code=M&amp;display=female&amp;display=male&amp;gender=M&amp;birth=&amp;place=">more</a>

</body></html>
