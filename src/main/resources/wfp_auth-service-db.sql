INSERT INTO role (id, name, created_by, created_at, updated_by, updated_at)
VALUES
    (1, 'FARMER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (2, 'RETAILER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (3, 'AGGREGATION_CENTER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (4, 'ADMIN', 'SYSTEM', NOW(), 'SYSTEM', NOW());


INSERT INTO permission (id, name, created_by, created_at, updated_by, updated_at)
VALUES
    (1, 'CREATE_USER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (2, 'READ_USER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (3, 'UPDATE_USER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (4, 'DELETE_USER',  'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (5, 'MANAGE_PERMISSIONS', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (6, 'MANAGE_ROLE',  'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (7, 'ASSIGN_ROLE',  'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (8, 'ACCESS_DASHBOARD', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (9, 'ADD_FARMER',  'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (10, 'UPDATE_FARMER',  'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (11, 'VIEW_FARMER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (12, 'DELETE_FARMER',  'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (13, 'CREATE_LOCATION',  'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (14, 'VIEW_LOCATION',  'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (15, 'ADD_PRODUCT_TRACEABILITY',  'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (16, 'PUBLISH_PRODUCT',  'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (17, 'CREATE_SMART_CONTRACT',  'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (18, 'UPDATE_SMART_CONTRACT',  'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (19, 'VIEW_SMART_CONTRACT', 'SYSTEM', NOW(), 'SYSTEM', NOW());

# For Market Place Service
INSERT INTO permission (id, name, created_by, created_at, updated_by, updated_at)
VALUES
    (20, 'CREATE_OFFER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (21, 'VIEW_OFFER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (22, 'MANAGE_OFFER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (23, 'CREATE_ORDER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (24, 'VIEW_ORDER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (25, 'MANAGE_ORDER', 'SYSTEM', NOW(), 'SYSTEM', NOW());


INSERT INTO permission (id, name, created_by, created_at, updated_by, updated_at)
VALUES
    (26, 'CREATE_RETAILER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (27, 'VIEW_RETAILER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (28, 'UPDATE_RETAILER', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (29, 'DELETE_RETAILER', 'SYSTEM', NOW(), 'SYSTEM', NOW());


INSERT INTO role_permissions (role_id, permission_id)
VALUES
    (1, 9), -- FARMER can ADD_FARMER
    (1, 10), -- FARMER can UPDATE_FARMER
    (1, 11), -- FARMER can VIEW_FARMER
    (1, 12), -- FARMER can DELETE_FARMER
    (2, 15), -- RETAILER can ADD_PRODUCT_TRACEABILITY
    (2, 16), -- RETAILER can PUBLISH_PRODUCT
    (2, 19), -- RETAILER can VIEW_SMART_CONTRACT
    (3, 17), -- AGGREGATION_CENTER can CREATE_SMART_CONTRACT
    (3, 18), -- AGGREGATION_CENTER can UPDATE_SMART_CONTRACT
    (3, 19), -- AGGREGATION_CENTER can VIEW_SMART_CONTRACT
    (4, 1), -- ADMIN can CREATE_USER
    (4, 2), -- ADMIN can READ_USER
    (4, 3), -- ADMIN can UPDATE_USER
    (4, 4), -- ADMIN can DELETE_USER
    (4, 5), -- ADMIN can MANAGE_PERMISSIONS
    (4, 6), -- ADMIN can MANAGE_ROLE
    (4, 7), -- ADMIN can ASSIGN_ROLE
    (4, 8); -- ADMIN can ACCESS_DASHBOARD


INSERT INTO permission (id, name, created_by, created_at, updated_by, updated_at)
VALUES
    (30, 'UPDATE_PRODUCT', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (31, 'DELETE_PRODUCT', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (32, 'VIEW_PRODUCT', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (33, 'VIEW_PRODUCT_LIST', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO permission (id, name, created_by, created_at, updated_by, updated_at)
VALUES
    (34, 'ARCHIVE_PRODUCT', 'SYSTEM', NOW(), 'SYSTEM', NOW());


-- Role Permissions for Aggregation Centers
INSERT INTO role_permissions (role_id, permission_id)
VALUES
    (3, (SELECT id FROM permission WHERE name = 'ARCHIVE_PRODUCT')); -- Aggregation Center

-- Role Permissions for Admins
INSERT INTO role_permissions (role_id, permission_id)
VALUES
    (4, (SELECT id FROM permission WHERE name = 'DELETE_PRODUCT')); -- Admin


# For Market Place Service Permission
INSERT INTO role_permissions (role_id, permission_id)
VALUES
    (2, (SELECT id FROM permission WHERE name = 'CREATE_OFFER')), -- Retailer
    (2, (SELECT id FROM permission WHERE name = 'VIEW_OFFER')),   -- Retailer
    (3, (SELECT id FROM permission WHERE name = 'MANAGE_OFFER')), -- Aggregation Center
    (2, (SELECT id FROM permission WHERE name = 'CREATE_ORDER')), -- Retailer
    (2, (SELECT id FROM permission WHERE name = 'VIEW_ORDER')),   -- Retailer
    (3, (SELECT id FROM permission WHERE name = 'MANAGE_ORDER')); -- Aggregation Center


INSERT INTO role_permissions (role_id, permission_id)
VALUES
    (4, 26), -- Admin can create retailers
    (4, 27), -- Admin can view retailers
    (4, 28), -- Admin can update retailers
    (4, 29); -- Admin can delete retailers


INSERT INTO user (id, username, password, email, active, created_by, created_at, updated_by, updated_at)
VALUES
    (1, 'admin', '$2a$10$uq9OSXM5CdGKCnNbK3cPpe4z6T7kIKng2WkeJ421AMTMjzQ7imcuu', 'nazim@test.com', true, 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (2, 'farmer', '$2a$10$uq9OSXM5CdGKCnNbK3cPpe4z6T7kIKng2WkeJ421AMTMjzQ7imcuu', 'farmer@test.com', true, 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (3, 'retailer', '$2a$10$uq9OSXM5CdGKCnNbK3cPpe4z6T7kIKng2WkeJ421AMTMjzQ7imcuu', 'retailer@test.com', true, 'SYSTEM', NOW(), 'SYSTEM', NOW()),
    (4, 'aggregation', '$2a$10$uq9OSXM5CdGKCnNbK3cPpe4z6T7kIKng2WkeJ421AMTMjzQ7imcuu', 'aggregation@test.com', true, 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO user_roles (user_id, role_id)
VALUES
    (1, 4), -- Admin user
    (2, 1), -- Farmer user
    (3, 2), -- Retailer user
    (4, 3); -- Aggregation Center user



INSERT INTO role_permissions (role_id, permission_id)
SELECT 4, id FROM permission
    ON DUPLICATE KEY UPDATE role_id = role_id;

INSERT INTO user_roles (user_id, role_id)
SELECT 1, id FROM role
    ON DUPLICATE KEY UPDATE user_id = user_id;




