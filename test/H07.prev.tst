Dbat Vx.hhhh/yyyy-mm-dd - DataBase Application Tool
Utilisation:
  java -jar dbat.jar [-acdfghlnrstvx] (table | "sql"| file | - | parameter ...)
  java org.teherba.dbat.Dbat "SELECT entry, morph FROM words"
  -29 tableau     SELECT * FROM tableau FETCH FIRST 29 ROWS ONLY
  -a colonne      aggreate (concatène) les valeurs de cette colonne
  -c propfile     fichier de propriétés avec les paramètres de connexion etc.
  -d tableau      affiche la description LDD (DROP / CREATE TABLE) pour le tableau
  -e codage       ISO-8859-1 (par défaut), UTF-8 etc. (deux fois pour l'entrée / sortie)
  -f fichier.sql  processus des instructions SQL à partir de ce fichier
  -f spec.xml     teste un fichier de spécification web (avec les paramètres -p)
  -g colonnes     liste de colonnes séparées par des virgules qui provoquent un changement de groupe (nouveaux en-têtes)
  -h              imprime ce texte d'aide d'utilisation
  -i tableau      imprimer les instructions INSERT pour "SELECT * FROM tableau"
  -l l1, l2, ...  définissent les largeurs des colonnes de sortie (pour -m fix)
  -m mode         mode de sortie: tsv (valeurs séparées par TAB, par défaut), csv (c.f. -s),
                  dbiv, echo, fix (c.f. -l), htm(l), jdbc, json, probe,
                  spec, sql, sqlj, taylor, trans, xls(x), xml
  -n tableau      SELECT count (*) FROM tableau
  -p name=val     réglage optionnel des paramètres (répétable)
  -r tableau[url] Insérer les valeurs brutes ([espace] séparées) d'un URL (ou de STDIN) dans le tableau
  -s sep          séparateur pour mode csv
  -sa sep         séparateur pour une agrégation
  -sp sep         séparateur pour CREATE PROCEDURE
  -t tableau      nom de tableau (pour -g)
  -v              verbose: imprimer les instructions SQL et le temps d'exécution
  -x              imprime aucune rubrique / résumé
Les options et les actions sont évaluées de gauche à droite.
Les instructions SQL doivent contenir un espace, et doivent être insérées entre guillemets doubles.
Les noms de fichiers ne doivent pas contenir d'espaces. '-' est STDIN.
Pilotes JDBC inclus:
  com.mysql.jdbc.Driver V5.1
Formats de sortie inclus (-m):
  html      HTML
  xml       XML
  xlsx,xls  Microsoft Excel
  json      JSON
  wiki      Code pour MediaWiki
  csv,tsv   Valeurs séparées
  fix       Colonnes de largeur fixe
  taylor    Substitution de variables
  sql       SQL INSERTs
  update    SQL UPDATEs
  jdbc      SQL avec échappement JDBC
  trans     XML+XSLT
  gen       Générateur d'événements SAX
  spec      Spécification pour Dbat
  dbiv,view Spécification pour Dbiv
  echo      Echo SQL
  sqlj      Générateur pour SQLJ
  probe     Probe SQL

