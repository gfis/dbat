xsltproc ../etc/xslt/dbiv_spec.xsl ../web/spec/test/crud01.iv.xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
	Interactive view definition for test form crud01.xml, with INT key
    @(#) $Id$
    2012-05-21: with help URLs on field names
    2012-05-02, Dr. Georg Fischer
-->
<!--test/crud01.xml äöü
	@(#) $Id$
	Generated by dbat/etc/xslt/dbiv_spec.xsl V1.2 at 2012-06-01T22:56:12+02:00
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
<choose>
	<when name="view" match="sear">
	</when><!--== d e l ==============-->
	<when name="view" match="del">
		<select limit="512">
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
			<where>NAME = '<parm name="NAME"/>'
				and decease = <parm name="decease"/>
				and family = '<parm name="family"/>'
			</where>
		</select>
		<ht:form method="post">
			<ht:input name="view" type="hidden" value="del2"/>
			<ht:input name="opcode" type="hidden" value="del"/>
			<ht:input name="SEARCH_CRIT_1" type="hidden" init=""/>
			<ht:input name="search_crit_2" type="hidden" init=""/>
			<ht:input name="search_crit_3" type="hidden" init=""/>
			<ht:input name="NAME" type="hidden" init=""/>
			<ht:input name="decease" type="hidden" init=""/>
			<ht:input name="family" type="hidden" init=""/>
			<ht:input name="gender" type="hidden" init="M"/>
			<ht:input name="birth" type="hidden" init=""/>
			<ht:input name="place" type="hidden" init=""/>
			<ht:input name="changed" type="hidden" init=""/>
			<ht:input name="user" type="hidden" init=""/>
			<ht:input type="submit" value="Delete"/> <ht:a href="servlet?spec=test/crud01">Back</ht:a> to the search form
		</ht:form>
	</when><!--== d e l 2 ==============-->
	<when name="view" match="del2">
		<delete><from>relatives r</from>
			<where>NAME = '<parm name="NAME"/>'
				and decease = <parm name="decease"/>
				and family = '<parm name="family"/>'
			</where>
		</delete>
		<ht:h4><parm name="update_count"/> row(s) for key
			 <ht:em> <parm name="NAME"/></ht:em>
			 <ht:em> <parm name="decease"/></ht:em>
			 <ht:em> <parm name="family"/></ht:em> deleted
		</ht:h4>
	</when><!--== d a t ==============-->
	<when name="view" match="dat">
		<select into="parm">
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
			<where>NAME = '<parm name="NAME"/>'
				and decease = <parm name="decease"/>
				and family = '<parm name="family"/>'
			</where>
		</select>
		<ht:form name="form1" method="post">
			<ht:table>
			<ht:tr><ht:td valign="top" title="NAME" id="na0">Name:</ht:td><ht:td id="na1"><parm name="NAME"/></ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="decease">Died:</ht:td><ht:td><parm name="decease"/></ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="family">Family:</ht:td><ht:td><parm name="family"/></ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="gender">Gender:</ht:td><ht:td><parm name="gender"/></ht:td></ht:tr>
			<ht:tr><ht:td colspan="2" align="center" class="wht" id="or0"><ht:a href="http://en.wiktionary.org/wiki/origin">Origin</ht:a> of the Person
	    	</ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="birth">Birthdate:</ht:td><ht:td><parm name="birth"/></ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="place">Place:</ht:td><ht:td><parm name="place"/></ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="changed" class="wht">Changed:</ht:td><ht:td class="wht"><parm name="changed"/></ht:td></ht:tr>
			</ht:table>
			<ht:a href="servlet?spec=test/crud01">Back</ht:a> to the search form
		</ht:form>
	</when><!--== i n s ==============-->
	<when name="view" match="ins">
		<ht:h4>Enter a new Person</ht:h4>
		<ht:form name="form1" method="post">
			<ht:input name="view" type="hidden" value="ins2"/>
			<ht:input name="opcode" type="hidden" value="ins"/>
			<ht:input name="SEARCH_CRIT_1" type="hidden" init=""/>
			<ht:input name="search_crit_2" type="hidden" init=""/>
			<ht:input name="search_crit_3" type="hidden" init=""/>
			<ht:table>
			<ht:tr><ht:td valign="top" title="NAME" id="na0">Name:</ht:td><ht:td id="na1"><ht:input name="NAME" size="40" init="" id="na2" title="[\w\-%]*" onkeyup="this.form.NAME.className = (this.form.NAME.value.match('^[\\w\\-%]*$')) ? 'valid': 'invalid'"/> Letters and "-"</ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="decease">Died:</ht:td><ht:td><ht:input name="decease" size="4" init="" title="\d{4}" onkeyup="this.form.decease.className = (this.form.decease.value.match('^\\d{4}$')) ? 'valid': 'invalid'"/> yyyy</ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="family">Family:</ht:td><ht:td><ht:select name="family" init="" size="2">
				<ht:option value="Ritter">Ritter Family</ht:option>
				<ht:option value="Fischer">Fischer Family</ht:option>
				</ht:select></ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="gender">Gender:</ht:td><ht:td>
				<db:select distinct="yes" into="parm">
					<db:col name="code">gender</db:col><db:col name="display">case 
						when gender = 'M' then 'male' 
						else                   'female' end 
					</db:col><db:from>relatives</db:from><db:order by="1"/>
				</db:select>
				<db:listbox height="2" name="gender" init="M" code="code" display="display"/>
				</ht:td></ht:tr>
			<ht:tr><ht:td colspan="2" align="center" class="wht" id="or0"><ht:a href="http://en.wiktionary.org/wiki/origin">Origin</ht:a> of the Person
	    	</ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="birth">Birthdate:</ht:td><ht:td><ht:input name="birth" size="10" init="" title="\d{4}\-\d{2}\-\d{2}" onkeyup="this.form.birth.className = (this.form.birth.value.match('^\\d{4}\\-\\d{2}\\-\\d{2}$')) ? 'valid': 'invalid'"/> yyyy-mm-dd</ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="place">Place:</ht:td><ht:td><ht:textarea name="place" cols="20" rows="5" init="" title="\w[\w \.\-\(\)]*" onkeyup="this.form.place.className = (this.form.place.value.match('^\\w[\\w \\.\\-\\(\\)]*$')) ? 'valid': 'invalid'"/> Letters space . - ( )</ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="changed" class="wht">Changed:</ht:td><ht:td class="wht"><parm name="changed"/></ht:td></ht:tr>
			</ht:table>
			<ht:input type="submit" value="Save"/> <ht:a href="servlet?spec=test/crud01">Back</ht:a> to the search form
		</ht:form>
	</when><!--== i n s 2 ==============-->
	<when name="view" match="ins2">
		<insert><into>relatives</into>
			<values>
			<col name="NAME">'<parm name="NAME"/>'</col>
			<col name="decease"><parm name="decease"/></col>
			<col name="family">'<parm name="family"/>'</col>
			<col name="gender">'<parm name="gender"/>'</col>
			<col name="birth">'<parm name="birth"/>'</col>
			<col name="place">'<parm name="place"/>'</col>
			<col name="changed">current_timestamp</col>
			<col name="user">'<parm name="remote_user"/>'</col>
			</values>
		</insert>
	</when><!--== u p d ==============-->
	<when name="view" match="upd">
		<select into="parm">
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
			<where>NAME = '<parm name="NAME"/>'
				and decease = <parm name="decease"/>
				and family = '<parm name="family"/>'
			</where>
		</select>
		<ht:h4>Update Person</ht:h4>
		<ht:form name="form1" method="post">
			<ht:input name="view" type="hidden" value="upd2"/>
			<ht:input name="opcode" type="hidden" value="upd"/>
			<ht:input name="SEARCH_CRIT_1" type="hidden" init=""/>
			<ht:input name="search_crit_2" type="hidden" init=""/>
			<ht:input name="search_crit_3" type="hidden" init=""/>
			<ht:table>
			<ht:input name="NAME" type="hidden" init=""/>
			<ht:input name="decease" type="hidden" init=""/>
			<ht:input name="family" type="hidden" init=""/>
			<ht:tr><ht:td valign="top" title="NAME" id="na0">Name:</ht:td><ht:td id="na1"><parm name="NAME"/></ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="decease">Died:</ht:td><ht:td><parm name="decease"/></ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="family">Family:</ht:td><ht:td><parm name="family"/></ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="gender">Gender:</ht:td><ht:td>
				<db:select distinct="yes" into="parm">
					<db:col name="code">gender</db:col><db:col name="display">case 
						when gender = 'M' then 'male' 
						else                   'female' end 
					</db:col><db:from>relatives</db:from><db:order by="1"/>
				</db:select>
				<db:listbox height="2" name="gender" init="M" code="code" display="display"/>
				</ht:td></ht:tr>
			<ht:tr><ht:td colspan="2" align="center" class="wht" id="or0"><ht:a href="http://en.wiktionary.org/wiki/origin">Origin</ht:a> of the Person
	    	</ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="birth">Birthdate:</ht:td><ht:td><ht:input name="birth" size="10" init="" title="\d{4}\-\d{2}\-\d{2}" onkeyup="this.form.birth.className = (this.form.birth.value.match('^\\d{4}\\-\\d{2}\\-\\d{2}$')) ? 'valid': 'invalid'"/> yyyy-mm-dd</ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="place">Place:</ht:td><ht:td><ht:textarea name="place" cols="20" rows="5" init="" title="\w[\w \.\-\(\)]*" onkeyup="this.form.place.className = (this.form.place.value.match('^\\w[\\w \\.\\-\\(\\)]*$')) ? 'valid': 'invalid'"/> Letters space . - ( )</ht:td></ht:tr>
			<ht:tr><ht:td valign="top" title="changed" class="wht">Changed:</ht:td><ht:td class="wht"><parm name="changed"/></ht:td></ht:tr>
			</ht:table>
			<ht:input type="submit" value="Update"/> <ht:a href="servlet?spec=test/crud01">Back</ht:a> to the search form
		</ht:form>
	</when><!--== u p d 2 ==============-->
	<when name="view" match="upd2">
		<update>relatives
			<col name="gender">'<parm name="gender"/>'</col>
			<col name="birth">'<parm name="birth"/>'</col>
			<col name="place">'<parm name="place"/>'</col>
			<col name="changed">current_timestamp</col>
			<col name="user">'<parm name="remote_user"/>'</col>
			<where>NAME = '<parm name="NAME"/>'
				and decease = <parm name="decease"/>
				and family = '<parm name="family"/>'
			</where>
		</update>
		<ht:h4><parm name="update_count"/> row(s) for
			 <ht:em> <parm name="NAME"/></ht:em>
			 <ht:em> <parm name="decease"/></ht:em>
			 <ht:em> <parm name="family"/></ht:em> updated
		</ht:h4>
	</when>
</choose>
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
		<ht:form method="post">
			<ht:input name="view" type="hidden" value="ins"/>
			<ht:input name="SEARCH_CRIT_1" type="hidden" init=""/>
			<ht:input name="search_crit_2" type="hidden" init=""/>
			<ht:input name="search_crit_3" type="hidden" init=""/>
			<ht:input name="gender" type="hidden" init="M"/>
			<ht:input name="birth" type="hidden" init=""/>
			<ht:input name="place" type="hidden" init=""/>
			<ht:input name="changed" type="hidden" init=""/>
			<ht:input name="user" type="hidden" init=""/>
			<ht:input type="submit" value="New Person"/>
		</ht:form>
		<select limit="2048">
			<col pseudo="image" label="Upd." align="center" remark="update" link="test/crud01&amp;SEARCH_CRIT_1=&amp;search_crit_2=&amp;search_crit_3=&amp;NAME=&amp;decease=&amp;family=&amp;view=">
				'<parm name="SEARCH_CRIT_1"/>' || '=' 
				 || '<parm name="search_crit_2"/>' || '=' 
				 || '<parm name="search_crit_3"/>' || '=' 
				 || NAME || '=' 
				 || CAST(decease AS CHAR(4)) || '=' 
				 || family || '=' || 'upd' 
			</col>
			<col pseudo="image" label="Del." align="center" remark="delete" link="test/crud01&amp;SEARCH_CRIT_1=&amp;search_crit_2=&amp;search_crit_3=&amp;NAME=&amp;decease=&amp;family=&amp;view=">
				'<parm name="SEARCH_CRIT_1"/>' || '=' 
				 || '<parm name="search_crit_2"/>' || '=' 
				 || '<parm name="search_crit_3"/>' || '=' 
				 || NAME || '=' 
				 || CAST(decease AS CHAR(4)) || '=' 
				 || family || '=' || 'del' 
			</col>
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
