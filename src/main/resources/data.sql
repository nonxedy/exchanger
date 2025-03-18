-- Добавление валют
INSERT INTO currencies (code, full_name, sign) VALUES
('USD', 'United States dollar', '$'),
('EUR', 'Euro', '€'),
('GBP', 'British pound', '£'),
('JPY', 'Japanese yen', '¥'),
('AUD', 'Australian dollar', 'A$'),
('CAD', 'Canadian dollar', 'C$'),
('CHF', 'Swiss franc', 'Fr'),
('CNY', 'Chinese yuan', '¥'),
('RUB', 'Russian ruble', '₽')
ON CONFLICT (code) DO NOTHING;

-- Добавление обменных курсов
-- USD к другим валютам
INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) 
SELECT c1.id, c2.id, 0.92
FROM currencies c1, currencies c2
WHERE c1.code = 'USD' AND c2.code = 'EUR'
ON CONFLICT (base_currency_id, target_currency_id) DO NOTHING;

INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) 
SELECT c1.id, c2.id, 0.79
FROM currencies c1, currencies c2
WHERE c1.code = 'USD' AND c2.code = 'GBP'
ON CONFLICT (base_currency_id, target_currency_id) DO NOTHING;

INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) 
SELECT c1.id, c2.id, 149.50
FROM currencies c1, currencies c2
WHERE c1.code = 'USD' AND c2.code = 'JPY'
ON CONFLICT (base_currency_id, target_currency_id) DO NOTHING;

INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) 
SELECT c1.id, c2.id, 1.52
FROM currencies c1, currencies c2
WHERE c1.code = 'USD' AND c2.code = 'AUD'
ON CONFLICT (base_currency_id, target_currency_id) DO NOTHING;

INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) 
SELECT c1.id, c2.id, 1.36
FROM currencies c1, currencies c2
WHERE c1.code = 'USD' AND c2.code = 'CAD'
ON CONFLICT (base_currency_id, target_currency_id) DO NOTHING;

INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) 
SELECT c1.id, c2.id, 0.89
FROM currencies c1, currencies c2
WHERE c1.code = 'USD' AND c2.code = 'CHF'
ON CONFLICT (base_currency_id, target_currency_id) DO NOTHING;

INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) 
SELECT c1.id, c2.id, 7.25
FROM currencies c1, currencies c2
WHERE c1.code = 'USD' AND c2.code = 'CNY'
ON CONFLICT (base_currency_id, target_currency_id) DO NOTHING;

INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) 
SELECT c1.id, c2.id, 91.20
FROM currencies c1, currencies c2
WHERE c1.code = 'USD' AND c2.code = 'RUB'
ON CONFLICT (base_currency_id, target_currency_id) DO NOTHING;

-- EUR к другим валютам
INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) 
SELECT c1.id, c2.id, 1.09
FROM currencies c1, currencies c2
WHERE c1.code = 'EUR' AND c2.code = 'USD'
ON CONFLICT (base_currency_id, target_currency_id) DO NOTHING;

INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) 
SELECT c1.id, c2.id, 0.86
FROM currencies c1, currencies c2
WHERE c1.code = 'EUR' AND c2.code = 'GBP'
ON CONFLICT (base_currency_id, target_currency_id) DO NOTHING;
