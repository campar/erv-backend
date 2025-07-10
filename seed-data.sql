-- Seed podaci bazirani na DataSeeder.java
-- VAŽNO: Pokrenuti NAKON init-db.sql

-- ========== DEPARTMENT UNITS ==========
-- 87 organizacionih jedinica sa hijerarhijom (parent = department_unit kolona)
INSERT INTO public.department_unit (id, department_unit, code, description, name) VALUES
(1,  NULL, 'EXAMPLE', '', 'EXAMPLE'),
(2,  1,    'SAMOSTALNE ORGANIZACIONE JEDINICE', '', 'SAMOSTALNE ORGANIZACIONE JEDINICE'),
(3,  2,    'SLUŽBA INTERNE REVIZIJE', '', 'SLUŽBA INTERNE REVIZIJE'),
(4,  2,    'SLUŽBA UPRAVLJANJA KVALITETOM', '', 'SLUŽBA UPRAVLJANJA KVALITETOM'),
(5,  2,    'SLUŽBA UPRAVLJANJA INFORMACIONIM SISTEMOM', '', 'SLUŽBA UPRAVLJANJA INFORMACIONIM SISTEMOM'),
(6,  2,    'SLUŽBA BEZBEDNOSTI I ZDRAVLJA NA RADU', '', 'SLUŽBA BEZBEDNOSTI I ZDRAVLJA NA RADU'),
(7,  2,    'ODELJENJE KOMUNIKACIJA', '', 'ODELJENJE KOMUNIKACIJA'),
(8,  2,    'SLUŽBA UPRAVLJANJA PROJEKTIMA', '', 'SLUŽBA UPRAVLJANJA PROJEKTIMA'),
(9,  1,    'POGON 1', '', 'POGON 1'),
(10, 1,    'SEKTOR OPERATIVE', '', 'SEKTOR OPERATIVE'),
(11, 10,   'SLUŽBA KONTROLE KVALITETA USLUGA', '', 'SLUŽBA KONTROLE KVALITETA USLUGA'),
(12, 1,    'POGON 2', '', 'POGON 2'),
(13, 1,    'POGON 3', '', 'POGON 3'),
(14, 1,    'POGON 4', '', 'POGON 4'),
(15, 2,    'INFO CENTAR', '', 'INFO CENTAR'),
(16, 1,    'POGON 5', '', 'POGON 5'),
(17, 1,    'SEKTOR ODRŽAVANJA', '', 'SEKTOR ODRŽAVANJA'),
(18, 17,   'ODELJENJE KOMPJUTERSKE DIJAGNOSTIKE', '', 'ODELJENJE KOMPJUTERSKE DIJAGNOSTIKE'),
(19, 1,    'POGON 6', '', 'POGON 6'),
(20, 1,    'POGON 7', '', 'POGON 7'),
(21, 1,    'POGON 8', '', 'POGON 8'),
(22, 1,    'POGON 9', '', 'POGON 9'),
(23, 1,    'POGON 10', '', 'POGON 10'),
(24, 1,    'POGON 11', '', 'POGON 11'),
(25, 10,   'SLUŽBA PODRŠKE VOZNOG PARKA', '', 'SLUŽBA PODRŠKE VOZNOG PARKA'),
(26, 10,   'SLUŽBA PROJEKTOVANJA USLUGA I NORMIRANJA', '', 'SLUŽBA PROJEKTOVANJA USLUGA I NORMIRANJA'),
(27, 17,   'ODELJENJE EKSPLOATACIJE', '', 'ODELJENJE EKSPLOATACIJE'),
(28, 10,   'ODELJENJE NORMIRANJA', '', 'ODELJENJE NORMIRANJA'),
(29, 10,   'ODELJENJE PRIBAVLJANJA DOZVOLA', '', 'ODELJENJE PRIBAVLJANJA DOZVOLA'),
(30, 2,    'SLUŽBA NADZORA I OBRADE PODATAKA', '', 'SLUŽBA NADZORA I OBRADE PODATAKA'),
(31, 10,   'ODELJENJE REKLAMACIJA', '', 'ODELJENJE REKLAMACIJA'),
(32, 1,    'SEKTOR LOGISTIKE', '', 'SEKTOR LOGISTIKE'),
(33, 32,   'SLUŽBA TRANSPORTA', '', 'SLUŽBA TRANSPORTA'),
(34, 32,   'POGON 12', '', 'POGON 12'),
(35, 1,    'SEKTOR PRAVNIH, KADROVSKIH I OPŠTIH POSLOVA', '', 'SEKTOR PRAVNIH, KADROVSKIH I OPŠTIH POSLOVA'),
(36, 35,   'SLUŽBA PRAVNIH POSLOVA', '', 'SLUŽBA PRAVNIH POSLOVA'),
(37, 35,   'SLUŽBA KADROVSKIH POSLOVA', '', 'SLUŽBA KADROVSKIH POSLOVA'),
(38, 35,   'SLUŽBA PROTIVPOŽARNE ZAŠTITE', '', 'SLUŽBA PROTIVPOŽARNE ZAŠTITE'),
(39, 35,   'SLUŽBA OPŠTIH POSLOVA', '', 'SLUŽBA OPŠTIH POSLOVA'),
(40, 35,   'ODELJENJE DRUŠTVENOG STANDARDA', '', 'ODELJENJE DRUŠTVENOG STANDARDA'),
(41, 1,    'SEKTOR EKONOMSKIH POSLOVA', '', 'SEKTOR EKONOMSKIH POSLOVA'),
(42, 41,   'SLUŽBA KNJIGOVODSTVENIH POSLOVA', '', 'SLUŽBA KNJIGOVODSTVENIH POSLOVA'),
(43, 41,   'SLUŽBA FINANSIJSKIH POSLOVA', '', 'SLUŽBA FINANSIJSKIH POSLOVA'),
(44, 41,   'SLUŽBA PRIHODA', '', 'SLUŽBA PRIHODA'),
(45, 41,   'SLUŽBA PLANA, ANALIZE I RAZVOJA', '', 'SLUŽBA PLANA, ANALIZE I RAZVOJA'),
(46, 1,    'SEKTOR KOMERCIJALNIH POSLOVA', '', 'SEKTOR KOMERCIJALNIH POSLOVA'),
(47, 46,   'SLUŽBA JAVNIH NABAVKI', '', 'SLUŽBA JAVNIH NABAVKI'),
(48, 46,   'SLUŽBA KOMERCIJALNIH POSLOVA', '', 'SLUŽBA KOMERCIJALNIH POSLOVA'),
(49, 46,   'ODELJENJE NABAVKE AUTO-DELOVA I USLUGA', '', 'ODELJENJE NABAVKE AUTO-DELOVA I USLUGA'),
(50, 46,   'MAGACINSKA SLUŽBA', '', 'MAGACINSKA SLUŽBA'),
(51, 46,   'SLUŽBA OSIGURANJA VOZILA, LICA I IMOVINE', '', 'SLUŽBA OSIGURANJA VOZILA, LICA I IMOVINE'),
(52, 17,   'SLUŽBA ODRŽAVANJA VOZILA, MAŠINA I POMAGALA ZA RAD', '', 'SLUŽBA ODRŽAVANJA VOZILA, MAŠINA I POMAGALA ZA RAD'),
(53, 17,   'ODELJENJE AUTOMEHANIČARA', '', 'ODELJENJE AUTOMEHANIČARA'),
(54, 17,   'ODELJENJE MAŠINSKE PRIPREME', '', 'ODELJENJE MAŠINSKE PRIPREME'),
(55, 17,   'ODELJENJE AUTOELEKTRIČARA', '', 'ODELJENJE AUTOELEKTRIČARA'),
(56, 17,   'ODELJENJE AUTOBRAVARA - AUTOLIMARA', '', 'ODELJENJE AUTOBRAVARA - AUTOLIMARA'),
(57, 17,   'ODELJENJE AUTOLAKIRERA', '', 'ODELJENJE AUTOLAKIRERA'),
(58, 17,   'ODELJENJE OPRAVKI OPREME', '', 'ODELJENJE OPRAVKI OPREME'),
(59, 17,   'ODELJENJE ZA ODRŽAVANJE MAŠINA I INSTALACIJA', '', 'ODELJENJE ZA ODRŽAVANJE MAŠINA I INSTALACIJA'),
(60, 17,   'ODELJENJE ODRŽAVANJA AUTO-GUMA', '', 'ODELJENJE ODRŽAVANJA AUTO-GUMA'),
(61, 17,   'TEHNIČKO RAZVOJNA SLUŽBA', '', 'TEHNIČKO RAZVOJNA SLUŽBA'),
(62, 17,   'ODELJENJE KOTLARNICE I PARNIH INSTALACIJA', '', 'ODELJENJE KOTLARNICE I PARNIH INSTALACIJA'),
(63, 17,   'ODELJENJE ZA ODRŽAVANJE GRAĐEVINSKE MEHANIZACIJE', '', 'ODELJENJE ZA ODRŽAVANJE GRAĐEVINSKE MEHANIZACIJE'),
(64, 17,   'ODELJENJE GRAĐEVINSKIH POSLOVA', '', 'ODELJENJE GRAĐEVINSKIH POSLOVA'),
(65, 17,   'SLUŽBA DEFINISANJA KVALITETA', '', 'SLUŽBA DEFINISANJA KVALITETA'),
(66, 17,   'SLUŽBA UPRAVLJANJA INTEGRISANIM ŠIFARSKIM SISTEMOM', '', 'SLUŽBA UPRAVLJANJA INTEGRISANIM ŠIFARSKIM SISTEMOM'),
(67, 2,    'KORISNIČKI SERVIS', '', 'KORISNIČKI SERVIS'),
(68, 1,    'SEKTOR ZAŠTITE ŽIVOTNE SREDINE', '', 'SEKTOR ZAŠTITE ŽIVOTNE SREDINE'),
(69, 68,   'ODELJENJE TEHNIČKO-LOGISTIČKIH POSLOVA', '', 'ODELJENJE TEHNIČKO-LOGISTIČKIH POSLOVA'),
(70, 68,   'ODELJENJE ADMINISTRATIVNIH POSLOVA', '', 'ODELJENJE ADMINISTRATIVNIH POSLOVA'),
(71, 68,   'SLUŽBA TERENSKIH POSLOVA', '', 'SLUŽBA TERENSKIH POSLOVA'),
(72, 68,   'SLUŽBA MONITORINGA', '', 'SLUŽBA MONITORINGA'),
(73, 68,   'SLUŽBA POSLOVA LABORATORIJSKIH ISPITIVANJA', '', 'SLUŽBA POSLOVA LABORATORIJSKIH ISPITIVANJA'),
(74, 10,   'ODELJENJE TERENSKOG ODRŽAVANJA', '', 'ODELJENJE TERENSKOG ODRŽAVANJA'),
(75, 35,   'ODELJENJE PISARNICE', '', 'ODELJENJE PISARNICE'),
(76, 10,   'SLUŽBA GARAŽNO TEHNIČKIH POSLOVA', '', 'SLUŽBA GARAŽNO TEHNIČKIH POSLOVA'),
(77, 68,   'ODELJENJE HEMIJSKIH ISPITIVANJA', '', 'ODELJENJE HEMIJSKIH ISPITIVANJA'),
(78, 68,   'ODELJENJE BIOLOŠKIH ISPITIVANJA', '', 'ODELJENJE BIOLOŠKIH ISPITIVANJA'),
(79, 1,    'POSLOVI I ZADACI NA NIVOU PREDUZEĆA', '', 'POSLOVI I ZADACI NA NIVOU PREDUZEĆA'),
(80, 35,   'ODELJENJE PRIPREMANJA I SERVIRANJA ISHRANE ZAPOSLENIH', '', 'ODELJENJE PRIPREMANJA I SERVIRANJA ISHRANE ZAPOSLENIH'),
(81, 41,   'ODELJENJE POSLOVA OBAVEZA, PROMETA NOVCA I HARTIJA OD VREDNOSTI', '', 'ODELJENJE POSLOVA OBAVEZA, PROMETA NOVCA I HARTIJA OD VREDNOSTI'),
(82, 41,   'ODELJENJE BILANSNIH POSLOVA', '', 'ODELJENJE BILANSNIH POSLOVA'),
(83, 41,   'ODELJENJE ANALITIČKIH POSLOVA', '', 'ODELJENJE ANALITIČKIH POSLOVA'),
(84, 41,   'ODELJENJE POSLOVA OBRAČUNA ZARADA', '', 'ODELJENJE POSLOVA OBRAČUNA ZARADA'),
(85, 41,   'ODELJENJE POSLOVA POREZA, PRIHODA I POTRAŽIVANJA', '', 'ODELJENJE POSLOVA POREZA, PRIHODA I POTRAŽIVANJA'),
(86, 41,   'SLUŽBA FINANSIJSKOG UPRAVLJANJA I KONTROLE', '', 'SLUŽBA FINANSIJSKOG UPRAVLJANJA I KONTROLE'),
(87, 35,   'ODELJENJE TEHNIČKIH USLUGA', '', 'ODELJENJE TEHNIČKIH USLUGA');


