INSERT INTO employee2 (username, password, email, role)
SELECT * FROM (
    SELECT
        'Fenil' ,
        '$2a$10$Rt4h4E9Nv5n1CVf1Hh77Se5w23Un1OnT1YHxOwPTowf4VJIbk60w6' ,
        'F@gmail.com',
        'SUPER_ADMIN'
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM employee2 WHERE UPPER (role) = 'SUPER_ADMIN'
)
LIMIT 1;
