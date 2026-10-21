INSERT INTO producto (nombre, categoria, descripcion, precio, stock_actual, stock_minimo, activo)
SELECT 'Misterpet Adultos Todo Tamaño 15 kg', 'Alimento para perros',
       'Alimento seco para perros adultos de todos los tamaños', 380.00, 8, 2, TRUE
WHERE NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Misterpet Adultos Todo Tamaño 15 kg');

INSERT INTO producto (nombre, categoria, descripcion, precio, stock_actual, stock_minimo, activo)
SELECT 'Brain Plus Gatos Castrados 10 kg', 'Alimento para gatos',
       'Alimento seco para gatos adultos castrados', 270.00, 8, 2, TRUE
WHERE NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Brain Plus Gatos Castrados 10 kg');

INSERT INTO producto (nombre, categoria, descripcion, precio, stock_actual, stock_minimo, activo)
SELECT 'Cat Chow Adultos Carne 85 g', 'Alimento húmedo para gatos',
       'Sobre de alimento húmedo para gatos adultos sabor carne', 10.40, 24, 6, TRUE
WHERE NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Cat Chow Adultos Carne 85 g');

INSERT INTO producto (nombre, categoria, descripcion, precio, stock_actual, stock_minimo, activo)
SELECT 'Pedigree Adulto Carne 100 g', 'Alimento húmedo para perros',
       'Sobre de alimento húmedo para perros adultos sabor carne', 12.20, 24, 6, TRUE
WHERE NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Pedigree Adulto Carne 100 g');

INSERT INTO producto (nombre, categoria, descripcion, precio, stock_actual, stock_minimo, activo)
SELECT 'Arena sanitaria Catsan 4 kg', 'Higiene para gatos',
       'Arena sanitaria absorbente para gatos en bolsa de 4 kg', 39.80, 12, 3, TRUE
WHERE NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Arena sanitaria Catsan 4 kg');

INSERT INTO producto (nombre, categoria, descripcion, precio, stock_actual, stock_minimo, activo)
SELECT 'Mata pulgas spray 300 ml', 'Higiene y cuidado',
       'Spray para el control externo de pulgas en mascotas', 45.00, 10, 3, TRUE
WHERE NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Mata pulgas spray 300 ml');

INSERT INTO producto (nombre, categoria, descripcion, precio, stock_actual, stock_minimo, activo)
SELECT 'Palitos Guauu 8 unidades', 'Premios para perros',
       'Snack para perros en bolsa de 8 unidades', 16.20, 18, 5, TRUE
WHERE NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Palitos Guauu 8 unidades');
