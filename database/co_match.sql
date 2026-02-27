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

-- Index on email to improve login performance
create index idx_users_email on users(email);

-- user profile table (Profile info)
-- Stores public profile information
-- One-to-one relationship with users table
create table user_profile (
    profile_id int auto_increment primary key, -- Unique profile ID
    user_id int unique not null, -- Each user can have only one profile
    full_name varchar(100) not null, -- Required name field
    bio varchar(500), -- Short bio (max 500 chars)
    photo_url varchar(255), -- Profile picture URL
    location varchar(100), -- Country/location

    -- Foreign key ensures profile belongs to a valid user
    -- on delete cascade removes profile if user is deleted
    foreign key (user_id) references users(user_id) on delete cascade
);

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
    user_id int, -- User reference
    skill_id int, -- Skill reference
	
    -- Composite primary key prevents duplicate skill assignments
    primary key (user_id, skill_id),
    
    -- If user is deleted, their skills are removed automatically
    foreign key (user_id) references users(user_id)on delete cascade,
    
    -- If skill is deleted, related records are removed
	foreign key (skill_id) references skills(id) on delete cascade
);

-- Index to improve performance when fetching user skills
create index idx_user_skills_user on user_skills(user_id);

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
    user_id int, -- User reference
    sector_id int, -- Sector reference

    -- Composite primary key prevents duplicates
    primary key (user_id, sector_id),
    
    -- Remove sector record if user is deleted
    foreign key (user_id) references users(user_id)on delete cascade,
	
    -- Remove relationship if sector is deleted
    foreign key (sector_id) references sectors(id)on delete cascade
);

-- Index to improve sector lookup performance
create index idx_user_sectors_user on user_sectors(user_id);

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

-- MATCHES TABLE
-- Stores mutual matches between two users
-- Used for privacy enforcement (NFR-004)
create table matches (
    id int auto_increment primary key, -- Unique match ID
    user1_id int not null, -- First user
    user2_id int not null, -- Second user
    matched_at timestamp default current_timestamp, -- Match time
	
    -- Remove match if either user is deleted
    foreign key (user1_id) references users(user_id)on delete cascade,
    foreign key (user2_id) references users(user_id)on delete cascade,
    
    unique (user1_id, user2_id), -- Prevent duplicate matches
    check (user1_id <> user2_id) -- Prevent user matching with themselves
);

-- Testing data section

-- Insert a test user
insert into users (email, password_hash)
values ('mridh@gmail.com', '$2a$10$examplehashedpassword');
-- view users table
select * from users;

-- Insert profile for test user
insert into user_profile (user_id, full_name, bio, location)
values (1, 'Mridhula', 'Startup founder', 'Ireland');
-- view profile table
select * from user_profile;

-- Assign skills to test user
insert into user_skills (user_id, skill_id)
values (1, 1), (1, 2);
-- view users skills
select * from user_skills;

-- Assign sector to test user
insert into user_sectors (user_id, sector_id)
values (1, 3);
-- view user sectors
select * from user_sectors;

-- profile fetch query
-- Used to retrieve full profile with skills & sectors
select
    u.user_id,
    u.email,
    p.full_name,
    p.bio,
    p.location,
    GROUP_CONCAT(distinct s.skill_name) as skills,
    GROUP_CONCAT(distinct sec.sector_name) as sectors
from users u
left join user_profile p on u.user_id = p.user_id
left join user_skills us on u.user_id = us.user_id
left join skills s on us.skill_id = s.id
left join user_sectors usec on u.user_id = usec.user_id
left join sectors sec on usec.sector_id = sec.id
where u.user_id = 1
group by u.user_id;

-- MATCH PRIVACY CHECK QUERY
-- Used to verify mutual match (NFR-004)
-- Returns result only if users are matched
select 1
from matches
where (user1_id = 1 and user2_id = 5)or (user1_id = 5 and user2_id = 1);

-- DATABASE USER FOR BACKEND CONNECTION

-- Create backend database user if not already existing
create user if not exists 'team04_db'@'localhost' -- username & host
identified by 'Mridhula@2026'; -- password

-- Grant full privileges on co_match database
grant all privileges on co_match.*
to 'team04_db'@'localhost';

-- Reload privilege tables
flush privileges;

-- Show granted permissions0
show grants for 'team04_db'@'localhost';


 



