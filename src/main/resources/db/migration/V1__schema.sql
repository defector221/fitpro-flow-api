-- IronCore Gym Management — Core Schema (multi-branch ready)

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ─── Organization ───────────────────────────────────────────────────────────

CREATE TABLE gyms (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(200) NOT NULL,
    slug            VARCHAR(100) NOT NULL UNIQUE,
    email           VARCHAR(255),
    phone           VARCHAR(20),
    address         TEXT,
    city            VARCHAR(100),
    state           VARCHAR(100),
    country         VARCHAR(100) DEFAULT 'India',
    pincode         VARCHAR(10),
    gstin           VARCHAR(20),
    logo_url        VARCHAR(500),
    timezone        VARCHAR(50) DEFAULT 'Asia/Kolkata',
    currency        VARCHAR(3) DEFAULT 'INR',
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE branches (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    gym_id          UUID NOT NULL REFERENCES gyms(id) ON DELETE CASCADE,
    name            VARCHAR(200) NOT NULL,
    code            VARCHAR(20) NOT NULL,
    address         TEXT,
    city            VARCHAR(100),
    phone           VARCHAR(20),
    is_head_office  BOOLEAN NOT NULL DEFAULT FALSE,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (gym_id, code)
);

-- ─── Auth & RBAC ────────────────────────────────────────────────────────────

CREATE TABLE roles (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(50) NOT NULL UNIQUE,
    display_name    VARCHAR(100) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE permissions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code            VARCHAR(100) NOT NULL UNIQUE,
    module          VARCHAR(50) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE role_permissions (
    role_id         UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id   UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    gym_id          UUID REFERENCES gyms(id) ON DELETE SET NULL,
    branch_id       UUID REFERENCES branches(id) ON DELETE SET NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    phone           VARCHAR(20),
    avatar_url      VARCHAR(500),
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    email_verified  BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at   TIMESTAMPTZ,
    last_login_ip   VARCHAR(45),
    failed_attempts INT NOT NULL DEFAULT 0,
    locked_until    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE user_roles (
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id         UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE refresh_tokens (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash      VARCHAR(255) NOT NULL UNIQUE,
    expires_at      TIMESTAMPTZ NOT NULL,
    revoked         BOOLEAN NOT NULL DEFAULT FALSE,
    ip_address      VARCHAR(45),
    user_agent      TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE audit_logs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    gym_id          UUID REFERENCES gyms(id) ON DELETE SET NULL,
    user_id         UUID REFERENCES users(id) ON DELETE SET NULL,
    action          VARCHAR(50) NOT NULL,
    entity_type     VARCHAR(100),
    entity_id       UUID,
    old_value       JSONB,
    new_value       JSONB,
    ip_address      VARCHAR(45),
    user_agent      TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE login_history (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    ip_address      VARCHAR(45),
    user_agent      TEXT,
    success         BOOLEAN NOT NULL,
    failure_reason  VARCHAR(255),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ─── Staff / HR ─────────────────────────────────────────────────────────────

CREATE TABLE employees (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    gym_id          UUID NOT NULL REFERENCES gyms(id) ON DELETE CASCADE,
    branch_id       UUID NOT NULL REFERENCES branches(id) ON DELETE CASCADE,
    user_id         UUID REFERENCES users(id) ON DELETE SET NULL,
    employee_code   VARCHAR(20) NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(255),
    phone           VARCHAR(20),
    date_of_birth   DATE,
    gender          VARCHAR(20),
    address         TEXT,
    emergency_name  VARCHAR(200),
    emergency_phone VARCHAR(20),
    department      VARCHAR(100),
    designation     VARCHAR(100),
    joining_date    DATE NOT NULL,
    employment_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    basic_salary    DECIMAL(12,2),
    photo_url       VARCHAR(500),
    qr_code         VARCHAR(500),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (gym_id, employee_code)
);

CREATE TABLE employee_attendance (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id     UUID NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    branch_id       UUID NOT NULL REFERENCES branches(id),
    attendance_date DATE NOT NULL,
    check_in        TIMESTAMPTZ,
    check_out       TIMESTAMPTZ,
    status          VARCHAR(30) NOT NULL DEFAULT 'PRESENT',
    source          VARCHAR(30) NOT NULL DEFAULT 'MANUAL',
    notes           TEXT,
    approved        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (employee_id, attendance_date)
);

CREATE TABLE leave_types (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    gym_id          UUID NOT NULL REFERENCES gyms(id) ON DELETE CASCADE,
    code            VARCHAR(20) NOT NULL,
    name            VARCHAR(100) NOT NULL,
    paid            BOOLEAN NOT NULL DEFAULT TRUE,
    max_days        INT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (gym_id, code)
);

CREATE TABLE leave_requests (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id     UUID NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    leave_type_id   UUID NOT NULL REFERENCES leave_types(id),
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    days            DECIMAL(4,1) NOT NULL,
    reason          TEXT,
    status          VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    approved_by     UUID REFERENCES users(id),
    approved_at     TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE payroll_runs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    gym_id          UUID NOT NULL REFERENCES gyms(id) ON DELETE CASCADE,
    branch_id       UUID REFERENCES branches(id),
    period_month    INT NOT NULL,
    period_year     INT NOT NULL,
    status          VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    total_amount    DECIMAL(14,2) DEFAULT 0,
    processed_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (gym_id, branch_id, period_month, period_year)
);

CREATE TABLE payslips (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payroll_run_id  UUID NOT NULL REFERENCES payroll_runs(id) ON DELETE CASCADE,
    employee_id     UUID NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    basic           DECIMAL(12,2) NOT NULL DEFAULT 0,
    hra             DECIMAL(12,2) NOT NULL DEFAULT 0,
    allowances      DECIMAL(12,2) NOT NULL DEFAULT 0,
    deductions      DECIMAL(12,2) NOT NULL DEFAULT 0,
    net_salary      DECIMAL(12,2) NOT NULL DEFAULT 0,
    status          VARCHAR(30) NOT NULL DEFAULT 'GENERATED',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (payroll_run_id, employee_id)
);

-- ─── Members & Membership ───────────────────────────────────────────────────

CREATE TABLE membership_plans (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    gym_id          UUID NOT NULL REFERENCES gyms(id) ON DELETE CASCADE,
    name            VARCHAR(200) NOT NULL,
    code            VARCHAR(50) NOT NULL,
    description     TEXT,
    duration_days   INT NOT NULL,
    price           DECIMAL(12,2) NOT NULL,
    plan_type       VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (gym_id, code)
);

CREATE TABLE members (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    gym_id          UUID NOT NULL REFERENCES gyms(id) ON DELETE CASCADE,
    branch_id       UUID NOT NULL REFERENCES branches(id) ON DELETE CASCADE,
    member_code     VARCHAR(20) NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(255),
    phone           VARCHAR(20) NOT NULL,
    date_of_birth   DATE,
    gender          VARCHAR(20),
    address         TEXT,
    emergency_name  VARCHAR(200),
    emergency_phone VARCHAR(20),
    height_cm       DECIMAL(5,1),
    weight_kg       DECIMAL(5,1),
    fitness_goals   TEXT,
    medical_notes   TEXT,
    photo_url       VARCHAR(500),
    status          VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    referral_code   VARCHAR(50),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (gym_id, member_code)
);

CREATE TABLE memberships (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id       UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    plan_id         UUID NOT NULL REFERENCES membership_plans(id),
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    amount_paid     DECIMAL(12,2) NOT NULL DEFAULT 0,
    status          VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    frozen          BOOLEAN NOT NULL DEFAULT FALSE,
    frozen_from     DATE,
    frozen_until    DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE member_check_ins (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id       UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    branch_id       UUID NOT NULL REFERENCES branches(id),
    check_in_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    check_out_at    TIMESTAMPTZ,
    source          VARCHAR(30) NOT NULL DEFAULT 'QR',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ─── Trainers ───────────────────────────────────────────────────────────────

CREATE TABLE trainers (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id     UUID NOT NULL UNIQUE REFERENCES employees(id) ON DELETE CASCADE,
    specialization  VARCHAR(200),
    bio             TEXT,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE trainer_assignments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    trainer_id      UUID NOT NULL REFERENCES trainers(id) ON DELETE CASCADE,
    member_id       UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    start_date      DATE NOT NULL,
    end_date        DATE,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ─── Finance ─────────────────────────────────────────────────────────────────

CREATE TABLE payments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    gym_id          UUID NOT NULL REFERENCES gyms(id) ON DELETE CASCADE,
    branch_id       UUID REFERENCES branches(id),
    member_id       UUID REFERENCES members(id) ON DELETE SET NULL,
    amount          DECIMAL(12,2) NOT NULL,
    payment_method  VARCHAR(50) NOT NULL DEFAULT 'CASH',
    payment_type    VARCHAR(50) NOT NULL DEFAULT 'MEMBERSHIP',
    status          VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    reference_no    VARCHAR(100),
    notes           TEXT,
    paid_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE expenses (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    gym_id          UUID NOT NULL REFERENCES gyms(id) ON DELETE CASCADE,
    branch_id       UUID REFERENCES branches(id),
    category        VARCHAR(100) NOT NULL,
    description     TEXT,
    amount          DECIMAL(12,2) NOT NULL,
    expense_date    DATE NOT NULL,
    vendor          VARCHAR(200),
    gst_amount      DECIMAL(12,2) DEFAULT 0,
    created_by      UUID REFERENCES users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ─── CRM ────────────────────────────────────────────────────────────────────

CREATE TABLE leads (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    gym_id          UUID NOT NULL REFERENCES gyms(id) ON DELETE CASCADE,
    branch_id       UUID REFERENCES branches(id),
    name            VARCHAR(200) NOT NULL,
    email           VARCHAR(255),
    phone           VARCHAR(20),
    source          VARCHAR(50) NOT NULL DEFAULT 'WALK_IN',
    status          VARCHAR(30) NOT NULL DEFAULT 'NEW',
    assigned_to     UUID REFERENCES users(id),
    notes           TEXT,
    follow_up_date  DATE,
    converted_member_id UUID REFERENCES members(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ─── Inventory ──────────────────────────────────────────────────────────────

CREATE TABLE inventory_items (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    gym_id          UUID NOT NULL REFERENCES gyms(id) ON DELETE CASCADE,
    branch_id       UUID REFERENCES branches(id),
    sku             VARCHAR(50) NOT NULL,
    name            VARCHAR(200) NOT NULL,
    category        VARCHAR(100) NOT NULL,
    quantity        INT NOT NULL DEFAULT 0,
    unit_price      DECIMAL(12,2),
    reorder_level   INT DEFAULT 5,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (gym_id, sku)
);

-- ─── Settings ─────────────────────────────────────────────────────────────────

CREATE TABLE gym_settings (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    gym_id          UUID NOT NULL UNIQUE REFERENCES gyms(id) ON DELETE CASCADE,
    working_hours   JSONB,
    membership_rules JSONB,
    tax_config      JSONB,
    notification_config JSONB,
    payment_config  JSONB,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ─── Indexes ─────────────────────────────────────────────────────────────────

CREATE INDEX idx_users_gym ON users(gym_id);
CREATE INDEX idx_employees_gym ON employees(gym_id);
CREATE INDEX idx_members_gym ON members(gym_id);
CREATE INDEX idx_memberships_member ON memberships(member_id);
CREATE INDEX idx_member_check_ins_date ON member_check_ins(check_in_at);
CREATE INDEX idx_payments_gym_date ON payments(gym_id, paid_at);
CREATE INDEX idx_audit_logs_gym ON audit_logs(gym_id, created_at);
CREATE INDEX idx_leads_gym_status ON leads(gym_id, status);
