<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>listboxmult</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

    <!--List Box and List Parameter Test - multiple choice-->
    <!--Test von Auswahl- und Parameterlisten - multiple Auswahl-->

    <h2>List Box and List Parameter Test - check multiple options with Ctrl</h2>
    <form method="get" action="servlet?spec=test/listboxmult"><input name="spec" type="hidden" value="test/listboxmult" />

        <table>
        <tr><td valign="top" title="Initial">Initial:</td>
        <td>
            <!-- SQL:
SELECT DISTINCT substr(name, 1, 1)
, name 
FROM c01 
ORDER BY name;
:SQL -->

            <select name="namebox" size="10" multiple="yes ">
<option value="D" selected="yes">Dorothea</option>
<option value="E">Eberhard</option>
<option value="F">Fritz</option>
<option value="I">Ilse</option>
<option value="J">Johannes</option>
<option value="L">Lucie</option>
<option value="M">Maria</option>
<option value="M">Martha</option>
</select>

        </td>
        <td valign="top" title="Typ">Multiple<br />
            initials       <br />
            are selected   <br />
            by Ctrl-click, <br />
            all with Ctrl-a<br />
            <input type="submit" value="Search"></input>
        </td></tr>
        </table>
    </form>
    
    <!-- SQL:
SELECT name
, univ
, birth 
FROM c01 
WHERE substr(name, 1, 1) in ('D', 'F', 'J') 
ORDER BY name;
:SQL -->
<table id="table2">
<tr><th title="name">Name</th><th title="univ">University</th><th title="birth">Birthdate</th></tr>
<tr><td>Dorothea</td><td>Lübars</td><td>1910-02-07</td></tr>
<tr><td>Fritz</td><td>Waldshut</td><td>1907-08-08</td></tr>
<tr><td>Johannes</td><td>Schramberg</td><td>1911-06-03</td></tr>
</table>


<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/listboxmult.xml" type="text/plain">test/listboxmult</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;namebox=D&amp;namebox=F&amp;namebox=J&amp;spec=test%2Flistboxmult&amp;lang=en&amp;code=D&amp;code=E&amp;code=F&amp;code=I&amp;code=J&amp;code=L&amp;code=M&amp;code=M&amp;display=Dorothea&amp;display=Eberhard&amp;display=Fritz&amp;display=Ilse&amp;display=Johannes&amp;display=Lucie&amp;display=Maria&amp;display=Martha">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;namebox=D&amp;namebox=F&amp;namebox=J&amp;spec=test%2Flistboxmult&amp;lang=en&amp;code=D&amp;code=E&amp;code=F&amp;code=I&amp;code=J&amp;code=L&amp;code=M&amp;code=M&amp;display=Dorothea&amp;display=Eberhard&amp;display=Fritz&amp;display=Ilse&amp;display=Johannes&amp;display=Lucie&amp;display=Maria&amp;display=Martha">more</a>

</body></html>
