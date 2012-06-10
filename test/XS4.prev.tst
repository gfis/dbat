xsltproc --stringparam alter 0 ../etc/xslt/dbiv_spec.xsl ../web/spec/test/crud01.iv.xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
	Interactive view definition for test form crud01.xml, with INT key
    @(#) $Id$
    2012-05-21: with help URLs on field names
    2012-05-02, Dr. Georg Fischer
-->
<!--test/crud01.xml äöü
	@(#) $Id$
	Generated by dbat/etc/xslt/dbiv_spec.xsl V1.2 at 2012-06-01T22:52:25+02:00
	DO NOT EDIT HERE, but in test/crud01.iv.xml instead!
-->
<dbat xmlns="http://www.teherba.org/2007/dbat" xmlns:ht="http://www.w3.org/1999/xhtml" xmlns:iv="http://www.teherba.org/2011/dbiv" xmlns:db="http://www.teherba.org/2007/dbat" encoding="UTF-8" conn="worddb" lang="en" title="test/crud01">
	<!--
	    param "view" takes the following values:
	    sear - search form only
	    del  - confirmation of deletion
	    del2 - SQL DELETE + search form
	    ins  - insert form
	    ins2 - SQL INSERT + search form
	    upd  - update form
	    upd2 - SQL UPDATE + search form
	--><ht:h3><ht:a href="servlet?spec=test.index">Test</ht:a> Form - Relatives (with INT key)
	</ht:h3><ht:p>
		You may search for relatives and update / delete / insert their data.
	</ht:p>
	
	<ht:h2/>

<!--== s e a r  etc. ==============================-->
<choose>
	<when name="view" match="(sear|ins2|upd2|del2|)">
		<ht:form method="post">
			<ht:input name="view" type="hidden" value="sear"/>
			<ht:table>
			<ht:tr><ht:td valign="top" title="NAME" id="na0">Name:</ht:td><ht:td id="na1"><ht:input name="SEARCH_CRIT_1" size="40" init="" id="na2" title="[\w\-%]*" onkeyup="this.form.NAME.className = (this.form.NAME.value.match('^[\\w\\-%]*$')) ? 'valid': 'invalid'"/> Letters and "-"</ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="decease">Died:</ht:td><ht:td><ht:input name="search_crit_2" size="4" init="" title="\d{4}" onkeyup="this.form.decease.className = (this.form.decease.value.match('^\\d{4}$')) ? 'valid': 'invalid'"/> yyyy</ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="family">Family:</ht:td><ht:td><ht:select name="search_crit_3" init="" size="3">
				<ht:option value="">(any)</ht:option>
				<ht:option value="Ritter">Ritter Family</ht:option>
				<ht:option value="Fischer">Fischer Family</ht:option>
				</ht:select></ht:td></ht:tr>
			</ht:table>
			<ht:input name="gender" type="hidden" init="M"/>
			<ht:input name="birth" type="hidden" init=""/>
			<ht:input name="place" type="hidden" init=""/>
			<ht:input name="changed" type="hidden" init=""/>
			<ht:input name="user" type="hidden" init=""/>
			<ht:input type="submit" value="Search"/>
		</ht:form><ht:br/>
		<select limit="2048">
			<col pseudo="image" label="Show" align="center" remark="details" link="test/crud01&amp;SEARCH_CRIT_1=&amp;search_crit_2=&amp;search_crit_3=&amp;NAME=&amp;decease=&amp;family=&amp;view=">
				'<parm name="SEARCH_CRIT_1"/>' || '=' 
				 || '<parm name="search_crit_2"/>' || '=' 
				 || '<parm name="search_crit_3"/>' || '=' 
				 || NAME || '=' 
				 || CAST(decease AS CHAR(4)) || '=' 
				 || family || '=' || 'dat' 
			</col>
			<col name="NAME" label="Name" href="servlet?&amp;spec=test.crud03&amp;search_crit_2=&amp;search_crit_1=">family || '=' || name
			</col>
			<col name="decease" label="Died">
				decease
			</col>
			<col name="family" label="Family">family
			</col>
			<col name="gender" label="Gender" align="center">gender
			</col>
			<col name="birth" label="Birthdate">
				birth
			</col>
			<col name="place" label="Place" href="http://de.wikipedia.org/index.php?&amp;title=">
				place
			</col>
			<col name="changed" label="Changed" style="wht">
				changed
			</col>
			<from>relatives r</from>
			<where>NAME like '<parm name="SEARCH_CRIT_1"/>%'
				and CAST(decease AS CHAR(4)) like '<parm name="search_crit_2"/>%'
				and family like '<parm name="search_crit_3"/>%'
			</where>
			<order by="1,2"/>
			<counter desc="Person,s"/>
		</select>
	</when>
	<when name="view" match="(sear|)">
		<ht:h3>Statistics</ht:h3><db:select><db:col label="Count" align="right">count(*)</db:col>
				<db:col label="Min. Birth" align="center">min(birth)		</db:col>
				<db:col label="Max. Birth" align="center">max(birth)		</db:col>
				<db:col label="Min. Decease" align="center">min(decease)	</db:col>
				<db:col label="Max. Decease" align="center">max(decease)	</db:col>
				<db:from>relatives
		</db:from>
				</db:select>
				
	</when>
</choose>

</dbat>
