-- SQL:
--WITH cte as
--		( select entry, enrel 
--		  from words
--		  where entry like 'backe%'
--		)
--select * from (
--SELECT word 
--FROM (
--        		( select entry as word from cte )
--        	  union
--        	    ( select enrel as word from cte )
--        	  ) as subs
--) probe where 1 = 0  
--ORDER BY 1;
--:SQL
You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'cte as
		( select entry, enrel 
		  from words
		  where entry like 'backe%'
		)' at line 1
