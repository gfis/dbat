#!/usr/bin/make

# test dbat functions with SQLite and MySQL
# @(#) $Id$
# 2012-01-20: parm_xref
# 2011-07-27: target only
# 2011-07-21: comp is default target; make fill must be called explicitely
# 2011-05-27: new_regression with test/batch_test.pl
# 2011-04-27: & < > in INSERT values
# 2010-03-17, Georg Fischer

DBAW=java -cp dist/dbat.jar org.teherba.dbat.Dbat  -c worddb.properties  -e UTF-8
DIFF=diff -y --suppress-common-lines --width=160
DIFF=diff -w -rs -C0
MAX=`unzip -p dist/dbat.war | ident | gawk -F ' ' '{ print $$3 }' | sort -rn | head -1 | tr -d " \r\n"`
SRC=src/main/java/org/teherba/dbat
TOMC=/var/lib/tomcat/webapps/dbat
METHOD=get
TAB=relatives

all: new_regression
#------------------------------------------------
new_regression: comp eval
fill:
	cd test ; perl batch_test.pl -fill all.tca
comp:
	cd test ; perl batch_test.pl -comp all.tca | tee regression.log 
eval:
	rm -f [pF]*[dD].tests
	grep -E "FAILED" 			test/regression.log
	grep -E "passed" 			test/regression.log > passed.tests
	grep -E "FAILED" 			test/regression.log > FAILED.tests
	echo ================== >>  test/regression.log
	cat *.tests 	   		>>	test/regression.log
	wc -l *.tests | head -2 >> 	test/regression.log
	wc -l *.tests | head -2
fill_only:
	cd test ; perl batch_test.pl -fill -only $(TEST) all.tca
only: 
	cd test ; perl batch_test.pl -comp -only $(TEST) all.tca
show:
	make -i show1 TEST=$(TEST) | less
show1:
	diff -C0 test/$(TEST).prev.tst test/$(TEST).this.tst | cat -vET
	head -2000 test/$(TEST).*.tst
nosvn:
	echo nosvn.txt: Test file for URIReader tests 						>  web/nosvn.txt
	echo This file contains no SVN Id keyword for better comparision.	>> web/nosvn.txt
	echo 2011-07-27, Dr. Georg Fischer. Do not change this date.		>> web/nosvn.txt
#--------------------------------------------------
doc: javadoc wikidoc
javadoc:
	ant javadoc
wikidoc:	
	cd target/docs ; wget -E -H -k -K -p -nd -nc http://localhost/wiki/index.php/Dbat	             || true
	cd target/docs ; wget -E -H -k -K -p -nd -nc http://localhost/wiki/index.php/Dbat-Spezifikation  || true
identify:
	sed -e "s/Configuration.java [0-9]* /Configuration.java $(MAX) /" \
	    -e "s/$$\";.*/$$\"; \/\/ old $(MAX)/" \
	   $(SRC)/Configuration.java > \
	   $(SRC)/Configuration.java.bak
	grep '$$Id' $(SRC)/Configuration.java.bak
	mv $(SRC)/Configuration.java.bak \
	   $(SRC)/Configuration.java
#-----
#	less $(SRC)/Configuration.java.bak
id2:
	echo $(MAX)
	grep '$$Id' $(SRC)/Configuration.java
#---------------------------------------------------
exit1:
	java -jar dist/dbat.jar -c wi17 -n user_groups
	echo this shall not be executed
	java -jar dist/dbat.jar -c dbat -n user_groups
#---------------------------------------------------
clean_spec:
	perl test/get_scripts.pl test/all.tca | sort | uniq > test/used.tmp
	find web/spec -iname "*.xml" -printf "%P\n" | sort  > test/found.tmp
	diff -y --suppress-common-lines test/used.tmp test/found.tmp
