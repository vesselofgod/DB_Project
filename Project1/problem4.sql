select maximum, C."MAJOR_NAME", A."GRADE", B."STUDENT_ID", B."COLLEGE_NAME"
from
(select max(t.total_grade) as maximum, s."MAJOR_ID", s."GRADE"
from "STUDENTS" as s, (select "STUDENT_ID", sum("CREDIT" * "GRADE") / sum("CREDIT") as total_grade
            from "GRADE" as g, "COURSE" as c
            where g."COURSE_ID" = c."COURSE_ID" and c."YEAR" = 2018 and c."SEMESTER" = 2
            group by "STUDENT_ID") as t, "COLLEGE" as c
where s."STUDENT_ID" = t."STUDENT_ID" and s."MAJOR_ID" = c."MAJOR_ID" and s."GRADE" < 5
group by s."MAJOR_ID", "GRADE") as A,
(select t.total_grade, s."MAJOR_ID", s."GRADE", s."STUDENT_ID", c."COLLEGE_NAME"
from "STUDENTS" as s, (select "STUDENT_ID", sum("CREDIT" * "GRADE") / sum("CREDIT") as total_grade
            from "GRADE" as g, "COURSE" as c
            where g."COURSE_ID" = c."COURSE_ID" and c."YEAR" = 2018 and c."SEMESTER" = 2
            group by "STUDENT_ID") as t, "COLLEGE" as c
where s."STUDENT_ID" = t."STUDENT_ID" and s."MAJOR_ID" = c."MAJOR_ID" and s."GRADE" < 5) as B, "COLLEGE" as C
where A.maximum = B.total_grade and A."MAJOR_ID" = B."MAJOR_ID" and A."GRADE" = B."GRADE" and C."MAJOR_ID" = A."MAJOR_ID";
