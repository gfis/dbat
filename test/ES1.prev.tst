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

            Area: <input name="subdir" maxsize="64" size="32" init="test" value="test"></input>% <br />
            <input type="hidden" name="execsql" value="1"></input>
            <input type="submit" value="Show list"></input> 
        </form>
        <h3><a href="index.html">Dbat</a> specifications in area <em>test</em></h3>
        <!-- SQL:
SELECT subdir || '.' || name
, '<strong>' || title || '</strong> — ' || comment
, params 
FROM spec_index 
WHERE lang   = 'en'
              AND  subdir = 'test' 
ORDER BY subdir,name;
:SQL -->

    
    
    
<br />Output by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/area.xml" type="text/plain">area</a>

</body></html>
