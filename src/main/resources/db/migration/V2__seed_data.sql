-- Seed roles, permissions, demo gym, and admin user
-- Default password: Admin@123 (BCrypt)

INSERT INTO roles (id, name, display_name, description) VALUES
    ('11111111-1111-1111-1111-111111111101', 'SUPER_ADMIN', 'Super Admin', 'Platform super administrator'),
    ('11111111-1111-1111-1111-111111111102', 'GYM_OWNER', 'Gym Owner', 'Gym owner with full access'),
    ('11111111-1111-1111-1111-111111111103', 'MANAGER', 'Manager', 'Branch manager'),
    ('11111111-1111-1111-1111-111111111104', 'RECEPTIONIST', 'Receptionist', 'Front desk operations'),
    ('11111111-1111-1111-1111-111111111105', 'TRAINER', 'Trainer', 'Fitness trainer'),
    ('11111111-1111-1111-1111-111111111106', 'HR', 'HR', 'Human resources'),
    ('11111111-1111-1111-1111-111111111107', 'ACCOUNTANT', 'Accountant', 'Finance and accounting'),
    ('11111111-1111-1111-1111-111111111108', 'MEMBER', 'Member', 'Gym member portal access');

INSERT INTO permissions (id, code, module, description) VALUES
    ('22222222-2222-2222-2222-222222222201', 'dashboard:read', 'dashboard', 'View dashboard'),
    ('22222222-2222-2222-2222-222222222202', 'members:read', 'members', 'View members'),
    ('22222222-2222-2222-2222-222222222203', 'members:write', 'members', 'Manage members'),
    ('22222222-2222-2222-2222-222222222204', 'staff:read', 'staff', 'View staff'),
    ('22222222-2222-2222-2222-222222222205', 'staff:write', 'staff', 'Manage staff'),
    ('22222222-2222-2222-2222-222222222206', 'attendance:read', 'attendance', 'View attendance'),
    ('22222222-2222-2222-2222-222222222207', 'attendance:write', 'attendance', 'Manage attendance'),
    ('22222222-2222-2222-2222-222222222208', 'payroll:read', 'payroll', 'View payroll'),
    ('22222222-2222-2222-2222-222222222209', 'payroll:write', 'payroll', 'Manage payroll'),
    ('22222222-2222-2222-2222-222222222210', 'finance:read', 'finance', 'View finance'),
    ('22222222-2222-2222-2222-222222222211', 'finance:write', 'finance', 'Manage finance'),
    ('22222222-2222-2222-2222-222222222212', 'settings:read', 'settings', 'View settings'),
    ('22222222-2222-2222-2222-222222222213', 'settings:write', 'settings', 'Manage settings');

-- Grant all permissions to GYM_OWNER
INSERT INTO role_permissions (role_id, permission_id)
SELECT '11111111-1111-1111-1111-111111111102', id FROM permissions;

INSERT INTO gyms (id, name, slug, email, phone, city, state) VALUES
    ('33333333-3333-3333-3333-333333333301', 'IronCore Fitness', 'ironcore', 'hello@ironcore.fitness', '+91-9876543210', 'Mumbai', 'Maharashtra');

INSERT INTO branches (id, gym_id, name, code, city, is_head_office) VALUES
    ('44444444-4444-4444-4444-444444444401', '33333333-3333-3333-3333-333333333301', 'Bandra West', 'BND', 'Mumbai', TRUE);

INSERT INTO users (id, gym_id, branch_id, email, password_hash, first_name, last_name, phone, active, email_verified) VALUES
    ('55555555-5555-5555-5555-555555555501',
     '33333333-3333-3333-3333-333333333301',
     '44444444-4444-4444-4444-444444444401',
     'admin@ironcore.fitness',
     '$2a$10$m76R259BHpRaX6tEh/34xOgdu.ChfghahW38BLF.uPai4zNGUfCIm',
     'Alex',
     'Petrov',
     '+91-9876543211',
     TRUE,
     TRUE);

INSERT INTO user_roles (user_id, role_id) VALUES
    ('55555555-5555-5555-5555-555555555501', '11111111-1111-1111-1111-111111111102');

INSERT INTO gym_settings (gym_id, working_hours, tax_config) VALUES
    ('33333333-3333-3333-3333-333333333301',
     '{"monday":{"open":"06:00","close":"22:00"},"tuesday":{"open":"06:00","close":"22:00"}}'::jsonb,
     '{"gst_rate":18}'::jsonb);

INSERT INTO membership_plans (id, gym_id, name, code, duration_days, price, plan_type) VALUES
    ('66666666-6666-6666-6666-666666666601', '33333333-3333-3333-3333-333333333301', 'Monthly', 'MONTHLY', 30, 2500.00, 'GENERAL'),
    ('66666666-6666-6666-6666-666666666602', '33333333-3333-3333-3333-333333333301', 'Quarterly', 'QUARTERLY', 90, 6500.00, 'GENERAL'),
    ('66666666-6666-6666-6666-666666666603', '33333333-3333-3333-3333-333333333301', 'Yearly', 'YEARLY', 365, 22000.00, 'GENERAL'),
    ('66666666-6666-6666-6666-666666666604', '33333333-3333-3333-3333-333333333301', 'Personal Training', 'PT', 30, 12000.00, 'PERSONAL_TRAINING');

INSERT INTO leave_types (gym_id, code, name, paid, max_days) VALUES
    ('33333333-3333-3333-3333-333333333301', 'CL', 'Casual Leave', TRUE, 12),
    ('33333333-3333-3333-3333-333333333301', 'SL', 'Sick Leave', TRUE, 10),
    ('33333333-3333-3333-3333-333333333301', 'PL', 'Paid Leave', TRUE, 15);
