-- Produtos de exemplo para a demo. Os usuarios padrao sao criados no boot
-- (DataSeeder), porque a senha precisa ser gerada com hash BCrypt.

insert into produto (id, codigo_barras, descricao, preco, categoria, ativo) values
    ('11111111-1111-1111-1111-111111111111', '7891000100103', 'Coca-Cola 350ml',   5.50, 'BEBIDA',   true),
    ('22222222-2222-2222-2222-222222222222', '7891910000197', 'Agua Mineral 500ml', 2.50, 'BEBIDA',   true),
    ('33333333-3333-3333-3333-333333333333', '7896005800010', 'Salgadinho 100g',    8.90, 'ALIMENTO', true),
    ('44444444-4444-4444-4444-444444444444', '7891150056788', 'Sabonete 90g',       3.20, 'HIGIENE',  true);

insert into estoque (produto_id, quantidade, versao) values
    ('11111111-1111-1111-1111-111111111111', 100, 0),
    ('22222222-2222-2222-2222-222222222222', 200, 0),
    ('33333333-3333-3333-3333-333333333333',  50, 0),
    ('44444444-4444-4444-4444-444444444444',  30, 0);
