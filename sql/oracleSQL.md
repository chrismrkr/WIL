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
