#!/usr/bin/make

# Test Dbat functions with MySQL, and other utility targets
# @(#) $Id$
# 2022-03-28: rename.pl("log.error(getMessage(exc),")
# 2017-04-27: spec_index for LANG=en,de,fr
# 2016-05-24: validate with entity (included file) replacement
# 2016-04-16: unique parameters in generated SQL of target spec_index; $(SUDO)
# 2015-04-07: parameter LANG=en for spec_index and dbiv
# 2014-11-10: regression*.log now in test/; validation and dbiv processing
# 2014-11-08: target identify with etc/util/git_version.pl; no gopher
# 2013-01-05: RegressionTester replaces all_tester.pl
# 2012-06-27: all_tester.pl replaces batch_test.pl; etc/schema
# 2012-01-20: parm_xref
# 2011-07-27: target only
# 2011-07-21: comp is default target; make fill must be called explicitely
# 2011-05-27: new_regression with test/batch_test.pl
# 2011-04-27: & < > in INSERT values
# 2010-03-17, Georg Fischer

APPL=dbat
DBAW=java -cp dist/dbat.jar org.teherba.dbat.Dbat  -c worddb.properties  -e UTF-8
DIFF=diff -y --suppress-common-lines --width=160
DIFF=diff -w -rs -C0
SRC=src/main/java/org/teherba/dbat
METHOD=post
LANG=en
TAB=relatives
TESTDIR=test
# the following can be overriden outside for single or subset tests,
# for example make regression TEST=U%
TEST="%"
# for Windows, SUDO should be empty
SUDO=sudo
SUDO=
TOMC=c:/var/lib/tomcat/webapps/dbat
TOMC=/var/lib/tomcat/webapps/dbat

all: regression
#----
copy:
	cp -v web/spec/javascript.js c:$(TOMC)/spec
	cp -v web/spec/area.xml      c:$(TOMC)/spec/
#-------------------------------------------------------------------
# Perform a regression test (a complete run > 250 testcases with TEST=% takes > 17 s)
#	java -Djdk.net.registerGopherProtocol=true -cp dist/dbat.jar
regression: regression_mysql
regression_mysql:
	java -cp dist/$(APPL).jar \
			org.teherba.common.RegressionTester $(TESTDIR)/mysql.tests $(TEST) 2>&1 \
	| tee $(TESTDIR)/regression_mysql.log
	grep FAILED $(TESTDIR)/regression_mysql.log
#
# Recreate all testcases which failed (i.e. remove xxx.prev.tst)
# Handle with care!
# Failing testcases are turned into "passed" and are manifested by this target!
recreate: recr1 regr2
recr0:
	grep -E '> FAILED' $(TESTDIR)/regression*.log | cut -f 3 -d ' ' | xargs -l -ißß echo rm -v test/ßß.prev.tst
recr1:
	grep -E '> FAILED' $(TESTDIR)/regression*.log | cut -f 3 -d ' ' | xargs -l -ißß rm -v test/ßß.prev.tst
regr2:
	make regression TEST=$(TEST) > x.tmp
#--------------------------------------
# generate a short constant file
nosvn:
	echo nosvn.txt: Test file for URIReader tests 						>  web/nosvn.txt
	echo This file contains no SVN Id keyword for better comparision.	>> web/nosvn.txt
	echo 2011-07-27, Dr. Georg Fischer. Do not change this date.		>> web/nosvn.txt
#--------------------------------------------------
# create the documentation files
doc: javadoc wikidoc
javadoc:
	ant javadoc
wikidoc:
	cd target/docs ; wget -E -H -k -K -p -nd -nc http://localhost/wiki/index.php/Dbat	             || true
	cd target/docs ; wget -E -H -k -K -p -nd -nc http://localhost/wiki/index.php/Dbat-Spezifikation  || true
#---------------------------------------------------
# show manifests of appl.jar and appl-core.jar
manifests:
	unzip -p dist/$(APPL)-core.jar META-INF/MANIFEST.MF
	unzip -p dist/$(APPL).jar      META-INF/MANIFEST.MF
#---------------------------------------------------
# insert the last git hash and the compilation date into Dbat's version string
identify:
	perl -i.bak etc/util/git_version.pl $(SRC)/Configuration.java
	touch                               $(SRC)/Configuration.java
	git add -v                          $(SRC)/Configuration.java
	grep CVSID                          $(SRC)/Configuration.java \
	| head -1 | sed -e 's/\s/\n/g' | tail -6 | head -5
