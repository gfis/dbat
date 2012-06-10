java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -m wiki -d c01

{| border="1"
|name||type||width||nullable||remark
|-
|name||VARCHAR||16||true||Name of the Relative
|-
|univ||VARCHAR||16||false||Town
|-
|year||INT||||false||Decease Year
|-
|gender||CHAR||1||false||some escaped character
|-
|birth||DATE||||false||
|-
<!-- BaseTable.describePrimaryKey -->
<!-- BaseTable.describeIndexes -->
<!-- BaseTable.describeConstraints -->
|}
