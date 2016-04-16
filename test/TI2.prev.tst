<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>highlight</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>


    <!--Liste der Spezifikationen mit Hervorhebung eines Schlagworts
    -->
    <!--List of Specifications with Highlighting of a Keyword
    -->

    <h2><a href="index.html">Dbat</a> - Test Specifications</h2>
    <form method="get" action="servlet?spec=test/highlight"><input name="spec" type="hidden" value="test/highlight" />

        Keyword: <input name="keyword" maxsize="72" size="72" init="ajax" value="ajax"></input>  
        <input type="submit" value="Search"></input>
    </form>

    <!-- SQL:
SELECT subdir
, subdir ||  '.' || name
, REPLACE(UPPER(comment),UPPER('ajax'), '<span style="background: lightsalmon;">' || UPPER('ajax') || '</span>')
, params 
FROM spec_index 
WHERE subdir = 'test'
        
            AND UPPER(comment) LIKE UPPER('%ajax%') 
ORDER BY 1,2;
:SQL -->
<table id="tab1"><!-- table_not_specified -->
<tr><th title="subdir">Area</th><th title="subdir ||  '.' || name">Script</th><th title="REPLACE(UPPER(comment),UPPER('ajax'), '<span style=&quot;background: lightsalmon;&quot;>' || UPPER('ajax') || '</span>')">Description</th><th title="params">Parameters with defaults</th></tr>
<tr><td class="visible">test</td><td><a href="servlet?&amp;spec=test.ajax01">test.ajax01</a></td><td>ERSTER <span style="background: lightsalmon;">AJAX</span>-TEST</td><td>&amp;changed=</td></tr>
<tr><td class="invisible">test</td><td><a href="servlet?&amp;spec=test.ajax02">test.ajax02</a></td><td>DATEN FÜR SPEZIFIKATION <span style="background: lightsalmon;">AJAX</span>01.XML</td><td>&amp;name=Dorothea</td></tr>
<tr><td class="counter" colspan="4">2 Scripts</td></tr>
</table>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/highlight.xml" type="text/plain">test/highlight</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;keyword=ajax&amp;spec=test%2Fhighlight&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;keyword=ajax&amp;spec=test%2Fhighlight&amp;lang=en">more</a>

</body></html>
