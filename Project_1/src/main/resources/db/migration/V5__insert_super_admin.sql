-- V5__insert_super_admin.sql
INSERT INTO employee2 (username, password, email, role)
SELECT * FROM (
    SELECT
        'Fenil' AS username,
        '$2a$10$Rt4h4E9Nv5n1CVf1Hh77Se5w23Un1OnT1YHxOwPTowf4VJIbk60w6' AS password,
        'F@gmail.com' AS email,
        'SUPER_ADMIN' AS role
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM employee2 WHERE role = 'SUPER_ADMIN'
)
LIMIT 1;
