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
<div>
<table id="table1">
<tr><th title="subdir || '.' || name">Specification</th><th title="'<strong>' || title || '</strong> — ' || comment">Description</th><th title="params">Parameters with defaults</th></tr>
<tr><td><a href="servlet?&amp;spec=test.aggr01">test.aggr01</a></td><td><strong>aggr01</strong> — Column Aggregation with linked values</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.ajax01">test.ajax01</a></td><td><strong>ajax01</strong> — First Ajax Test</td><td>&amp;changed=</td></tr>
<tr><td><a href="servlet?&amp;spec=test.ajax02">test.ajax02</a></td><td><strong>ajax02</strong> — Data to be retrieved by specification ajax01.xml</td><td>&amp;name=Dorothea</td></tr>
<tr><td><a href="servlet?&amp;spec=test.align01">test.align01</a></td><td><strong>align01</strong> — Select from test table with explicit widths</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.align02">test.align02</a></td><td><strong>align02</strong> — Select from test table with right aligned numbers</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.callproc">test.callproc</a></td><td><strong>callproc</strong> — Activation of a Stored Procedure</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.callx10">test.callx10</a></td><td><strong>callx10</strong> — Activation of a Stored Procedure; results into parameters</td><td>&amp;iname0= &amp;odate3= &amp;odec1= &amp;odec2= &amp;otime4= &amp;ots5=</td></tr>
<tr><td><a href="servlet?&amp;spec=test.calwork_temp">test.calwork_temp</a></td><td><strong>calwork_temp</strong> —</td><td>&amp;prefix=</td></tr>
<tr><td><a href="servlet?&amp;spec=test.choose06">test.choose06</a></td><td><strong>choose06</strong> — Test of Conditional Compilation with "choose"</td><td>&amp;switch=yes</td></tr>
<tr><td><a href="servlet?&amp;spec=test.choose07">test.choose07</a></td><td><strong>choose07</strong> — Test of Conditional Compilation with Regular Expressions</td><td>&amp;par=x</td></tr>
<tr><td><a href="servlet?&amp;spec=test.color04">test.color04</a></td><td><strong>color04</strong> — Select from test table - with colors and linked values</td><td>&amp;name=%r</td></tr>
<tr><td><a href="servlet?&amp;spec=test.color05">test.color05</a></td><td><strong>color05</strong> — Select from test table - with colors and linked values</td><td>&amp;name=%r</td></tr>
<tr><td><a href="servlet?&amp;spec=test.color07">test.color07</a></td><td><strong>color07</strong> — Select from test table with "sorttable.js"</td><td>&amp;name=%</td></tr>
<tr><td><a href="servlet?&amp;spec=test.color08">test.color08</a></td><td><strong>color08</strong> — Select from test table - with colors on rows</td><td>&amp;name=%r</td></tr>
<tr><td><a href="servlet?&amp;spec=test.color09">test.color09</a></td><td><strong>color09</strong> — Colors on columns and rows</td><td>&amp;name=</td></tr>
<tr><td><a href="servlet?&amp;spec=test.column02">test.column02</a></td><td><strong>column02</strong> — Select from test table - same as selec02</td><td>&amp;name=%</td></tr>
<tr><td><a href="servlet?&amp;spec=test.counter01">test.counter01</a></td><td><strong>counter01</strong> — Counter Test</td><td>&amp;name=%r</td></tr>
<tr><td><a href="servlet?&amp;spec=test.crud01">test.crud01</a></td><td><strong>test/crud01</strong> —</td><td>&amp;birth= &amp;decease= &amp;family= &amp;gender= &amp;name= &amp;place= &amp;remote_user= &amp;search_decease= &amp;search_family= &amp;search_name= &amp;update_count= &amp;user=</td></tr>
<tr><td><a href="servlet?&amp;spec=test.crud02">test.crud02</a></td><td><strong>test/crud02</strong> —</td><td>&amp;birth= &amp;changed= &amp;decease= &amp;family= &amp;gender= &amp;name= &amp;place= &amp;remote_user= &amp;search_family= &amp;search_gender= &amp;search_name= &amp;update_count= &amp;user=</td></tr>
<tr><td><a href="servlet?&amp;spec=test.crud03">test.crud03</a></td><td><strong>test/crud03</strong> —</td><td>&amp;birth= &amp;decease= &amp;family= &amp;gender= &amp;name= &amp;place= &amp;remote_user= &amp;search_family= &amp;search_gender= &amp;search_name= &amp;update_count=</td></tr>
<tr><td><a href="servlet?&amp;spec=test.crud04">test.crud04</a></td><td><strong>test/crud04</strong> —</td><td>&amp;birth= &amp;changed= &amp;decease= &amp;family= &amp;gender= &amp;name= &amp;place= &amp;remote_user= &amp;search_decease= &amp;search_family= &amp;search_name= &amp;update_count=</td></tr>
<tr><td><a href="servlet?&amp;spec=test.crud05">test.crud05</a></td><td><strong>test/crud05</strong> — Maintain Relatives (with field references)</td><td>&amp;birth= &amp;decease= &amp;family= &amp;gender= &amp;name= &amp;place= &amp;remote_user= &amp;search_decease= &amp;search_family= &amp;search_name= &amp;update_count= &amp;user=</td></tr>
<tr><td><a href="servlet?&amp;spec=test.crud06">test.crud06</a></td><td><strong>test/crud06</strong> —</td><td>&amp;birth= &amp;decease= &amp;family= &amp;gender= &amp;name= &amp;place= &amp;search_family= &amp;search_gender= &amp;search_name= &amp;update_count=</td></tr>
<tr><td><a href="servlet?&amp;spec=test.enum01">test.enum01</a></td><td><strong>enum01</strong> — Fetch Enumerations into Parameters</td><td>&amp;code=initcode &amp;code_count= &amp;display=initdisplay</td></tr>
<tr><td><a href="servlet?&amp;spec=test.enum02">test.enum02</a></td><td><strong>enum02</strong> — Fetch Enumerations and build dynamic listbox</td><td>&amp;gender=N</td></tr>
<tr><td><a href="servlet?&amp;spec=test.fragment01">test.fragment01</a></td><td><strong>fragment01</strong> — Select with fragment in link</td><td>&amp;name=%</td></tr>
<tr><td><a href="servlet?&amp;spec=test.grouping">test.grouping</a></td><td><strong>grouping</strong> — Test multiple header lines for control exchanges</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.highlight">test.highlight</a></td><td><strong>highlight</strong> — Column with Highlighting of a Keyword</td><td>&amp;keyword=er</td></tr>
<tr><td><a href="servlet?&amp;spec=test.image01">test.image01</a></td><td><strong>image01</strong> — Select from a Table with Images</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.image02">test.image02</a></td><td><strong>image01</strong> — Select from a Table with Pagebreaks</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.includer">test.includer</a></td><td><strong>includer</strong> — tests the inclusion of system entities</td><td>&amp;name=</td></tr>
<tr><td><a href="servlet?&amp;spec=test.index">test.index</a></td><td><strong>Test Specifications</strong> — List of the Specifications for Dbat Test Cases</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.label2">test.label2</a></td><td><strong>label2</strong> — Select from test table - with double line header</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.label2scroll">test.label2scroll</a></td><td><strong>label2scroll</strong> — Select from test table - with double line header and scrolling</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.label2sort">test.label2sort</a></td><td><strong>label2sort</strong> — Select from test table - with double line header and sorting</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.listbox">test.listbox</a></td><td><strong>listbox</strong> — List Box and List Parameter Test</td><td>&amp;birthchar= &amp;birthint= &amp;namebox=</td></tr>
<tr><td><a href="servlet?&amp;spec=test.listbox2">test.listbox2</a></td><td><strong>listbox</strong> — List Box and List Parameter Test</td><td>&amp;birthchar= &amp;birthint= &amp;namebox=</td></tr>
<tr><td><a href="servlet?&amp;spec=test.listboxmult">test.listboxmult</a></td><td><strong>listboxmult</strong> — List Box and List Parameter Test - multiple choice</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.minimal">test.minimal</a></td><td><strong></strong> —</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.minusconn">test.minusconn</a></td><td><strong>minusconn</strong> — Spec without Connection Id</td><td>&amp;conn= &amp;tabname=c01</td></tr>
<tr><td><a href="servlet?&amp;spec=test.multicss">test.multicss</a></td><td><strong>multijs</strong> — Test with 2 Stylesheets</td><td>&amp;name=%</td></tr>
<tr><td><a href="servlet?&amp;spec=test.multijs">test.multijs</a></td><td><strong>multijs</strong> — Test with 2 Javascripts</td><td>&amp;name=%</td></tr>
<tr><td><a href="servlet?&amp;spec=test.order01">test.order01</a></td><td><strong>order01</strong> — Select from test table with ORDER</td><td>&amp;name=%r</td></tr>
<tr><td><a href="servlet?&amp;spec=test.order02">test.order02</a></td><td><strong>order02</strong> — Select from test table with ORDER</td><td>&amp;name=%r</td></tr>
<tr><td><a href="servlet?&amp;spec=test.parmform">test.parmform</a></td><td><strong>parmform</strong> — Test of the format="..." attribute in &lt;parm&gt;</td><td>&amp;VALUE= &amp;value=</td></tr>
<tr><td><a href="servlet?&amp;spec=test.parmlink">test.parmlink</a></td><td><strong>parmlink</strong> — Test of an HTML Link with Parameter Substitution</td><td>&amp;name=M &amp;year=1995</td></tr>
<tr><td><a href="servlet?&amp;spec=test.pathstyle">test.pathstyle</a></td><td><strong>pathstyle</strong> — Test of the paths for stylesheet, javascript and xslt attributes</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.pivot03">test.pivot03</a></td><td><strong>pivot03</strong> — Pivot matrix output with linked values</td><td>&amp;name=%r</td></tr>
<tr><td><a href="servlet?&amp;spec=test.pivot04">test.pivot04</a></td><td><strong>pivot04</strong> — Pivot matrix output with linked values</td><td>&amp;name=%r</td></tr>
<tr><td><a href="servlet?&amp;spec=test.rewrite">test.rewrite</a></td><td><strong>rewrite</strong> — Rewriting of Parameters</td><td>&amp;DBSYS=</td></tr>
<tr><td><a href="servlet?&amp;spec=test.scroll1">test.scroll1</a></td><td><strong>scroll1</strong> — Single table with both scrollbars</td><td>&amp;name=%</td></tr>
<tr><td><a href="servlet?&amp;spec=test.scrollmm">test.scrollmm</a></td><td><strong>scrollmm</strong> — Sortable tables with both scrollbars</td><td>&amp;name=%</td></tr>
<tr><td><a href="servlet?&amp;spec=test.scrollpx">test.scrollpx</a></td><td><strong>scrollpx</strong> — Test table with both scrollbars</td><td>&amp;name=%</td></tr>
<tr><td><a href="servlet?&amp;spec=test.selec01">test.selec01</a></td><td><strong>selec01</strong> — Select from test table</td><td>&amp;name=%r</td></tr>
<tr><td><a href="servlet?&amp;spec=test.selec02">test.selec02</a></td><td><strong>selec02</strong> — Select from test table - with linked values</td><td>&amp;name=%</td></tr>
<tr><td><a href="servlet?&amp;spec=test.selec03">test.selec03</a></td><td><strong>selec03</strong> — Two SELECTs from test table</td><td>&amp;birth= &amp;name=%r</td></tr>
<tr><td><a href="servlet?&amp;spec=test.selec04">test.selec04</a></td><td><strong>selec04</strong> — Select from a CLOB table</td><td>&amp;name=%r</td></tr>
<tr><td><a href="servlet?&amp;spec=test.selec05">test.selec05</a></td><td><strong>selec05</strong> — Select from test table - with <read> element</td><td>&amp;name=%</td></tr>
<tr><td><a href="servlet?&amp;spec=test.selec06">test.selec06</a></td><td><strong>selec06</strong> — Select from test table - sometimes with Javascript</td><td>&amp;name=%</td></tr>
<tr><td><a href="servlet?&amp;spec=test.selec07">test.selec07</a></td><td><strong>selec07</strong> — Select from test table - with target="_blank" attribute</td><td>&amp;name=%</td></tr>
<tr><td><a href="servlet?&amp;spec=test.selec08">test.selec08</a></td><td><strong>selec08</strong> — Select from test table - with pseudo="sort" attribute</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.selecde1">test.selecde1</a></td><td><strong>selecde1</strong> — Select with date, decimal, and timestamp</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.selnull">test.selnull</a></td><td><strong>selnull</strong> — Select with NULL values</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.selvalid">test.selvalid</a></td><td><strong>selvalid</strong> — Select with input field validation</td><td>&amp;end=2000 &amp;start=1900</td></tr>
<tr><td><a href="servlet?&amp;spec=test.stylesheet1">test.stylesheet1</a></td><td><strong>stylesheet1</strong> — Explicit Stylesheet "/stylesheet.css"</td><td>&amp;name=%r</td></tr>
<tr><td><a href="servlet?&amp;spec=test.switchconn">test.switchconn</a></td><td><strong>switchconn</strong> — Switch to another database connection</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.taylor5">test.taylor5</a></td><td><strong>taylor5</strong> — Select from test table and replace variables in pattern file (file tayloring)</td><td>&amp;name=</td></tr>
<tr><td><a href="servlet?&amp;spec=test.textarea">test.textarea</a></td><td><strong>textarea</strong> — Test of an input text area</td><td>&amp;WORDLIST= &amp;wordlist=</td></tr>
<tr><td><a href="servlet?&amp;spec=test.trail01">test.trail01</a></td><td><strong>trail01</strong> — No Trailer</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.trail02">test.trail02</a></td><td><strong>trail02</strong> — Trailer with "out dbat"</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.trail03">test.trail03</a></td><td><strong>trail03</strong> — Trailer with "out dbat script"</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.trail04">test.trail04</a></td><td><strong>trail04</strong> — Trailer with "out dbat script xls"</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.trail05">test.trail05</a></td><td><strong>trail05</strong> — Trailer with "out dbat script xls more"</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.trail06">test.trail06</a></td><td><strong>trail06</strong> — Trailer with "out dbat more"</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.uml01">test.uml01</a></td><td><strong>test/uml01</strong> — Test of URL encoding with accented letters</td><td>&amp;name=%</td></tr>
<tr><td><a href="servlet?&amp;spec=test.var01">test.var01</a></td><td><strong>test/var01</strong> — Test of the var element</td><td>&amp;name=%</td></tr>
<tr><td><a href="servlet?&amp;spec=test.var02">test.var02</a></td><td><strong>test/var02</strong> — Test of the var element with data types</td><td>&amp;birth=1800-01-01 &amp;changed=1900-01-01 00:00:00 &amp;decease=1800 &amp;name=A</td></tr>
<tr><td><a href="servlet?&amp;spec=test.var03">test.var03</a></td><td><strong>test/var03</strong> — Test of the var element with data types</td><td>&amp;birth=1800-01-01 &amp;changed=1900-01-01 00:00:00 &amp;decease=1800 &amp;name=A</td></tr>
<tr><td><a href="servlet?&amp;spec=test.visible05">test.visible05</a></td><td><strong>visible05</strong> — Select from test table - suppress subsequent column values in groups</td><td></td></tr>
<tr><td><a href="servlet?&amp;spec=test.with_cte">test.with_cte</a></td><td><strong>with_cte</strong> — test the SQL &lt;with&gt; Syntax for Common Table Expressions</td><td>&amp;prefix=backe</td></tr>
<tr><td><a href="servlet?&amp;spec=test.xslt_brackets">test.xslt_brackets</a></td><td><strong>xslt_brackets</strong> — Select from test table with XSLT</td><td>&amp;name=%r</td></tr>
<tr><td class="counter" colspan="3">81 Specifications</td></tr>
</table>
</div>

    
    
    
<br />Output by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/area.xml" type="text/plain">area</a>

</body></html>
