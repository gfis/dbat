<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>image01</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
<script src="spec/test/../javascript.js" type="text/javascript"></script>
</head><body>
<!--  -->
<!-- SpecificationHandler.parameterMap: spec->test.image01
lang->en
 -->

    
    <!--Select from a Table with Images-->
    <!--Abfrage einer Tabelle mit Bildern-->

    <h3><a href="servlet?spec=test.index">Test</a>: Select from table with images</h3>
    <!-- SpecificationHandler.SELECT, aggregateIndex=-3 -->
<!-- SQL:
SELECT 'showImage,' || imagefile || ',100'
, imagename
, bytesize
, description 
FROM im1 
ORDER BY 1;
:SQL -->
<table id="tab1"><!-- table_not_specified -->
<tr><th title="'showImage,' || imagefile || ',100'">Image</th><th title="imagename">Name</th><th title="bytesize">Size</th><th title="description">Verbatim Description</th></tr>
<tr><td><script type="text/javascript">showImage("img/flowers/120px-Flower_in_Georgia.jpg","100");</script></td><td>120px-Flower_in_Georgia</td><td align="right">4964</td><td><span class="F">blue</span> flower</td></tr>
<tr><td><script type="text/javascript">showImage("img/flowers/120px-Georgian_Snow_Rose.jpg","100");</script></td><td>120px-Georgian_Snow_Rose</td><td align="right">5006</td><td>Snow Rose</td></tr>
<tr><td><script type="text/javascript">showImage("img/flowers/120px-Orange_flower_of_Georgia.jpg","100");</script></td><td>120px-Orange_flower_of_Georgia</td><td align="right">3101</td><td><span class="B">orange</span> flower</td></tr>
<tr><td><script type="text/javascript">showImage("img/flowers/120px-Tusheti_Flower.jpg","100");</script></td><td>120px-Tusheti_Flower</td><td align="right">3714</td><td>violet&#xa0;flower (hard space entity)</td></tr>
<tr><td><script type="text/javascript">showImage("img/flowers/120px-Xtidaylily-0575.jpg","100");</script></td><td>120px-Xtidaylily-0575</td><td align="right">8446</td><td><strong>purple<br />flower</strong></td></tr>
<tr><td><script type="text/javascript">showImage("img/flowers/90px-Flower_in_Georgia.jpg","100");</script></td><td>90px-Flower_in_Georgia</td><td align="right">5426</td><td><em>rose<br />flower</em></td></tr>
<tr><td class="counter" colspan="4">6 Flowers</td></tr>
</table>

    <p>
        The images were copied from
        <a href="https://commons.wikimedia.org/wiki/Category:Flowers_of_Georgia" target="_new">
        Category:Flowers_of_Georgia in commons.wikimedia.org
        </a>.
        <br />They are licensed there unter a "Attribution - Share alike"
        <a href="https://creativecommons.org/licenses/by-sa/2.0/deed.en" target="_new">Creative Commons</a> License.
    </p>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/image01.xml" type="text/plain">test/image01</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;spec=test.image01&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test.image01&amp;lang=en">more</a>

</body></html>