#---------------------------------------------------
# generate all entries for the parameter/link check table
parm_xref: parm_xref_gen parm_xref_cre parm_xref_ins
parm_xref_gen:
	rm -f x.tmp
	echo -- generated by etc/xslt/gen_parm_xref.xsl at `date +%Y-%m-%d' '%H:%M:%S` \
									>  etc/sql/parm_xref_insert.sql
	find web/spec -iname "*.xml" -printf "%P\n" \
	| grep -v badxml \
	| sed -e "s/.xml$$//" \
	| xargs -l -iqqq xsltproc --novalid \
	--stringparam file qqq \
	--stringparam lang de etc/xslt/gen_parm_xref.xsl web/spec/qqq.xml \
	>> x.tmp
	sort x.tmp | uniq | perl etc/xslt/parm_xref_post.pl \
							>> etc/sql/parm_xref_insert.sql
	echo 'commit;'			>> etc/sql/parm_xref_insert.sql
parm_xref_cre:
	$(DBAW) 				-f etc/sql/parm_xref_create.sql
parm_xref_ins:
	$(DBAW) "delete from parm_xref"
	$(DBAW)                 -f etc/sql/parm_xref_insert.sql
	$(DBAW) -m csv -s "\t" \
		"select element, name, spec, file from parm_xref order by 1,2,3"
#---------------------------------------------------
# generate all entries for the standard content table in each spec subdirectory
spec_index: spec_gen spec_cre spec_ins
spec_gen:
	echo -- generated by etc/xslt/gen_spec_index.xsl at `date +%Y-%m-%d' '%H:%M:%S` \
									>  etc/sql/spec_index_insert.sql
	find web/spec -iname "*.xml" -printf "%P\n" \
	| grep -v badxml \
	| grep -v '.iv.xml' \
	| grep -vE "incl" \
	| xargs -l -iqqq xsltproc --novalid \
	--stringparam filename qqq \
	--stringparam lang en etc/xslt/gen_spec_index.xsl web/spec/qqq \
							>> etc/sql/spec_index_insert.sql
	echo '--'				>> etc/sql/spec_index_insert.sql
	echo 'commit;'			>> etc/sql/spec_index_insert.sql
spec_cre:
	$(DBAW) -f etc/sql/spec_index_create.sql
spec_ins:
	$(DBAW) "delete from spec_index"
	$(DBAW) -v              -f etc/sql/spec_index_insert.sql
	$(DBAW) -m csv -s ";" \
		"select subdir, name, lang, title, substr(comment, 1, 40), params from spec_index order by subdir, name, lang"
#--------------------------------------------------
JSP=validate
CLASS=ViewValidate
jsp:
	make jspstyle JSP=message CLASS=ViewMessage
jsp2:
	make jspstyle JSP=metaInf CLASS=ViewMetaInf
jspstyle:
#	astyle --style=java -x < target/WEB-INF/src/org/apache/jsp/help_jsp.java > src/main/java/org/teherba/dbat/ViewHelp.java
#	svn add src/main/java/org/teherba/dbat/ViewHelp.java	
#	astyle --style=java -x < target/WEB-INF/src/org/apache/jsp/more_jsp.java > src/main/java/org/teherba/dbat/ViewMore.java
#	svn add src/main/java/org/teherba/dbat/ViewMore.java
	tr -d "\r" < target/WEB-INF/src/org/apache/jsp/$(JSP)_jsp.java > x.tmp
	astyle --style=java -x < x.tmp \
	| sed -e 's/out.print( /out.write(/' -e 's/ );/);/' \
	> src/main/java/org/teherba/dbat/$(CLASS).java
	svn add src/main/java/org/teherba/dbat/$(CLASS).java
#--------------------------------------
validate: valid_dbat valid_dbiv
valid_dbat:
	find web/spec -iname "*.xml" \
	| grep -v ".iv.xml"\
	| grep -v "/incl"\
	| grep -v "/bad"\
	| xargs -l xmllint --noout --schema etc/xslt/dbat.2007.xsd 2>&1 
valid_dbiv:
	find web/spec -iname "*.iv.xml"\
	| xargs -l xmllint --noout --schema etc/xslt/dbiv.2011.xsd 2>&1 
#--------------------------------------
form_gen_xalan:
	xalan -in web/spec/test/crud03.iv.xml -xsl etc/xslt/dbiv_spec.xsl\
	| tee web/spec/test/crud03.xml
#--------------------------------------
ajax: 
	sudo cp -v web/spec/test/ajax*.xml	 	$(TOMC)/spec/test/
	sudo cp -v web/spec/http_request.js 	$(TOMC)/spec/