-- ========== LOCATIONS ==========
INSERT INTO public.location (id, soft_deleted, address, city, description, name, phone) VALUES
(1,  false, 'Industrijska 1',               'Belgrade', 'Test description', 'Direkcija',          '011 123 456'),
(2,  false, 'Radnička 12',                  'Belgrade', 'Test description', 'Pogon 2',            '011 123 456'),
(3,  false, 'Fabrička 21',                  'Belgrade', 'Test description', 'Pogon 4',            '011 123 456'),
(4,  false, 'Skladišna 3',                  'Belgrade', 'Test description', 'Pogon 12',           '011 123 456'),
(5,  false, 'Zanatska 8',                   'Belgrade', 'Test description', 'Pogon 3',            '011 123 456'),
(6,  false, 'Magacinska 14',                'Belgrade', 'Test description', 'Pogon 5',            '011 123 456'),
(7,  false, 'Servisna 2',                   'Belgrade', 'Test description', 'Pogon Auto Baza',    '011 123 456'),
(8,  false, 'Pogonska 7',                   'Belgrade', 'Test description', 'Pogon 9',            '011 123 456'),
(9,  false, 'Logistička 19',                'Belgrade', 'Test description', 'Pogon 7',            '011 123 456'),
(10, false, 'Tehnička 11',                  'Belgrade', 'Test description', 'Pogon 8',            '011 123 456'),
(11, false, 'Proizvodna 4',                 'Belgrade', 'Test description', 'Pogon 10',           '011 123 456'),
(12, false, 'Radionička 9',                 'Belgrade', 'Test description', 'Pogon 13',           '011 123 456'),
(13, false, 'Glavna 16',                    'Belgrade', 'Test description', 'Magacin',            '011 123 456'),
(14, false, 'Servisna 4',                   'Belgrade', 'Test description', 'Servis',             '011 123 456');


