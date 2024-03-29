# Oracle SQL 기초 문법

예제 위주로 Oracle SQL 문법에 대해서 정리하도록 한다.

### Q1

**직무가 매니저나 세일즈가 아닌 사원 중 월급이 1000 미만이거나 1600 이상인 사원을 조회하십시오.**

```sql
SELECT  EMPNO "사번", 
        ENAME "이름", 
        JOB "직무", 
        sal*12+NVL(comm,0) "연봉", 
        deptno "부서"
FROM emp
WHERE JOB NOT IN ('SALESMAN', 'MANAGER') AND
      (sal < 1000 OR sal >= 1600)
ORDER BY 4 DESC, 2;
```
+ NULL과의 연산 결과는 항상 0 이므로 NVL을 사용하도록 한다.
+ NVL(comm, 0) == NVL2(comm, comm, 0) 
***

### Q2

**ename의 앞 두 글자만 출력하고 나머지는 마스킹하시오.**
```sql
SELECT  ename,
        rpad(substr(ename, 1, 2), length(ename), '*')
FROM    emp;
```

+ substr(col, startIdx, number), -가 붙으면 뒤에서 부터 기준을 잡음.
+ instr(col, '..'): 문자열 내에 특정 문자열이 어느 위치에 있는지 찾음
+ replace: 문자열 내 특정 부분을 다른 것으로 대체함
+ length(col)
+ rpad(substr(ename, 1, 2), length(ename), '*'): legnth만큼 왼쪽에 substr을 붙이고 오른쪽 나머지에 '*' Pad를 붙인다.

***

### Q3

```sql
 SELECT length('  abcde  '),
        length(trim('  abcde  ')),
        length(trim(leading from '  abcde   ')),
        length(trim(trailing from '  abcde   '))
 FROM dual;
```
+ trim: 공백을 제거함
+ ltrim: 왼쪽 공백을 모두 제거함
+ rtrim: 오른쪽 공백을 모두 제거함

***

### Q4. 숫자 함수 및 날짜 함수

+ ROUND: 반올림
+ TRUNC: 버림
+ CEIL: 올림
+ SYSDATE: 현재 날짜
+ ADD_MONTHS, ADD_YEAR: 월, 연을 더함
+ to_char(date, 'form'): Date 타입을 'form' 형태로 문자열을 가져옴
+ to_date(Str, 'form'): Literal 타입을 'form' 형태의 Date로 만듬
+ to_number: 문자 타입을 숫자로 변환함

**숫자 <-> 문자 <-> 날짜**

***

### Q5
부서별 입사인원 현황을 분기별로 조회하십시오. 단, 결과 칼럼이 분기별로 나오도록 구성하십시오.

```sql
SELECT  deprtment_id,
        SUM(CASE WHEN to_char(hiredate, 'Q') = '1' THEN 1 ELSE 0 END) "Q1",
        SUM(CASE WHEN to_char(hiredate, 'Q') = '2' THEN 1 ELSE 0 END) "Q2",
        SUM(CASE WHEN to_char(hiredate, 'Q') = '3' THEN 1 ELSE 0 END) "Q3",
        SUM(CASE WHEN to_char(hiredate, 'Q') = '4' THEN 1 ELSE 0 END) "Q4",
FROM    emp;
GROUP BY department_id
```

+ CASE WHEN THEN ELSE END 구문을 익히자. **2차원 배열**로 나타낼 때 많이 사용된다.

***

### Q6

1. 부서별, 직무별 인건비 현황을 조회하시오. 
```sql
SELECT department_id,
       job_id,
       sum(wage)
FROM ( SELECT nvl(department_id, 999) "DEPARTMENT_ID",
              job_id,
              sal + NVL(sal, 0) * commision_pct "WAGE"
       FROM EMPLOYEES )
GROUP BY department_id, job_id
```

2. 위의 결과에서 부서별, 업무별 인건비 합과 순위를 구하시오
```sql
SELECT department_id,
       job_id,
       sum(wage),
       sum(sum(wage)) over(partition by department_id),
       sum(sum(wage)) over(partition by job_id),
       rank() over(partition by department_id order by sum(wage)),
       rank() over(partition by job_id order by sum(wage))
FROM ( SELECT nvl(department_id, 999) "DEPARTMENT_ID",
              job_id,
              sal + NVL(sal, 0) * commision_pct "WAGE"
       FROM EMPLOYEES )
GROUP BY department_id, job_id
```

+ rank() over(partition by ... order by ...)
+ sum() over(partition by ...)
