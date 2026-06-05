-- =============================================================
-- GHADS - Gaza Humanitarian Aid Distribution System
-- Complete Database Schema + Seed Data
-- =============================================================

DROP DATABASE IF EXISTS ghads;
CREATE DATABASE ghads;
USE ghads;

-- =============================================================
-- 1. ORGANIZATIONS
-- =============================================================
CREATE TABLE organizations (
    org_id       INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    type         VARCHAR(50)  NOT NULL,
    contact_info VARCHAR(200)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================
-- 2. USERS  (Admin + Coordinators)
-- =============================================================
CREATE TABLE users (
    user_id   INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50)  UNIQUE NOT NULL,
    password  VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email     VARCHAR(100) UNIQUE NOT NULL,
    role      ENUM('ADMIN','COORDINATOR') NOT NULL,
    org_id    INT,
    photo     VARCHAR(255),
    FOREIGN KEY (org_id) REFERENCES organizations(org_id)
                ON DELETE SET NULL
                ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================
-- 3. FAMILIES  (Beneficiaries)
-- =============================================================
CREATE TABLE families (
    family_id          INT AUTO_INCREMENT PRIMARY KEY,
    household_name     VARCHAR(100) NOT NULL,
    phone              VARCHAR(20),
    location           VARCHAR(200),
    family_size        INT,
    national_id        VARCHAR(50) UNIQUE NOT NULL,
    vulnerability_level ENUM('HIGH','MEDIUM','LOW') NOT NULL,
    registration_date  DATE,
    last_aid_date      DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================
-- 4. AID DISTRIBUTIONS
-- =============================================================
CREATE TABLE aid_distributions (
    distribution_id   INT AUTO_INCREMENT PRIMARY KEY,
    family_id         INT NOT NULL,
    org_id            INT NOT NULL,
    distributed_by    INT NOT NULL,
    distribution_date DATE NOT NULL,
    aid_type          VARCHAR(100),
    FOREIGN KEY (family_id)      REFERENCES families(family_id)
                                ON DELETE CASCADE
                                ON UPDATE CASCADE,
    FOREIGN KEY (org_id)         REFERENCES organizations(org_id)
                                ON DELETE CASCADE
                                ON UPDATE CASCADE,
    FOREIGN KEY (distributed_by) REFERENCES users(user_id)
                                ON DELETE CASCADE
                                ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Index for the duplicate check query (family_id + aid_type + date range)
CREATE INDEX idx_dup_check ON aid_distributions(family_id, aid_type, distribution_date);

-- =============================================================
-- 5. SAMPLE DATA — Organizations
-- =============================================================
INSERT INTO organizations (name, type, contact_info) VALUES
('UNRWA Gaza',              'UN Agency',    'unrwa@gaza.org / +970-8-288-0000'),
('Palestine Red Crescent',  'Medical',      'prc@gaza.org / +970-8-264-0000'),
('WFP Gaza',                'Food Aid',     'wfp@gaza.org / +970-8-282-0000'),
('Qatar Charity',           'NGO',          'info@qcharity.ps / +970-8-286-0000'),
('Islamic Relief Palestine','NGO',          'gaza@irpal.org / +970-8-283-0000');

-- =============================================================
-- 6. SAMPLE DATA — Users
-- =============================================================
INSERT INTO users (username, password, full_name, email, role, org_id) VALUES
-- Admin
('admin',    'admin123', 'System Admin',        'admin@ghads.org',  'ADMIN', NULL),

-- Coordinators (one per organization)
('ahmed',    'ahmed123', 'Ahmed Al-Masri',      'ahmed@unrwa.org',  'COORDINATOR', 1),
('mariam',   'mariam123','Mariam Hassan',       'mariam@prc.org',   'COORDINATOR', 2),
('yousef',   'yousef123','Yousef Abu Amra',     'yousef@wfp.org',   'COORDINATOR', 3),
('fatima',   'fatima123','Fatima Al-Najjar',    'fatima@qcharity.ps','COORDINATOR', 4),
('huda',     'huda123',  'Huda Al-Sammak',      'huda@irpal.org',   'COORDINATOR', 5);

-- =============================================================
-- 7. SAMPLE DATA — Families
-- =============================================================
INSERT INTO families (household_name, phone, location, family_size, national_id, vulnerability_level, registration_date, last_aid_date) VALUES

-- HIGH vulnerability families
('Al-Masri Family',    '059-100-0001', 'Jabalia Camp',         8,  '800-0001', 'HIGH',   '2026-01-10', '2026-03-15'),
('Abu Laila Family',   '059-100-0002', 'Shejaiya',             6,  '800-0002', 'HIGH',   '2026-01-12', NULL),
('Al-Haddad Family',   '059-100-0003', 'Rimal North',          7,  '800-0003', 'HIGH',   '2026-01-15', '2026-04-01'),
('Shami Family',       '059-100-0004', 'Beach Camp',           5,  '800-0004', 'HIGH',   '2026-01-18', NULL),
('Al-Astal Family',    '059-100-0005', 'Khan Younis',          9,  '800-0005', 'HIGH',   '2026-01-20', '2026-04-10'),
('Ashour Family',      '059-100-0006', 'Deir Al-Balah',        6,  '800-0006', 'HIGH',   '2026-01-22', NULL),
('Abu Warda Family',   '059-100-0007', 'Rafah',                7,  '800-0007', 'HIGH',   '2026-01-25', NULL),

-- MEDIUM vulnerability families
('Al-Najjar Family',   '059-100-0008', 'Rimal South',          4,  '800-0008', 'MEDIUM', '2026-02-01', '2026-05-01'),
('Abu Odeh Family',    '059-100-0009', 'Zaitoun',              5,  '800-0009', 'MEDIUM', '2026-02-03', NULL),
('Al-Helo Family',     '059-100-0010', 'Mughraqa',             3,  '800-0010', 'MEDIUM', '2026-02-05', NULL),
('Baraka Family',      '059-100-0011', 'Nuseirat Camp',        5,  '800-0011', 'MEDIUM', '2026-02-08', '2026-05-10'),
('Saqr Family',        '059-100-0012', 'Bureij Camp',          4,  '800-0012', 'MEDIUM', '2026-02-10', NULL),
('Al-Baba Family',     '059-100-0013', 'Maghazi Camp',         6,  '800-0013', 'MEDIUM', '2026-02-12', NULL),

-- LOW vulnerability families
('Sultan Family',      '059-100-0014', 'Tel Al-Hawa',          3,  '800-0014', 'LOW',    '2026-03-01', NULL),
('Abu Taha Family',    '059-100-0015', 'Shuja\'iyya',          4,  '800-0015', 'LOW',    '2026-03-03', NULL),
('Al-Borno Family',    '059-100-0016', 'Beit Lahia',           2,  '800-0016', 'LOW',    '2026-03-05', NULL),
('Khader Family',      '059-100-0017', 'Beit Hanoun',          5,  '800-0017', 'LOW',    '2026-03-08', NULL),
('Al-Sammak Family',   '059-100-0018', 'Al-Shati Camp',        3,  '800-0018', 'LOW',    '2026-03-10', NULL);

-- =============================================================
-- 8. SAMPLE DATA — Aid Distributions
-- =============================================================
INSERT INTO aid_distributions (family_id, org_id, distributed_by, distribution_date, aid_type) VALUES

-- Al-Masri (HIGH) → received food from UNRWA, then food again from Qatar Charity (within 30 days — HIGH so allowed)
(1, 1, 2, '2026-03-15', 'Food'),
(1, 4, 5, '2026-03-28', 'Food'),

-- Al-Haddad (HIGH) → received food from Red Crescent
(3, 2, 3, '2026-04-01', 'Food'),

-- Al-Astal (HIGH) → received food from WFP
(5, 3, 4, '2026-04-10', 'Food'),

-- Al-Najjar (MEDIUM) → received food from UNRWA on May 1
-- If someone tries to give Food again within 30 days → REJECTED
(8, 1, 2, '2026-05-01', 'Food'),

-- Baraka (MEDIUM) → received Food from Qatar Charity on May 10
(11, 4, 5, '2026-05-10', 'Food');

-- =============================================================
-- 9. USEFUL QUERIES (for reference)
-- =============================================================

-- All families with their latest distribution info
-- SELECT f.*, ad.distribution_date, ad.aid_type, o.name AS org_name
-- FROM families f
-- LEFT JOIN aid_distributions ad ON f.family_id=ad.family_id
-- LEFT JOIN organizations o ON ad.org_id=o.org_id
-- ORDER BY FIELD(f.vulnerability_level,'HIGH','MEDIUM','LOW'), f.household_name;

-- Families NOT served by any organization
-- SELECT f.* FROM families f
-- LEFT JOIN aid_distributions ad ON f.family_id=ad.family_id
-- WHERE ad.distribution_id IS NULL
-- ORDER BY FIELD(f.vulnerability_level,'HIGH','MEDIUM','LOW');

-- Duplicate check: families who received same aid_type within last 30 days
-- SELECT f.household_name, f.vulnerability_level, ad.aid_type, ad.distribution_date, o.name
-- FROM aid_distributions ad
-- JOIN families f ON ad.family_id=f.family_id
-- JOIN organizations o ON ad.org_id=o.org_id
-- WHERE ad.distribution_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY);
