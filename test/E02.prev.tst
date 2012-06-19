wget -q -O - "http://localhost:8080/dbat/servlet?spec=test/with_cte&mode=echo"
WITH cte as
		( select entry, enrel 
		  from words
		  where entry like 'backe%'
		)
SELECT word 
FROM (
        		( select entry as word from cte )
        	  union
        	    ( select enrel as word from cte )
        	  ) as subs 
ORDER BY 1;
