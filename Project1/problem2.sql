select "COURSE_NAME"
from(
select c."COURSE_NAME",
	CASE
	WHEN sum is null
	THEN 0
	ELSE sum
	END AS total
from
(select  distinct "COURSE_ID_PREFIX", "COURSE_ID_NO", "COURSE_NAME"
from "COURSE"
) as c
left outer join 
(select "COURSE_ID_PREFIX", "COURSE_ID_NO", sum(reg)
from(
select "COURSE_ID_PREFIX", "COURSE_ID_NO", "STUDENT_ID", count(*) as reg
from(
select  c."COURSE_ID", "STUDENT_ID", count(*)
from
(select "COURSE_ID_PREFIX", "COURSE_ID_NO", count(*)
from "COURSE" as c, "COURSE_REGISTRATION" as cr
where c."COURSE_ID" = cr."COURSE_ID"
group by "COURSE_ID_PREFIX", "COURSE_ID_NO") as first, "COURSE" as c, "COURSE_REGISTRATION" as cr
where first."COURSE_ID_PREFIX" = c."COURSE_ID_PREFIX" and first."COURSE_ID_NO" = c."COURSE_ID_NO" and c."COURSE_ID" = cr."COURSE_ID"
group by c."COURSE_ID", "STUDENT_ID") as second, "COURSE" as c
where second."COURSE_ID" = c."COURSE_ID"
group by "COURSE_ID_PREFIX", "COURSE_ID_NO", "STUDENT_ID"
order by "COURSE_ID_PREFIX", "COURSE_ID_NO") as third
where reg > 1
group by "COURSE_ID_PREFIX", "COURSE_ID_NO") as fourth on fourth."COURSE_ID_PREFIX" = c."COURSE_ID_PREFIX" and fourth."COURSE_ID_NO" = c."COURSE_ID_NO"
order by total desc limit 3) as fifth
