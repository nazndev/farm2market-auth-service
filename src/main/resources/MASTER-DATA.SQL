INSERT INTO permission (id, name, created_by, created_at, updated_by, updated_at)
VALUES
    (1, 'READ_USER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (2, 'CREATE_USER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (3, 'UPDATE_USER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (4, 'DELETE_USER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (5, 'READ_ROLE', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (6, 'ASSIGN_ROLE', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (7, 'CREATE_FARM', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (8, 'UPDATE_FARM', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (9, 'DELETE_FARM', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (10, 'VIEW_FARMS', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (11, 'PLACE_ORDER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (12, 'VIEW_ORDER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (13, 'MANAGE_ORDER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (14, 'ACCESS_DASHBOARD', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (15, 'MANAGE_PERMISSIONS', 'SYSTEM', NOW(), 'SYSTEM', NOW());


INSERT INTO role (id, name, created_by, created_at, updated_by, updated_at)
VALUES
    (1, 'FARMER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (2, 'BUYER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (3, 'SUPPLIER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (4, 'ADMIN', 'SYSTEM', NOW(), 'SYSTEM', NOW());



-- Permissions for FARMER role
INSERT INTO role_permissions (role_id, permission_id)
VALUES
    (1, 7),  -- CREATE_FARM
    (1, 8),  -- UPDATE_FARM
    (1, 9),  -- DELETE_FARM
    (1, 12); -- VIEW_ORDER

-- Permissions for BUYER role
INSERT INTO role_permissions (role_id, permission_id)
VALUES
    (2, 10), -- VIEW_FARMS
    (2, 11), -- PLACE_ORDER
    (2, 12); -- VIEW_ORDER

-- Permissions for SUPPLIER role
INSERT INTO role_permissions (role_id, permission_id)
VALUES
    (3, 12), -- VIEW_ORDER
    (3, 13); -- MANAGE_ORDER

-- Permissions for ADMIN role (all permissions)
INSERT INTO role_permissions (role_id, permission_id)
VALUES
    (4, 1),  -- READ_USER
    (4, 2),  -- CREATE_USER
    (4, 3),  -- UPDATE_USER
    (4, 4),  -- DELETE_USER
    (4, 5),  -- READ_ROLE
    (4, 6),  -- ASSIGN_ROLE
    (4, 7),  -- CREATE_FARM
    (4, 8),  -- UPDATE_FARM
    (4, 9),  -- DELETE_FARM
    (4, 10), -- VIEW_FARMS
    (4, 11), -- PLACE_ORDER
    (4, 12), -- VIEW_ORDER
    (4, 13), -- MANAGE_ORDER
    (4, 14), -- ACCESS_DASHBOARD
    (4, 15); -- MANAGE_PERMISSIONS


INSERT INTO user (id, username, password, email, active, created_by, created_at, updated_by, updated_at)
VALUES
    (1, 'farmer1', '$2a$10$uq9OSXM5CdGKCnNbK3cPpe4z6T7kIKng2WkeJ421AMTMjzQ7imcuu', 'farmer1@example.com', true, 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (2, 'buyer1', '$2a$10$uq9OSXM5CdGKCnNbK3cPpe4z6T7kIKng2WkeJ421AMTMjzQ7imcuu', 'buyer1@example.com', true, 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (3, 'supplier1', '$2a$10$uq9OSXM5CdGKCnNbK3cPpe4z6T7kIKng2WkeJ421AMTMjzQ7imcuu', 'supplier1@example.com', true, 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (4, 'nazim', '$2a$10$uq9OSXM5CdGKCnNbK3cPpe4z6T7kIKng2WkeJ421AMTMjzQ7imcuu', 'nazmul.nazim@wfp.org', true, 'SYSTEM', NOW(), 'SYSTEM', NOW());


-- Assign FARMER role to farmer1
INSERT INTO user_roles (user_id, role_id)
VALUES
    (1, 1);

-- Assign BUYER role to buyer1
INSERT INTO user_roles (user_id, role_id)
VALUES
    (2, 2);

-- Assign SUPPLIER role to supplier1
INSERT INTO user_roles (user_id, role_id)
VALUES
    (3, 3);

-- Assign ADMIN role to admin1
INSERT INTO user_roles (user_id, role_id)
VALUES
    (4, 4);
