<?xml version="1.0" encoding="UTF-8" ?>
<dbat encoding="UTF-8"
	xmlns="http://www.teherba.org/2007/dbat"
	xmlns:ht="http://www.w3.org/1999/xhtml"
	>
<!--
	@(#) S/Id/S
-->
	<ht:h2>c01</ht:h2>
	<ht:form method="get">
		<ht:input label="Prefix" name="prefix" maxsize="20" size="10" />
		<ht:input type="submit" value="Submit" />
	</ht:form>
	<select>
		<col label="name">a.name</col>
		<col label="univ">a.univ</col>
		<col label="year" align="right">a.year</col>
		<col label="gender">a.gender</col>
		<col label="birth">a.birth</col>
	<constraint name="PK29" type="primary">
			<key name="name" />
	</constraint>

	</select>
	<counter />
</dbat>
