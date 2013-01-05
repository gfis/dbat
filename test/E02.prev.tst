--[01]--dbat.format.EchoSQL on yyyy-mm-dd hh:mm:ss----
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
--[99]----------------------------