-- ========== SHIFTS ==========
-- 5 smena: 07-09 / 15-17 ... 11-13 / 19-21
INSERT INTO public.shift (id, duration_hours, duration_minutes, soft_deleted, start_from, start_to, end_from, end_to) VALUES
(1, 8, 0, false, '07:00', '09:00', '15:00', '17:00'),
(2, 8, 0, false, '08:00', '10:00', '16:00', '18:00'),
(3, 8, 0, false, '09:00', '11:00', '17:00', '19:00'),
(4, 8, 0, false, '10:00', '12:00', '18:00', '20:00'),
(5, 8, 0, false, '11:00', '13:00', '19:00', '21:00');


-- ========== RESET SEQUENCE VALUES ==========
-- Pošto smo umetnuli eksplicitne id-eve u IDENTITY kolone, treba da pomerimo sequence-e
-- da bi auto-generisani id-evi sutra krenuli posle najvećeg postojećeg.
SELECT setval(pg_get_serial_sequence('public.permission', 'id'),      (SELECT MAX(id) FROM public.permission));
SELECT setval(pg_get_serial_sequence('public.role', 'id'),            (SELECT MAX(id) FROM public.role));
SELECT setval(pg_get_serial_sequence('public.erv_user', 'id'),       (SELECT MAX(id) FROM public.erv_user));
SELECT setval(pg_get_serial_sequence('public.department_unit', 'id'), (SELECT MAX(id) FROM public.department_unit));
SELECT setval(pg_get_serial_sequence('public.location', 'id'),        (SELECT MAX(id) FROM public.location));
SELECT setval(pg_get_serial_sequence('public.shift', 'id'),           (SELECT MAX(id) FROM public.shift));


-- ========== POVEŽI ADMIN KORISNIKA SA ODELJENJEM ==========
-- init-db.sql kreira admin-a sa department_unit = NULL
-- DataSeeder kasnije postavlja department_unit na id=1
UPDATE public.erv_user SET department_unit = 1 WHERE username = 'admin';
