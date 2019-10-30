select f."NAME", count("COURSE_ID")
from
(
   select (CAST(count(CRN."STUDENT_ID") AS NUMERIC(10,4))/CAST(CO."MAX_ENROLLEES" AS NUMERIC(10,4))) as per, CO."PROF_ID" as pid, CO."COURSE_ID"
   from "COURSE" as CO ,"COURSE_REGISTRATION" as CRN 
   where CO."COURSE_ID" = CRN."COURSE_ID" group by CO."COURSE_ID"
) as A, "FACULTY" as f
where A.pid = f."ID" and A.per >= 0.8
group by f."NAME"