-- ============================================================
-- DATABASE: CO_MATCH
-- DESCRIPTION:
-- A matchmaking platform database for founders to connect,
-- swipe, match, communicate, and collaborate.
--
-- FEATURES:
-- - User profiles with skills & sectors
-- - Swipe & match system
-- - Filtering (country, sector, skill)
-- - Safety features (block/report)
-- - Video call support
--
-- AUTHOR: Team 04
-- ============================================================

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
    photo_url varchar(255), -- Profile picture URL
    location varchar(100), -- Country/location

    -- Foreign key ensures profile belongs to a valid user
    -- on delete cascade removes profile if user is deleted
    foreign key (user_id) references users(user_id) on delete cascade
);

-- Sprint 2: Add startup stage column (FR-009)
alter table user_profile 
add column startup_stage enum('idea', 'MVP', 'funded') default 'idea'
after location;

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

-- Index on email to improve login performance
create index idx_users_email on users(email);

-- Index to improve performance when fetching user skills
create index idx_user_skills_user on user_skills(user_id);

-- Index to improve sector lookup performance
create index idx_user_sectors_user on user_sectors(user_id);

-- NFR-001: Add indexes for performance (3 second load time)
-- Index for filtering by country (FR-014)
create index idx_profile_location on user_profile(location);

-- Index for filtering by startup stage (FR-009)
create index idx_profile_stage on user_profile(startup_stage);

-- Index for faster swipe lookups
create index idx_swipes_swiper on swipes(swiper_id);

-- Index for matches performance
create index idx_matches_user1 on matches(user1_id);
create index idx_matches_user2 on matches(user2_id);

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

-- Testing data section 

-- Insert a test user
insert into users (email, password_hash)
values ('mridh@gmail.com', '$2a$10$examplehashedpassword');

-- view users table
select * from users;
insert into user_profile (user_id, full_name, bio, location, startup_stage)
values (1, 'Mridhula', 'Startup founder', 'Ireland', 'idea');

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
-- Uses GROUP_CONCAT for aggregation
select
    u.user_id,
    u.email,
    p.full_name,
    p.bio,
    p.location,
    p.startup_stage,
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

-- Testing for Sprint 2
-- Insert 5 new users
insert into users (email, password_hash) values
('sarah@gmail.com', '$2a$10$hash1'),
('mike@gmail.com', '$2a$10$hash2'),
('alex@gmail.com', '$2a$10$hash3'),
('emma@gmail.com', '$2a$10$hash4'),
('david@gmail.com', '$2a$10$hash5');

-- Insert profiles with startup_stage
insert into user_profile (user_id, full_name, bio, location, startup_stage) values
(2, 'Sarah Chen', 'Full-stack developer with fintech experience', 'Ireland', 'MVP'),
(3, 'Mike Ross', 'Business graduate with marketing expertise', 'Ireland', 'idea'),
(4, 'Alex Kumar', 'Backend engineer specializing in blockchain', 'United Kingdom', 'funded'),
(5, 'Emma Wilson', 'UI/UX designer with product experience', 'Ireland', 'MVP'),
(6, 'David Brown', 'Sales professional with e-commerce network', 'United Kingdom', 'idea');

-- Assign skills to new users
insert into user_skills (user_id, skill_id) values
(2, 1), (2, 4), -- Sarah: Software Dev, UI/UX
(3, 2), (3, 5), (3, 6), -- Mike: Marketing, Product, Sales
(4, 1), (4, 5), -- Alex: Software Dev, Product
(5, 4), (5, 5), -- Emma: UI/UX, Product
(6, 2), (6, 6); -- David: Marketing, Sales

-- Assign sectors to new users
insert into user_sectors (user_id, sector_id) values
(2, 1), -- Sarah: FinTech
(3, 2), -- Mike: HealthTech
(4, 6), -- Alex: Blockchain
(5, 3), -- Emma: EdTech
(6, 5); -- David: E-commerce

-- Create swipes for testing
-- Mridhula (1) likes Sarah (2), Sarah likes back = MATCH
insert into swipes (swiper_id, swiped_id, swipe_type) values (1, 2, 'LIKE');
insert into swipes (swiper_id, swiped_id, swipe_type) values (2, 1, 'LIKE');

-- Mridhula (1) passes on Mike (3)
insert into swipes (swiper_id, swiped_id, swipe_type) values (1, 3, 'PASS');

-- Mridhula (1) likes Alex (4), Alex likes back = MATCH
insert into swipes (swiper_id, swiped_id, swipe_type) values (1, 4, 'LIKE');
insert into swipes (swiper_id, swiped_id, swipe_type) values (4, 1, 'LIKE');

-- Create the matches
insert into matches (user1_id, user2_id) values (1, 2); -- Mridhula & Sarah
insert into matches (user1_id, user2_id) values (1, 4); -- Mridhula & Alex


-- DELIMITER
-- STEP 1: Change delimiter to // (so semicolons inside procedure don't end the command)
delimiter //
-- Drop old version so we can replace it
-- STORED PROCEDURES (SPRINT 2 + 3)
-- Handles matching, filtering, blocking, and reporting
-- GetNextProfile (with filters + block logic)
drop procedure if exists GetNextProfile //

