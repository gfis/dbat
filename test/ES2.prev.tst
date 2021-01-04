<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>area</title>
<link rel="stylesheet" type="text/css" href="spec/stylesheet.css" />
</head><body>

    <!--specifications in one area-->
    <!--Spezifikationen in einem Teilbereich-->
    
        <a href="servlet?spec=area&lang=de"><img src="img/de_flag.png" align="right" title="Deutsch"></img></a>
        <form method="get" action="servlet?spec=area"><input name="spec" type="hidden" value="area" />

            Area: <input name="subdir" maxsize="64" size="32" init="test" value=""></input>% <br />
            <input type="hidden" name="execsql" value="1"></input>
            <input type="submit" value="Show list"></input> 
        </form>
        <h3><a href="index.html">Dbat</a> specifications in area <em></em></h3>
        <!-- SQL:
SELECT subdir || '.' || name
, '<strong>' || title || '</strong> — ' || comment
, params 
FROM spec_index 
WHERE lang   = 'en'
              AND  subdir = '' 
ORDER BY subdir,name;
:SQL -->
<div>
<table id="table1">
<tr><th title="subdir || '.' || name">Specification</th><th title="'<strong>' || title || '</strong> — ' || comment">Description</th><th title="params">Parameters with defaults</th></tr>
<tr><td><a href="servlet?&amp;spec=.area">.area</a></td><td><strong>area</strong> — specifications in one area</td><td>&amp;lang=de &amp;subdir=</td></tr>
<tr><td><a href="servlet?&amp;spec=.describe">.describe</a></td><td><strong>DDL of a Table/View</strong> — Shows the DDL Description of a Table or View</td><td>&amp;table=spec_ind</td></tr>
<tr><td><a href="servlet?&amp;spec=.index">.index</a></td><td><strong>index</strong> — all specifications</td><td>&amp;lang=de</td></tr>
<tr><td class="counter" colspan="3">3 Specifications</td></tr>
</table>
</div>

    
    
    
<br />Output by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/area.xml" type="text/plain">area</a>

</body></html>
