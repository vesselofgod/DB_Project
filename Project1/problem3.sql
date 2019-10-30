select f."NAME", count("COURSE_ID") as num
from(
   select (CAST(count(CRN."STUDENT_ID") AS NUMERIC(10,4))/CAST(CO."MAX_ENROLLEES" AS NUMERIC(10,4))) as per, CO."PROF_ID" as pid, CO."COURSE_ID",CO."YEAR" as y
   from "COURSE" as CO ,"COURSE_REGISTRATION" as CRN 
   where CO."COURSE_ID" = CRN."COURSE_ID" group by CO."COURSE_ID") as A, "FACULTY" as f
where A.pid = f."ID" and A.y<=2018 and A.y>=2013 and per>0.8
group by f."NAME"
order by num DESC