#-------------------------------------
crud: crud01 crud02 crud03 crud04 crud05
crud01:
	make dbiv_spec IV=test/crud01
	diff -C0 web/spec/test/crud01.xml crud01.tmp
crud02:
	make dbiv_spec  IV=test/crud02
	make dbiv_sproc IV=test/crud02
crud03:
	make dbiv_spec IV=test/crud03
crud04:
	make dbiv_spec IV=test/crud04
	sudo cp -v web/spec/http_request.js 	$(TOMC)/spec/
	sudo cp -v web/spec/test/ajax02.xml	 	$(TOMC)/spec/test/
	diff -C0 web/spec/test/crud04.xml crud04.tmp
crud05:
	make dbiv_spec IV=test/crud05
	sed -e "s/crud05/crud01/g" web/spec/test/crud05.xml > crud05.tmp
	diff -C0 web/spec/test/crud01.xml crud05.tmp
dbiv_dbat:
	echo $(IV) | xargs -l -iqqq xsltproc --novalid \
	--stringparam lang   en  \
	--stringparam method $(METHOD) \
	etc/xslt/dbiv_dbat.xsl web/spec/qqq.iv.xml >web/spec/$(IV).xml
	sudo touch 								$(TOMC)/spec/$(IV).xml
	sudo cp -v web/spec/$(IV).xml 			$(TOMC)/spec/$(IV).xml
	sudo cp -v web/spec/test/stylesheet.css $(TOMC)/spec/test
dbiv_spec:
	echo $(IV) | xargs -l -iqqq xsltproc --novalid \
	--stringparam lang   en  \
	--stringparam method $(METHOD) \
	etc/xslt/dbiv_spec.xsl web/spec/qqq.iv.xml >web/spec/$(IV).xml
	sudo touch 								$(TOMC)/spec/$(IV).xml
	sudo cp -v web/spec/$(IV).xml 			$(TOMC)/spec/$(IV).xml
	sudo cp -v web/spec/test/stylesheet.css $(TOMC)/spec/test
dbnv_spec:
	echo $(IV) | xargs -l -iqqq xsltproc --novalid \
	--stringparam lang   en  \
	--stringparam alter  0  \
	--stringparam method $(METHOD) \
	etc/xslt/dbiv_spec.xsl web/spec/qqq.iv.xml >web/spec/$(IV)_nv.xml
	sudo touch 								$(TOMC)/spec/$(IV)_nv.xml
	sudo cp -v web/spec/$(IV)_nv.xml 		$(TOMC)/spec/$(IV)_nv.xml
#--------------------------------------
desp:
	sudo cp -v web/spec/$(SPEC).xml			$(TOMC)/spec/$(SPEC).xml
	sudo cp -v web/spec/http_request.js 	$(TOMC)/spec
#--------------------------------------
spr:
	make dbiv_sproc IV=test/crud02
dbiv_sproc:
	xsltproc --novalid etc/xslt/dbiv_sproc.xsl web/spec/$(IV).iv.xml > $(IV).spr.sql
	$(DBAW) -sp "$$" -f $(IV).spr.sql
#--------------------------------------
dbat_dbiv: iv1 iv2
iv1:
	$(DBAW) -m xml -nsp db -d $(TAB) | tee x.tmp
iv2:
	xsltproc --novalid \
	--stringparam lang en  \
	--stringparam conn worddb \
	etc/xslt/dbat_dbiv.xsl x.tmp
#--------------------------------------
zipart:
	zip -r dbat.part.`date +%Y-%m-%d`.zip \
	src/main/java/org/teherba/dbat/format/SQLTable.java \
	src/main/java/org/teherba/dbat/SQLAction.java \
	src/main/java/org/teherba/dbat/SpecificationHandler.java \
	etc/xslt/dbiv_spec.xsl \
	etc/xslt/dbiv_sproc.xsl \
	test/all.tca \
	web/spec/test/crud0*.xml \
	makefile
#--------------------------------------
misc:
	sudo cp -v web/spec/test/check_word.xml $(TOMC)/spec/test
