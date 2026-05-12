-- 1. Usuarios
INSERT INTO user_entity (id, email, fullname, username, password, role) VALUES (NEXTVAL('user_entity_seq'), 'admin@example.com', 'Administrador', 'admin', '{noop}12345', 'ADMIN');
INSERT INTO user_entity (id, email, fullname, username, password, role) VALUES (NEXTVAL('user_entity_seq'), 'gestor@example.com', 'Usuario Gestor', 'gestor', '{noop}12345', 'GESTOR');
INSERT INTO user_entity (id, email, fullname, username, password, role) VALUES (NEXTVAL('user_entity_seq'), 'usuario@example.com', 'Usuario Normal', 'usuario', '{noop}12345', 'USUARIO');

-- 2. Categoría
INSERT INTO category (id, title) VALUES (NEXTVAL('category_seq'), 'General');

-- 3. Tareas (He puesto cada una en una sola línea para evitar errores de interpretación del parser)
INSERT INTO task (id, created_at, deadline, title, description, completed, author_id, category_id) VALUES (NEXTVAL('task_seq'), CURRENT_TIMESTAMP, TIMESTAMPADD(DAY, 7, CURRENT_TIMESTAMP), 'Comprar alimentos', 'Hacer una lista de compras.', false, (SELECT id FROM user_entity WHERE username='usuario'), (SELECT id FROM category WHERE title='General'));
INSERT INTO task (id, created_at, deadline, title, description, completed, author_id, category_id) VALUES (NEXTVAL('task_seq'), CURRENT_TIMESTAMP, TIMESTAMPADD(DAY, 2, CURRENT_TIMESTAMP), 'Pagar facturas', 'Pagar electricidad.', false, (SELECT id FROM user_entity WHERE username='usuario'), (SELECT id FROM category WHERE title='General'));