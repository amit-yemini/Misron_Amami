INSERT INTO msa.launch_country (id, name, external_id) VALUES
(1, 'Iran', 1001),
(2, 'Gaza', 1002),
(3, 'Lebanon', 1003);

INSERT INTO msa.missile_type (id, name, external_id) VALUES
(1, 'Short-Range Rocket', 2001),
(2, 'Ballistic Missile', 2002),
(3, 'Cruise Missile', 2003);

INSERT INTO msa.alert_type (id, name, category, event, distribution_time) VALUES
(1, 'Red Alert',      'SECURITY',       'MISSILE', 30),
(2, 'Test Notification', 'OTHER',       'TEST',   90);

INSERT INTO msa.alert_to_missile (alert_type_id, missile_type_id) VALUES
(1, 1), -- Red alert -> Short range rocket
(1, 2), -- Red alert -> Ballistic missile
(2, 2); -- Test -> Ballistic missile
