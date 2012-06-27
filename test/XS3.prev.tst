xsltproc ../etc/xslt/dbiv_spec.xsl ../web/spec/test/crud03.iv.xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
	Interactive view definition for test form crud03.xml
    @(#) $Id$
    2012-03-15: type="action" fields
    2012-02-27, Dr. Georg Fischer
-->
<!--test/crud03.xml äöü
  @(#) $Id$
  Generated by dbat/etc/xslt/dbiv_spec.xsl V1.4 on yyyy-mm-dd hh:mm:ss
  DO NOT EDIT HERE, but in test/crud03.iv.xml instead!
-->
<dbat xmlns="http://www.teherba.org/2007/dbat" xmlns:ht="http://www.w3.org/1999/xhtml" xmlns:iv="http://www.teherba.org/2011/dbiv" xmlns:db="http://www.teherba.org/2007/dbat" encoding="UTF-8" conn="worddb" lang="en" title="test/crud03">
  <!--
        param "view" takes the following values:
        sear - search form only
        del  - confirmation of deletion
        del2 - SQL DELETE + search form
        ins  - insert form
        ins2 - SQL INSERT + search form
        upd  - update form
        upd2 - SQL UPDATE + search form
    -->
  
  <ht:h2><ht:a href="servlet?spec=test.index">Test</ht:a> Form - Relatives</ht:h2>
<choose>
  <when name="view" match="sear">
  </when><!--== d e l ==============-->
  <when name="view" match="del">
    <select limit="512">
      <col name="name" label="Name">
        name
      </col>
      <col name="family" label="Family">family
      </col>
      <col name="gender" label="Gender" align="center">gender
      </col>
      <col name="birth" label="Birthdate">
        birth
      </col>
      <col name="place" label="Place">
        place
      </col>
      <col name="decease" label="Died" align="right">
        decease
      </col>
      <from>relatives </from>
      <where>name = '<parm name="name"/>'
        and family = '<parm name="family"/>'
        and gender = '<parm name="gender"/>'
      </where>
    </select>
    <ht:form method="post">
      <ht:input name="view" type="hidden" value="del2"/>
      <ht:input name="opcode" type="hidden" value="del"/>
      <ht:input name="search_name" type="hidden" init=""/>
      <ht:input name="search_family" type="hidden" init=""/>
      <ht:input name="search_gender" type="hidden" init="M"/>
      <ht:input name="name" type="hidden" init=""/>
      <ht:input name="family" type="hidden" init=""/>
      <ht:input name="gender" type="hidden" init="M"/>
      <ht:input name="birth" type="hidden" init=""/>
      <ht:input name="place" type="hidden" init=""/>
      <ht:input name="decease" type="hidden" init=""/>
      <ht:input name="user" type="hidden" init=""/>
      <ht:input type="submit" value="Delete"/> <ht:a href="servlet?spec=test/crud03">Back</ht:a> to the search form
    </ht:form>
  </when><!--== d e l 2 ==============-->
  <when name="view" match="del2">
    <delete><from>relatives </from>
      <where>name = '<parm name="name"/>'
        and family = '<parm name="family"/>'
        and gender = '<parm name="gender"/>'
      </where>
    </delete>
    <ht:h4><parm name="update_count"/> row(s) for key
       <ht:em> <parm name="name"/></ht:em>
       <ht:em> <parm name="family"/></ht:em>
       <ht:em> <parm name="gender"/></ht:em> deleted
    </ht:h4>
  </when><!--== d a t ==============-->
  <when name="view" match="dat">
    <select into="parm">
      <col name="birth" label="Birthdate">
        birth
      </col>
      <col name="place" label="Place">
        place
      </col>
      <col name="decease" label="Died" align="right">
        decease
      </col>
      <from>relatives </from>
      <where>name = '<parm name="name"/>'
        and family = '<parm name="family"/>'
        and gender = '<parm name="gender"/>'
      </where>
    </select>
    <ht:form name="form1" method="post">
      <ht:table>
      <ht:tr><ht:td valign="top" title="name">Name:</ht:td>
        <ht:td><parm name="name"/>
        </ht:td></ht:tr>
      <ht:tr><ht:td valign="top" title="family">Family:</ht:td>
        <ht:td><parm name="family"/>
        </ht:td></ht:tr>
      <ht:tr><ht:td valign="top" title="gender">Gender:</ht:td>
        <ht:td><parm name="gender"/>
        </ht:td></ht:tr>
      <ht:tr><ht:td valign="top" title="birth">Birthdate:</ht:td>
        <ht:td><parm name="birth"/>
        </ht:td></ht:tr>
      <ht:tr><ht:td valign="top" title="place">Place:</ht:td>
        <ht:td><parm name="place"/>
        </ht:td></ht:tr>
      <ht:tr><ht:td valign="top" title="decease">Died:</ht:td>
        <ht:td><parm name="decease"/>
        </ht:td></ht:tr>
      </ht:table>
      <ht:a href="servlet?spec=test/crud03">Back</ht:a> to the search form
    </ht:form>
  </when><!--== i n s ==============-->
  <when name="view" match="ins">
    <ht:h4>New Relative</ht:h4>
    <ht:form name="form1" method="post">
      <ht:input name="view" type="hidden" value="ins2"/>
      <ht:input name="opcode" type="hidden" value="ins"/>
      <ht:input name="search_name" type="hidden" init=""/>
      <ht:input name="search_family" type="hidden" init=""/>
      <ht:input name="search_gender" type="hidden" init="M"/>
      <ht:table>
      <ht:tr><ht:td valign="top" title="name">Name:</ht:td>
        <ht:td><ht:input name="name" size="40" init="" title="[\w\-%]*" onkeyup="this.form.name.className = (this.form.name.value.match('^[\\w\\-%]*$')) ? 'valid': 'invalid'"/> Letters and "-"
        </ht:td></ht:tr>
      <ht:tr><ht:td valign="top" title="family">Family:</ht:td>
        <ht:td><ht:select name="family" init="" size="2">
        <ht:option value="Ritter">Ritter Family</ht:option>
        <ht:option value="Fischer">Fischer Family</ht:option>
        </ht:select>
        </ht:td></ht:tr>
      <ht:tr><ht:td valign="top" title="gender">Gender:</ht:td>
        <ht:td>
        <db:select distinct="yes" into="parm">
          <db:col name="code">gender</db:col><db:col name="display">case 
						when gender = 'M' then 'male' 
						else                   'female' end 
					</db:col><db:from>relatives</db:from><db:order by="1"/>
        </db:select>
        <db:listbox height="2" name="gender" init="M" code="code" display="display"/>
        
        </ht:td></ht:tr>
      <ht:tr><ht:td valign="top" title="birth">Birthdate:</ht:td>
        <ht:td><ht:input name="birth" size="10" init="" title="\d{4}\-\d{2}\-\d{2}" onkeyup="this.form.birth.className = (this.form.birth.value.match('^\\d{4}\\-\\d{2}\\-\\d{2}$')) ? 'valid': 'invalid'"/> yyyy-mm-dd
        </ht:td></ht:tr>
      <ht:tr><ht:td valign="top" title="place">Place:</ht:td>
        <ht:td><ht:textarea name="place" cols="20" rows="5" init="" title="\w[\w \.\-\(\)]*" onkeyup="this.form.place.className = (this.form.place.value.match('^\\w[\\w \\.\\-\\(\\)]*$')) ? 'valid': 'invalid'"/> Letters space . - ( )
        </ht:td></ht:tr>
      <ht:tr><ht:td valign="top" title="decease">Died:</ht:td>
        <ht:td><ht:input name="decease" size="4" init="" title="\d{4}" onkeyup="this.form.decease.className = (this.form.decease.value.match('^\\d{4}$')) ? 'valid': 'invalid'"/> Year yyyy
        </ht:td></ht:tr>
      </ht:table>
      <ht:input type="submit" value="Save"/> <ht:a href="servlet?spec=test/crud03">Back</ht:a> to the search form
    </ht:form>
  </when><!--== i n s 2 ==============-->
  <when name="view" match="ins2">
    <insert><into>relatives</into>
      <values>
      <col name="name">'<parm name="name"/>'</col>
      <col name="family">'<parm name="family"/>'</col>
      <col name="gender">'<parm name="gender"/>'</col>
      <col name="birth">'<parm name="birth"/>'</col>
      <col name="place">'<parm name="place"/>'</col>
      <col name="decease"><parm name="decease"/></col>
      <col name="user">'<parm name="remote_user"/>'</col>
      </values>
    </insert>
  </when><!--== u p d ==============-->
  <when name="view" match="upd">
    <select into="parm">
      <col name="birth" label="Birthdate">
        birth
      </col>
      <col name="place" label="Place">
        place
      </col>
      <col name="decease" label="Died" align="right">
        decease
      </col>
      <from>relatives </from>
      <where>name = '<parm name="name"/>'
        and family = '<parm name="family"/>'
        and gender = '<parm name="gender"/>'
      </where>
    </select>
    <ht:h4>Update Person</ht:h4>
    <ht:form name="form1" method="post">
      <ht:input name="view" type="hidden" value="upd2"/>
      <ht:input name="opcode" type="hidden" value="upd"/>
      <ht:input name="search_name" type="hidden" init=""/>
      <ht:input name="search_family" type="hidden" init=""/>
      <ht:input name="search_gender" type="hidden" init="M"/>
      <ht:table>
      <ht:input name="name" type="hidden" init=""/>
      <ht:input name="family" type="hidden" init=""/>
      <ht:input name="gender" type="hidden" init="M"/>
      <ht:tr><ht:td valign="top" title="name">Name:</ht:td>
        <ht:td><parm name="name"/>
        </ht:td></ht:tr>
      <ht:tr><ht:td valign="top" title="family">Family:</ht:td>
        <ht:td><parm name="family"/>
        </ht:td></ht:tr>
      <ht:tr><ht:td valign="top" title="gender">Gender:</ht:td>
        <ht:td><parm name="gender"/>
        </ht:td></ht:tr>
      <ht:tr><ht:td valign="top" title="birth">Birthdate:</ht:td>
        <ht:td><ht:input name="birth" size="10" init="" title="\d{4}\-\d{2}\-\d{2}" onkeyup="this.form.birth.className = (this.form.birth.value.match('^\\d{4}\\-\\d{2}\\-\\d{2}$')) ? 'valid': 'invalid'"/> yyyy-mm-dd
        </ht:td></ht:tr>
      <ht:tr><ht:td valign="top" title="place">Place:</ht:td>
        <ht:td><ht:textarea name="place" cols="20" rows="5" init="" title="\w[\w \.\-\(\)]*" onkeyup="this.form.place.className = (this.form.place.value.match('^\\w[\\w \\.\\-\\(\\)]*$')) ? 'valid': 'invalid'"/> Letters space . - ( )
        </ht:td></ht:tr>
      <ht:tr><ht:td valign="top" title="decease">Died:</ht:td>
        <ht:td><ht:input name="decease" size="4" init="" title="\d{4}" onkeyup="this.form.decease.className = (this.form.decease.value.match('^\\d{4}$')) ? 'valid': 'invalid'"/> Year yyyy
        </ht:td></ht:tr>
      </ht:table>
      <ht:input type="submit" value="Update"/> <ht:a href="servlet?spec=test/crud03">Back</ht:a> to the search form
    </ht:form>
  </when><!--== u p d 2 ==============-->
  <when name="view" match="upd2">
    <update>relatives
      <col name="birth">'<parm name="birth"/>'</col>
      <col name="place">'<parm name="place"/>'</col>
      <col name="decease"><parm name="decease"/></col>
      <col name="user">'<parm name="remote_user"/>'</col>
      <where>name = '<parm name="name"/>'
        and family = '<parm name="family"/>'
        and gender = '<parm name="gender"/>'
      </where>
    </update>
    <ht:h4><parm name="update_count"/> row(s) for key
       <ht:em> <parm name="name"/></ht:em>
       <ht:em> <parm name="family"/></ht:em>
       <ht:em> <parm name="gender"/></ht:em> updated
    </ht:h4>
  </when>
</choose>
<!--== s e a r  etc. ==============================-->
<choose>
  <when name="view" match="(sear|ins2|upd2|del2|)">
    <ht:form method="post">
      <ht:input name="view" type="hidden" value="sear"/>
      <ht:table>
      <ht:tr><ht:td valign="top" title="name">Name:</ht:td>
        <ht:td><ht:input name="search_name" size="40" init="" title="[\w\-%]*" onkeyup="this.form.name.className = (this.form.name.value.match('^[\\w\\-%]*$')) ? 'valid': 'invalid'"/> Letters and "-"
        </ht:td></ht:tr>
      <ht:tr><ht:td valign="top" title="family">Family:</ht:td>
        <ht:td><ht:select name="search_family" init="" size="3">
        <ht:option value="">(any)</ht:option>
        <ht:option value="Ritter">Ritter Family</ht:option>
        <ht:option value="Fischer">Fischer Family</ht:option>
        </ht:select>
        </ht:td></ht:tr>
      <ht:tr><ht:td valign="top" title="gender">Gender:</ht:td>
        <ht:td>
        <db:select distinct="yes" into="parm">
          <db:col name="code">gender</db:col><db:col name="display">case 
						when gender = 'M' then 'male' 
						else                   'female' end 
					</db:col><db:from>relatives</db:from><db:order by="1"/>
        </db:select>
        <db:listbox height="2" name="search_gender" init="" empty="(any)" code="code" display="display"/>
        
        </ht:td></ht:tr>
      </ht:table>
      <ht:input name="birth" type="hidden" init=""/>
      <ht:input name="place" type="hidden" init=""/>
      <ht:input name="decease" type="hidden" init=""/>
      <ht:input name="user" type="hidden" init=""/>
      <ht:input type="submit" value="Search"/>
    </ht:form><ht:br/>
    <ht:form method="post">
      <ht:input name="view" type="hidden" value="ins"/>
      <ht:input name="search_name" type="hidden" init=""/>
      <ht:input name="search_family" type="hidden" init=""/>
      <ht:input name="search_gender" type="hidden" init="M"/>
      <ht:input name="birth" type="hidden" init=""/>
      <ht:input name="place" type="hidden" init=""/>
      <ht:input name="decease" type="hidden" init=""/>
      <ht:input name="user" type="hidden" init=""/>
      <ht:input type="submit" value="New Relative"/>
    </ht:form>
    <select limit="2048">
      <col pseudo="image" label="Upd." align="center" remark="update" link="test/crud03&amp;search_name=&amp;search_family=&amp;search_gender=&amp;name=&amp;family=&amp;gender=&amp;view=">
        '<parm name="search_name"/>' || '=' 
         || '<parm name="search_family"/>' || '=' 
         || '<parm name="search_gender"/>' || '=' 
         || name || '=' 
         || family || '=' 
         || gender || '=' || 'upd' 
      </col>
      <col pseudo="image" label="Del." align="center" remark="delete" link="test/crud03&amp;search_name=&amp;search_family=&amp;search_gender=&amp;name=&amp;family=&amp;gender=&amp;view=">
        '<parm name="search_name"/>' || '=' 
         || '<parm name="search_family"/>' || '=' 
         || '<parm name="search_gender"/>' || '=' 
         || name || '=' 
         || family || '=' 
         || gender || '=' || 'del' 
      </col>
      <col name="name" label="Name">
        name
      </col>
      <col name="family" label="Family">family
      </col>
      <col name="gender" label="Gender" align="center">gender
      </col>
      <col name="birth" label="Birthdate">
        birth
      </col>
      <col name="place" label="Place">
        place
      </col>
      <col name="decease" label="Died" align="right">
        decease
      </col>
      <from>relatives </from>
      <where>name like '<parm name="search_name"/>%'
        and family like '<parm name="search_family"/>%'
        and gender like '<parm name="search_gender"/>%'
      </where>
      <order by="1,2"/>
      <counter desc="Person,s"/>
    </select>
  </when>
  <when name="view" match="(sear|)">
    <ht:h3>Help text follows here</ht:h3>
  </when>
</choose>

</dbat>