#---------------------------------------------------
# test whether all defined tests in mysql.tests have *.prev.tst results and vice versa
check_tests:
	grep -E "^TEST" $(TESTDIR)/mysql.tests | cut -b 6-8 | sort | uniq -c > $(TESTDIR)/tests_formal.tmp
	ls -1 $(TESTDIR)/*.prev.tst            | cut -b 6-8 | sort | uniq -c > $(TESTDIR)/tests_actual.tmp
	diff -y --suppress-common-lines --width=32 $(TESTDIR)/tests_formal.tmp $(TESTDIR)/tests_actual.tmp
#---------------------------------------------------
# test whether the VI% (crud03.iv) test outputs are the same as the corresponding precompiled V0% (crud03) outputs
same_ivs:
	find test -name "VI*.this.tst" | cut -b 8 | sort \
	| xargs -l -i{} make same_iv1 V0=V0{} VI=VI{}
#	     -e 's/xmlns="http:\/\/www.teherba.org\/2011\/dbiv"//g'
same_iv1:
	sed  -e "s/\.iv//g" \
	     $(TESTDIR)/$(VI).this.tst > x.tmp
	diff -w test/$(V0).prev.tst x.tmp
#---------------------------------------------------
jfind:
	find src -iname "*.java" | xargs -l grep -iH "$(JF)"
rmbak:
	find etc src web -iname "*.bak" | xargs -l rm -v
#---------------------------------------------------
exit1:
	java -jar dist/dbat.jar -c wi17 -n user_groups
	echo this shall not be executed
	java -jar dist/dbat.jar -c dbat -n user_groups
#---------------------------------------------------
clean_spec:
	perl $(TESTDIR)/get_scripts.pl $(TESTDIR)/all_test.cases | sort | uniq > $(TESTDIR)/used.tmp
	find web/spec -iname "*.xml" -printf "%P\n" | sort  > $(TESTDIR)/found.tmp
	diff -y --suppress-common-lines $(TESTDIR)/used.tmp $(TESTDIR)/found.tmp
#---------------------------------------------------
# generate all entries for the parameter/link check table
parm_xref: parm_xref_gen parm_xref_cre parm_xref_ins
parm_xref_gen:
	rm -f x.tmp
	echo -- generated by etc/xslt/gen_parm_xref.xsl at `date +%Y-%m-%d' '%H:%M:%S` \
	>                 etc/sql/parm_xref_insert.sql
	find web/spec -iname "*.xml" -printf "%P\n" | sort \
	| grep -v badxml \
	| grep -v '.iv.xml' \
	| grep -vE "incl" \
	| sed -e "s/.xml$$//" \
	| xargs -l -iqqq xsltproc --novalid \
	--stringparam file qqq \
	--stringparam lang de etc/xslt/gen_parm_xref.xsl web/spec/qqq.xml \
	>> x.tmp
	sort x.tmp | uniq | perl etc/xslt/parm_xref_post.pl \
	>>                etc/sql/parm_xref_insert.sql
	echo 'commit;' >> etc/sql/parm_xref_insert.sql
parm_xref_cre:
	$(DBAW)        -f etc/sql/parm_xref_create.sql
parm_xref_ins:
	$(DBAW) "delete from parm_xref"
	$(DBAW)        -f etc/sql/parm_xref_insert.sql
	$(DBAW) -m csv -s "\t" \
	    "SELECT element, name, spec, file FROM parm_xref ORDER BY 1,2,3"
#---------------------------------------------------
# generate all entries for the standard content table in each spec subdirectory
spec_index: spec_gen spec_cre spec_ins
spec_gen:
	echo -- generated by etc/xslt/gen_spec_index.xsl at `date +%Y-%m-%d' '%H:%M:%S` \
							>  etc/sql/spec_index_insert.sql
	cd web/spec; make -f ../../makefile spec_gen2 LANG=en
	cd web/spec; make -f ../../makefile spec_gen2 LANG=de
	cd web/spec; make -f ../../makefile spec_gen2 LANG=fr
	perl -i*.bak etc/xslt/unify_parms.pl \
	           etc/sql/spec_index_insert.sql
spec_gen2:
	find . -iname "*.xml" -printf "%P\n" | sort \
	| grep -v badxml \
	| grep -v '.iv.xml' \
	| xargs -l -iqqq xsltproc --novalid \
	--stringparam filename qqq \
	--stringparam lang $(LANG) ../../etc/xslt/gen_spec_index.xsl qqq \
	>>   ../../etc/sql/spec_index_insert.sql
spec_cre:
	$(DBAW) -f etc/sql/spec_index_create.sql
spec_ins:
	$(DBAW) "DELETE FROM spec_index"
	$(DBAW) -f etc/sql/spec_index_insert.sql
	$(DBAW) -m csv -s ";" \
	    "SELECT subdir, name, lang, title, SUBSTR(comment, 1, 40), params FROM spec_index ORDER BY subdir, name, lang"
#--------------------------------------
validate: valid_dbat valid_dbiv
valid_dbat:
	find web/spec -iname "*.xml" | sort \
	| grep -v ".iv.xml"\
	| grep -v "/incl"\
	| grep -v "/bad"\
	| xargs -l xmllint --noent --noout --schema etc/schema/dbat_dbiv.xsd 2>&1
valid_dbiv:
	find web/spec -iname "*.iv.xml" | sort \
	| xargs -l xmllint --noent --noout --schema etc/schema/dbiv.2011.xsd 2>&1
#--------------------------------------
form_gen_xalan:
	xalan -in web/spec/test/crud03.iv.xml -xsl etc/xslt/dbiv_spec.xsl\
	| tee web/spec/test/crud03.xml
#--------------------------------------
ajax:
	$(SUDO) cp -v web/spec/test/ajax*.xml   $(TOMC)/spec/test/
	$(SUDO) cp -v web/spec/http_request.js  $(TOMC)/spec/
#-------------------------------------
crud: crud01 crud02 crud03 crud04 crud05
crud01:
	make dbiv_dbat  IV=test/crud01
crud02:
	make dbiv_dbat  IV=test/crud02
	make dbiv_sproc IV=test/crud02
crud03:
	make dbiv_dbat  IV=test/crud03
crud04:
	make dbiv_dbat  IV=test/crud04
	$(SUDO) cp -v web/spec/http_request.js  $(TOMC)/spec/
	$(SUDO) cp -v web/spec/test/ajax02.xml  $(TOMC)/spec/test/
crud05:
	make dbiv_dbat  IV=test/crud05
	sed -e "s/crud05/crud01/g" web/spec/test/crud05.xml > crud05.tmp
	diff -C0 web/spec/test/crud01.xml crud05.tmp
odrd: 
	make dbiv_dbat IV=odrd/clients
	make dbiv_dbat IV=odrd/prets
#-------------------------------------
# precompile a single IV, e.g. with
# make dbiv_dbat IV=test/crud03
dbiv_spec: dbiv_dbat
dbiv_dbat:
	echo $(IV) | xargs -l -iqqq xsltproc --novalid \
	--stringparam lang   $(LANG)  \
	--stringparam method $(METHOD) \
	web/xslt/dbiv_dbat.xsl web/spec/qqq.iv.xml >web/spec/$(IV).xml
	$(SUDO) touch 								$(TOMC)/spec/$(IV).xml
	$(SUDO) cp -v web/spec/$(IV).xml 			$(TOMC)/spec/$(IV).xml
	$(SUDO) cp -v web/spec/test/stylesheet.css $(TOMC)/spec/test
#--------------------------------------
desp:
	$(SUDO) cp -v web/spec/$(SPEC).xml			$(TOMC)/spec/$(SPEC).xml
	# $(SUDO) cp -v web/spec/http_request.js 	$(TOMC)/spec
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
	--stringparam lang $(LANG)  \
	--stringparam conn worddb \
	etc/xslt/dbat_dbiv.xsl x.tmp
#--------------------------------------
zipart:
	zip -r dbat.part.`date +%Y-%m-%d`.zip \
	src/main/java/org/teherba/dbat/format/SQLTable.java \
	src/main/java/org/teherba/dbat/SQLAction.java \
	src/main/java/org/teherba/dbat/SpecificationHandler.java \
	etc/xslt/dbiv_dbat.xsl \
	etc/xslt/dbiv_sproc.xsl \
	$(TESTDIR)/all_test.cases \
	web/spec/test/crud0*.xml \
	makefile
#--------------------------------------
misc:
	$(SUDO) cp -v web/spec/test/check_word.xml $(TOMC)/spec/test
mod:
	$(DBAW) "update relatives set name='Cäcilie' where name='Cacilie'"
#----------------------
style-test:
	$(DBAW) -f temp/cal2017.sql
	$(DBAW) -n calwork_temp
	$(DBAW) -m xlsx -4000 calwork_temp > temp/calwork_temp.xlsx
#----------------
rename:
	echo "DbatServlet"          >   $@.tmp
	echo "SpecificationHandler" >>  $@.tmp
	echo "SQLAction"            >>  $@.tmp
	echo "Configuration"        >>  $@.tmp
	echo "TableMetaData"        >>  $@.tmp
	find src -iname "*.java" | grep -Ef $@.tmp | xargs -innn perl ../common/etc/rename.pl nnn
