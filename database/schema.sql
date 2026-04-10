-- NOTE:
-- This schema represents the final AWS RDS database structure for the Co-Match application, designed to meet all functional and non-functional requirements outlined in the project documentation. 
-- It includes tables for user authentication, profile management, skills and sectors, swipe actions, matches, and safety features. The schema is optimized for performance with appropriate indexing and enforces data integrity through foreign key constraints.
-- including tables created via backend (JPA) and manual SQL design.

-- Create database if it does not already exist
create database if not exists co_match;

-- Select the database to use
use  co_match;

-- user table (Authentication)
-- Stores login credentials and basic account info
create table users(
    user_id int auto_increment primary key, -- Unique ID for each user
    email varchar(150) unique not null, -- User email (must be unique)
    password_hash varchar(255) not null, -- Hashed password 
    role varchar(50) default 'user', -- User role (default=user)
    created_at timestamp default current_timestamp -- Account creation time
);

-- user profile table (Profile info)
-- Stores public profile information
-- One-to-one relationship with users table
create table user_profile (
    profile_id int auto_increment primary key, -- Unique profile ID
    user_id int unique not null, -- Each user can have only one profile
    full_name varchar(100) not null, -- Required name field
    bio varchar(500), -- Short bio (max 500 chars)
    profile_photo_url varchar(255), -- Profile picture URL
    country varchar(100), -- Country/location

    -- Foreign key ensures profile belongs to a valid user
    -- on delete cascade removes profile if user is deleted
    foreign key (user_id) references users(user_id) on delete cascade
);

-- Sprint 2: Add startup stage column (FR-009)
alter table user_profile 
add column startup_stage enum('idea', 'MVP', 'funded') default 'idea'
after country;

-- skills table
-- Stores predefined list of available skills
create table skills(
	id int auto_increment primary key, -- Unique skill ID
    skill_name varchar(100) unique not null -- Skill name must be unique
);

-- Insert default predefined skills
insert into skills (skill_name) values
('Software Development'),
('Marketing'),
('Finance'),
('UI/UX Design'),
('Product Management'),
('Sales');

-- user skills (Many-to-many)
-- Links users with selected skills
create table user_skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    skill_name VARCHAR(100)
	
    -- Composite primary key prevents duplicate skill assignments
    primary key (user_id, skill_id),
    
    -- If user is deleted, their skills are removed automatically
    foreign key (user_id) references users(user_id)on delete cascade,
    
    -- If skill is deleted, related records are removed
	foreign key (skill_id) references skills(id) on delete cascade
);

-- sectors table
-- Stores predefined industry/sector categories
create table sectors (
    id int auto_increment primary key,
    sector_name varchar(100) unique not null
);

-- Insert default sectors
insert into sectors (sector_name) values
('FinTech'),
('HealthTech'),
('EdTech'),
('AI'),
('E-commerce'),
('Blockchain');

-- user sectors table (Many-to-many)
-- Links users with selected sectors
create table user_sectors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    sector_name VARCHAR(100),

    -- Composite primary key prevents duplicates
    primary key (user_id, sector_id),
    
    -- Remove sector record if user is deleted
    foreign key (user_id) references users(user_id)on delete cascade,
	
    -- Remove relationship if sector is deleted
    foreign key (sector_id) references sectors(id)on delete cascade
);

-- SWIPES TABLE
-- Stores swipe actions (LIKE or PASS)
-- Used for matching logic
create table swipes (
    id int auto_increment primary key, -- Unique swipe ID
    swiper_id int not null, -- User performing swipe
    swiped_id int not null, -- User being swiped
    swipe_type enum('LIKE','PASS') not null, -- Type of swipe action
    created_at timestamp default current_timestamp, -- Time of swipe

	-- If either user is deleted, swipe record is deleted
    foreign key (swiper_id) references users(user_id)on delete cascade,
	foreign key (swiped_id) references users(user_id)on delete cascade 
);


-- Index on email to improve login performance
create index idx_users_email on users(email);

-- Index to improve performance when fetching user skills
create index idx_user_skills_user on user_skills(user_id);

-- Index to improve sector lookup performance
create index idx_user_sectors_user on user_sectors(user_id);

-- NFR-001: Add indexes for performance (3 second load time)
-- Index for filtering by country (FR-014)
create index idx_profile_country on user_profile(country);

-- Index for filtering by startup stage (FR-009)
create index idx_profile_stage on user_profile(startup_stage);

-- Index for faster swipe lookups
create index idx_swipes_swiper on swipes(swiper_id);

-- SPRINT 3: SAFETY FEATURES (FR-021)
-- Allows users to block or report inappropriate users
create table blocked_users (
    id int auto_increment primary key,
    blocker_id int not null,
    blocked_id int not null,
    blocked_at timestamp default current_timestamp,

    unique (blocker_id, blocked_id),

    foreign key (blocker_id) references users(user_id) on delete cascade,
    foreign key (blocked_id) references users(user_id) on delete cascade
);
create table reports (
    id int auto_increment primary key,
    reporter_id int not null,
    reported_id int not null,
    reason varchar(255) not null,
    reported_at timestamp default current_timestamp,

    foreign key (reporter_id) references users(user_id) on delete cascade,
    foreign key (reported_id) references users(user_id) on delete cascade
);
create index idx_blocked_blocker on blocked_users(blocker_id);
create index idx_reports_reporter on reports(reporter_id);

-- MATCHES TABLE
-- Stores mutual matches between two users
-- Used for privacy enforcement (NFR-004)
 CREATE TABLE user_matches (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    matched_at DATETIME DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE (user1_id, user2_id),
    FOREIGN KEY (user1_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (user2_id) REFERENCES users(user_id) ON DELETE CASCADE
);
