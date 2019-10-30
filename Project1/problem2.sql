select distinct c1."COURSE_NAME"
from
(select A."COURSE_ID_PREFIX", A."COURSE_ID_NO", all_students - one_students as again
from
(select "COURSE_ID_PREFIX", "COURSE_ID_NO", count(*) as all_students
from "COURSE" as c, "COURSE_REGISTRATION" as cr
where c."COURSE_ID" = cr."COURSE_ID"
GROUP BY "COURSE_ID_PREFIX", "COURSE_ID_NO") as A,
(select "COURSE_ID_PREFIX", "COURSE_ID_NO", count(distinct "STUDENT_ID") as one_students
from "COURSE" as c, "COURSE_REGISTRATION" as cr
where c."COURSE_ID" = cr."COURSE_ID"
GROUP BY "COURSE_ID_PREFIX", "COURSE_ID_NO") as B
where A."COURSE_ID_PREFIX" = B."COURSE_ID_PREFIX" and A."COURSE_ID_NO" = B."COURSE_ID_NO"
order by again desc limit 3) as final, "COURSE" as c1
where final."COURSE_ID_PREFIX" = c1."COURSE_ID_PREFIX" and final."COURSE_ID_NO" = c1."COURSE_ID_NO"
