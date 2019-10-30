select Data.score, Data.name, Data.ID, Data.Major, Data.CNAME
from
(select maximum as score, A."NAME" as name, A."MAJOR_ID" as Major, B."STUDENT_ID" as ID, B."COLLEGE_NAME" as CNAME
from
(
	select max(t.total_grade) as maximum, s."MAJOR_ID", s."GRADE",s."NAME" 
	from "STUDENTS" as s,
	(
		select s."STUDENT_ID", sum(c."CREDIT" * g."GRADE") / sum(c."CREDIT") as total_grade
		from "GRADE" as g, "COURSE" as c, "STUDENTS" as s
		where g."COURSE_ID" = c."COURSE_ID" and c."YEAR" <= 2018 and c."YEAR" >= 2015 and s."GRADE"=4 and g."STUDENT_ID" = s."STUDENT_ID"
		group by s."STUDENT_ID"
	) as t, "COLLEGE" as c
where s."STUDENT_ID" = t."STUDENT_ID" and s."MAJOR_ID" = c."MAJOR_ID" and s."GRADE" = 4
group by s."MAJOR_ID", s."GRADE",s."NAME") as A,
(
	select t.total_grade, s."MAJOR_ID", s."GRADE", s."STUDENT_ID", c."COLLEGE_NAME"
	from "STUDENTS" as s,
	(
		select s."STUDENT_ID", sum(c."CREDIT" * g."GRADE") / sum(c."CREDIT") as total_grade
		from "GRADE" as g, "COURSE" as c, "STUDENTS" as s
		where g."COURSE_ID" = c."COURSE_ID" and c."YEAR" <= 2018 and c."YEAR" >= 2015 and s."GRADE"=4 and g."STUDENT_ID" = s."STUDENT_ID"
		group by s."STUDENT_ID"
	) as t, "COLLEGE" as c
where s."STUDENT_ID" = t."STUDENT_ID" and s."MAJOR_ID" = c."MAJOR_ID" and s."GRADE" = 4
) as B
where A.maximum = B.total_grade and A."MAJOR_ID" = B."MAJOR_ID" and A."GRADE" = B."GRADE") as Data
where Data.score >= all(select maximum
from
(
	select max(t.total_grade) as maximum, s."MAJOR_ID", s."GRADE",s."NAME"
	from "STUDENTS" as s,
	(
		select s."STUDENT_ID", sum(c."CREDIT" * g."GRADE") / sum(c."CREDIT") as total_grade
		from "GRADE" as g, "COURSE" as c, "STUDENTS" as s
		where g."COURSE_ID" = c."COURSE_ID" and c."YEAR" <= 2018 and c."YEAR" >= 2015 and s."GRADE"=4 and g."STUDENT_ID" = s."STUDENT_ID"
		group by s."STUDENT_ID"
	) as t, "COLLEGE" as c
where s."STUDENT_ID" = t."STUDENT_ID" and s."MAJOR_ID" = c."MAJOR_ID" and s."GRADE" = 4
group by s."MAJOR_ID", s."GRADE",s."NAME") as A,
(
	select t.total_grade, s."MAJOR_ID", s."GRADE", s."STUDENT_ID", c."COLLEGE_NAME"
	from "STUDENTS" as s,
	(
		select s."STUDENT_ID", sum(c."CREDIT" * g."GRADE") / sum(c."CREDIT") as total_grade
		from "GRADE" as g, "COURSE" as c, "STUDENTS" as s
		where g."COURSE_ID" = c."COURSE_ID" and c."YEAR" <= 2018 and c."YEAR" >= 2015 and s."GRADE"=4 and g."STUDENT_ID" = s."STUDENT_ID"
		group by s."STUDENT_ID"
	) as t, "COLLEGE" as c
where s."STUDENT_ID" = t."STUDENT_ID" and s."MAJOR_ID" = c."MAJOR_ID" and s."GRADE" = 4
) as B
where A.maximum = B.total_grade and A."MAJOR_ID" = B."MAJOR_ID" and A."GRADE" = B."GRADE")
or 
Data.score <= all(select maximum
from
(
	select max(t.total_grade) as maximum, s."MAJOR_ID", s."GRADE",s."NAME"
	from "STUDENTS" as s,
	(
		select s."STUDENT_ID", sum(c."CREDIT" * g."GRADE") / sum(c."CREDIT") as total_grade
		from "GRADE" as g, "COURSE" as c, "STUDENTS" as s
		where g."COURSE_ID" = c."COURSE_ID" and c."YEAR" <= 2018 and c."YEAR" >= 2015 and s."GRADE"=4 and g."STUDENT_ID" = s."STUDENT_ID"
		group by s."STUDENT_ID"
	) as t, "COLLEGE" as c
where s."STUDENT_ID" = t."STUDENT_ID" and s."MAJOR_ID" = c."MAJOR_ID" and s."GRADE" = 4
group by s."MAJOR_ID", s."GRADE",s."NAME") as A,
(
	select t.total_grade, s."MAJOR_ID", s."GRADE", s."STUDENT_ID", c."COLLEGE_NAME"
	from "STUDENTS" as s,
	(
		select s."STUDENT_ID", sum(c."CREDIT" * g."GRADE") / sum(c."CREDIT") as total_grade
		from "GRADE" as g, "COURSE" as c, "STUDENTS" as s
		where g."COURSE_ID" = c."COURSE_ID" and c."YEAR" <= 2018 and c."YEAR" >= 2015 and s."GRADE"=4 and g."STUDENT_ID" = s."STUDENT_ID"
		group by s."STUDENT_ID"
	) as t, "COLLEGE" as c
where s."STUDENT_ID" = t."STUDENT_ID" and s."MAJOR_ID" = c."MAJOR_ID" and s."GRADE" = 4
) as B
where A.maximum = B.total_grade and A."MAJOR_ID" = B."MAJOR_ID" and A."GRADE" = B."GRADE")
