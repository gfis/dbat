<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>highlight</title>
<link rel="stylesheet" type="text/css" href="./web/spec/test/stylesheet.css" />
</head><body>


    <!--Liste der Spezifikationen mit Hervorhebung eines Schlagworts
    -->
    <!--List of Specifications with Highlighting of a Keyword
    -->

    <h2><a href="index.html">Dbat</a> - Test Specifications</h2>
    <form method="get" action="servlet?spec=web/spec/test/highlight.xml"><input name="spec" type="hidden" value="web/spec/test/highlight.xml" />

        Keyword: <input name="keyword" maxsize="72" size="72" init="color" value="color"></input>  
        <input type="submit" value="Search"></input>
    </form>

    <!-- SQL:
SELECT subdir
, subdir ||  '.' || name
, REPLACE(UPPER(comment),UPPER('color'), '<span style="background: lightsalmon;">' || UPPER('color') || '</span>')
, params 
FROM spec_index 
WHERE subdir = 'test'
            AND lang = 'en'
            AND UPPER(comment) LIKE UPPER('%color%') 
ORDER BY 1,2;
:SQL -->
<table id="tab1"><!-- table_not_specified -->
<tr><th title="subdir">Area</th><th title="subdir ||  '.' || name">Script</th><th title="REPLACE(UPPER(comment),UPPER('color'), '<span style=&quot;background: lightsalmon;&quot;>' || UPPER('color') || '</span>')">Description</th><th title="params">Parameters with defaults</th></tr>
<tr><td class="visible">test</td><td><a href="servlet?&amp;spec=test.color04">test.color04</a></td><td>SELECT FROM TEST TABLE - WITH <span style="background: lightsalmon;">COLOR</span>S AND LINKED VALUES</td><td>&amp;name=%r</td></tr>
<tr><td class="invisible">test</td><td><a href="servlet?&amp;spec=test.color05">test.color05</a></td><td>SELECT FROM TEST TABLE - WITH <span style="background: lightsalmon;">COLOR</span>S AND LINKED VALUES</td><td>&amp;name=%r</td></tr>
<tr><td class="invisible">test</td><td><a href="servlet?&amp;spec=test.color08">test.color08</a></td><td>SELECT FROM TEST TABLE - WITH <span style="background: lightsalmon;">COLOR</span>S ON ROWS</td><td>&amp;name=%r</td></tr>
<tr><td class="invisible">test</td><td><a href="servlet?&amp;spec=test.color09">test.color09</a></td><td><span style="background: lightsalmon;">COLOR</span>S ON COLUMNS AND ROWS</td><td></td></tr>
<tr><td class="counter" colspan="4">4 Scripts</td></tr>
</table>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="./web/spec/test/highlight.xml" type="text/plain">web/spec/test/highlight.xml</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;lang=en&amp;keyword=color">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;lang=en&amp;keyword=color">more</a>

</body></html>
