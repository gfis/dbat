java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -m xml -d c01
<?xml version="1.0" encoding="UTF-8"?>
<dbat>
<create type="TABLE" name="c01">
	<column name="name" type="VARCHAR" width="16" remark="Name of the Relative" />
	<column name="univ" type="VARCHAR" width="16" nullable="false" remark="Town" />
	<column name="year" type="INT" nullable="false" remark="Decease Year" />
	<column name="gender" type="CHAR" width="1" nullable="false" remark="some escaped character" />
	<column name="birth" type="DATE" nullable="false" />
	<constraint name="PK29" type="primary">
			<key name="name" />
	</constraint>

</create>
</dbat>
