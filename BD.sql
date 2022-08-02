CREATE DATABASE IF NOT EXISTS financial_management;

USE financial_management;

-- INSERT INTO income (date, description, value) VALUES ('2022-08-01', 'Salário', '2000');

SELECT * FROM income WHERE MONTH(data) = '8'