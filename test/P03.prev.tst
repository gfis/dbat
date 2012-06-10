java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -a sp2 -sa "pivot" -m html "select sp1, sp2, sp3 from pivot order by 1, 2"
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml" />
<meta name="robots" content="noindex, nofollow" />
<title>dbat</title>
<link rel="stylesheet" type="text/css" href="stylesheet.css" />
</head><body>
<!-- SQL:
select sp1, sp2, sp3 from pivot order by 1, 2
:SQL -->
<table id="tab1"><!-- table_not_specified -->
<tr><th>sp1</th><th>S1</th><th>S2</th><th>S3</th><th>S4</th><th>S5</th></tr>
<tr><td>A</td><td>A1</td><td>A2</td><td>A3</td><td>A4</td><td>A5</td></tr>
<tr><td>B</td><td>B1</td><td>B2</td><td>B3</td><td>B4</td><td>B5</td></tr>
<tr><td>C</td><td>C1</td><td>C2</td><td>C3</td><td>C4</td><td>C5</td></tr>
<tr><td>D</td><td>D1</td><td>D2</td><td>D3</td><td>D4</td><td>D5</td></tr>
</table>
</body></html>