-- GetNextProfile
-- Returns next available profile for swiping
-- Applies filters: Country (FR-014), Sector (FR-015), Skill (FR-016)
-- Also excludes: Already swiped users, Matched users, Blocked users
create procedure GetNextProfile(
    in current_user_id int,
    in filter_country varchar(100),
    in filter_sector varchar(100),
    in filter_skill varchar(100)
)
begin
    select
        u.user_id,
        p.full_name,
        p.bio,
        ifnull(p.photo_url, '') as photo_url,
        p.location,
        p.startup_stage,
        group_concat(distinct s.skill_name) as skills,
        group_concat(distinct sec.sector_name) as sectors
    from users u
    join user_profile p on u.user_id = p.user_id
    left join user_skills us on u.user_id = us.user_id
    left join skills s on us.skill_id = s.id
    left join user_sectors usec on u.user_id = usec.user_id
    left join sectors sec on usec.sector_id = sec.id
    where u.user_id != current_user_id

    and u.user_id not in (
        select swiped_id from swipes where swiper_id = current_user_id
    )

    and u.user_id not in (
        select user2_id from matches where user1_id = current_user_id
        union
        select user1_id from matches where user2_id = current_user_id
    )

    and u.user_id not in (
        select blocked_id from blocked_users where blocker_id = current_user_id
    )

    and (filter_country is null or p.location = filter_country)

    and (filter_sector is null or u.user_id in (
        select us2.user_id from user_sectors us2
        join sectors sec2 on us2.sector_id = sec2.id
        where sec2.sector_name = filter_sector
    ))

    and (filter_skill is null or u.user_id in (
        select usk2.user_id from user_skills usk2
        join skills sk2 on usk2.skill_id = sk2.id
        where sk2.skill_name = filter_skill
    ))

    group by u.user_id, p.full_name, p.bio, p.photo_url, p.location, p.startup_stage
    limit 1;
end //

-- RecordSwipe (match logic)
-- Records swipe action and checks for mutual LIKE
-- Automatically creates a match if both users LIKE each other
drop procedure if exists RecordSwipe //

create procedure RecordSwipe(
    in swiper int,
    in swiped int,
    in action_type varchar(10)
)
begin
    insert into swipes (swiper_id, swiped_id, swipe_type, created_at)
    values (swiper, swiped, action_type, now());

    if action_type = 'LIKE' then
        if exists (
            select 1 from swipes 
            where swiper_id = swiped 
            and swiped_id = swiper 
            and swipe_type = 'LIKE'
        ) then

            if swiper < swiped then
                insert ignore into matches (user1_id, user2_id)
                values (swiper, swiped);
            else
                insert ignore into matches (user1_id, user2_id)
                values (swiped, swiper);
            end if;

            select 1 as is_match;
        else
            select 0 as is_match;
        end if;
    else
        select 0 as is_match;
    end if;
end //

-- GetMyMatches
-- Retrieves all matches for a user with profile details
drop procedure if exists GetMyMatches //

create procedure GetMyMatches(
    in my_user_id int
)
begin
    select 
        m.id as match_id,
        case 
            when m.user1_id = my_user_id then m.user2_id 
            else m.user1_id 
        end as matched_user_id,
        p.full_name,
        p.photo_url,
        p.bio,
        p.location,
        p.startup_stage,
        group_concat(distinct s.skill_name) as skills,
        m.matched_at
    from matches m
    join user_profile p 
        on p.user_id = case 
            when m.user1_id = my_user_id then m.user2_id 
            else m.user1_id 
        end
    left join user_skills us on p.user_id = us.user_id
    left join skills s on us.skill_id = s.id
    where my_user_id in (m.user1_id, m.user2_id)
    group by m.id, p.user_id;
end //

-- BlockUser (FR-021)
-- Blocks a user and removes any existing match
drop procedure if exists BlockUser //

create procedure BlockUser(
    in blocker int,
    in blocked int
)
begin
    insert ignore into blocked_users (blocker_id, blocked_id)
    values (blocker, blocked);

    delete from matches
    where (user1_id = blocker and user2_id = blocked)
       or (user1_id = blocked and user2_id = blocker);
end //

-- ReportUser (FR-021)
-- Allows users to report inappropriate behavior
drop procedure if exists ReportUser //

create procedure ReportUser(
    in reporter int,
    in reported int,
    in report_reason varchar(255)
)
begin
    insert into reports (reporter_id, reported_id, reason)
    values (reporter, reported, report_reason);
end //

delimiter ;

-- TEST QUERIES
-- Used to validate stored procedures
call GetNextProfile(1, null, null, null);  -- Get profile to swipe
call RecordSwipe(1, 5, 'LIKE'); -- Returns 1 if match, 0 if not
call RecordSwipe(5, 1, 'LIKE'); -- Returns 1 if match, 0 if not
call GetMyMatches(1);           -- List all matches

-- Check who is left to swipe
select user_id, full_name from user_profile 
where user_id != 1
and user_id not in (select swiped_id from swipes where swiper_id = 1)
and user_id not in (
    select user2_id from matches where user1_id = 1
    union
    select user1_id from matches where user2_id = 1
);

-- DATABASE USER FOR BACKEND CONNECTION
-- Provides backend access to the database

-- Create backend database user if not already existing
create user if not exists 'team04_db'@'localhost' -- username & host
identified by 'Mridhula@2026'; -- password

-- Grant full privileges on co_match database
grant all privileges on co_match.*
to 'team04_db'@'localhost';

-- Reload privilege tables
flush privileges;

-- Show granted permissions
show grants for 'team04_db'@'localhost';


 



