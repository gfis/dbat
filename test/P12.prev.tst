

	<!--Pivot matrix output with linked values-->
	<!--Ausgabe einer Pivot-Matrix mit Verweisen auf den Werten-->

    
===Pivot matrix test - with linked values===

    <!--<input name="spec" type="hidden" value="test/pivot03" />

        Name: <input name="name" maxsize="20" size="10" init="%r" value="%r"></input>
        <input type="submit" value="Submit"></input>
    -->
    
    <!-- SQL:
SELECT sp1
, sp2
, concat(sp1, concat('=', concat(sp2, concat('=', sp3)))) 
FROM pivot;
:SQL -->

{| border="1"
|Application||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=&amp;beta=c1&amp;sp3=S1 S1]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=&amp;beta=c2&amp;sp3=S2 S2]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=&amp;beta=c3&amp;sp3=S3 S3]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=&amp;beta=c4&amp;sp3=S4 S4]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=&amp;beta=c5&amp;sp3=S5 S5]
|-
|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=A A]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=A&amp;beta=c1&amp;sp3=A1 A1]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=A&amp;beta=c2&amp;sp3=A2 A2]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=A&amp;beta=c3&amp;sp3=A3 A3]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=A&amp;beta=c4&amp;sp3=A4 A4]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=A&amp;beta=c5&amp;sp3=A5 A5]
|-
|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=B B]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=B&amp;beta=c1&amp;sp3=B1 B1]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=B&amp;beta=c2&amp;sp3=B2 B2]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=B&amp;beta=c3&amp;sp3=B3 B3]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=B&amp;beta=c4&amp;sp3=B4 B4]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=B&amp;beta=c5&amp;sp3=B5 B5]
|-
|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=C C]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=C&amp;beta=c1&amp;sp3=C1 C1]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=C&amp;beta=c2&amp;sp3=C2 C2]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=C&amp;beta=c3&amp;sp3=C3 C3]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=C&amp;beta=c4&amp;sp3=C4 C4]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=C&amp;beta=c5&amp;sp3=C5 C5]
|-
|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=D D]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=D&amp;beta=c1&amp;sp3=D1 D1]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=D&amp;beta=c2&amp;sp3=D2 D2]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=D&amp;beta=c3&amp;sp3=D3 D3]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=D&amp;beta=c4&amp;sp3=D4 D4]||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;alpha=D&amp;beta=c5&amp;sp3=D5 D5]
|-
|}


<!-- Output on yyyy-mm-dd hh:mm:ss by Dbat script test/pivot03, Excel, more
 -->
