<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>selvalid</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

    <!--Select with input field validation-->
    <!--Abfrage mit Eingabefeld-Validierung-->
    
    <h3>Select with input field validation</h3>
    <form method="get" action="servlet?spec=test/selvalid"><input name="spec" type="hidden" value="test/selvalid" />

        Start: <input name="start" maxsize="4" size="4" init="1900" valid="\d{4}" value="wrong" class="red" title="Error in field validation with pattern &quot;\d{4}&quot;"></input><a href="servlet?view=validate&amp;lang=en&amp;name=start&amp;regex=%5Cd%7B4%7D&amp;value=wrong" target="_blank">*</a>  
        End: <input name="end" maxsize="4" size="4" init="2000" valid="\d{4}" value="bad" class="red" title="Error in field validation with pattern &quot;\d{4}&quot;"></input><a href="servlet?view=validate&amp;lang=en&amp;name=end&amp;regex=%5Cd%7B4%7D&amp;value=bad" target="_blank">*</a>  
        <input type="submit" value="Submit"></input>
    </form>
    
<h4 style="color:red">There was a validation problem with the input field(s) highlighted in red.<br /> Please move the mouse cursor over the field or click on the asterisk to see the <a href="servlet?view=validate&amp;lang=en&amp;regex=" target="_blank">validation rule</a>.</h4><br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/selvalid.xml" type="text/plain">test/selvalid</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;end=bad&amp;spec=test.selvalid&amp;start=wrong&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;end=bad&amp;spec=test.selvalid&amp;start=wrong&amp;lang=en">more</a>

</body></html>
