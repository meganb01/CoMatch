-- DELIMITER
-- STEP 1: Change delimiter to // (so semicolons inside procedure don't end the command)
delimiter //
-- Drop old version so we can replace it
-- STORED PROCEDURES (SPRINT 2 + 3)
-- Handles matching, filtering, blocking, and reporting
-- GetNextProfile (with filters + block logic)
DROP PROCEDURE IF EXISTS GetNextProfile //

-- GetNextProfile
-- Returns next available profile for swiping
-- Applies filters: Country (FR-014), Sector (FR-015), Skill (FR-016)
-- Also excludes: Already swiped users, Matched users, Blocked users
CREATE PROCEDURE GetNextProfile(
    IN current_user_id INT,
    IN filter_country VARCHAR(100),
    IN filter_sector VARCHAR(100),
    IN filter_skill VARCHAR(100)
)
BEGIN
    SELECT
        u.user_id,
        p.full_name,
        p.bio,
        IFNULL(p.profile_photo_url, '') AS photo_url,
        p.country,
        p.startup_stage,
        GROUP_CONCAT(DISTINCT us.skill_name) AS skills,
        GROUP_CONCAT(DISTINCT usec.sector_name) AS sectors
    FROM users u
    JOIN user_profile p ON u.user_id = p.user_id
    LEFT JOIN user_skills us ON u.user_id = us.user_id
    LEFT JOIN user_sectors usec ON u.user_id = usec.user_id
    WHERE u.user_id != current_user_id

    AND u.user_id NOT IN (
        SELECT swiped_id FROM swipes WHERE swiper_id = current_user_id
    )

    AND u.user_id NOT IN (
        SELECT user2_id FROM user_matches WHERE user1_id = current_user_id
        UNION
        SELECT user1_id FROM user_matches WHERE user2_id = current_user_id
    )

    AND u.user_id NOT IN (
        SELECT blocked_id FROM blocked_users WHERE blocker_id = current_user_id
    )

    AND (filter_country IS NULL OR p.country = filter_country)

    AND (filter_sector IS NULL OR u.user_id IN (
        SELECT user_id FROM user_sectors
        WHERE sector_name = filter_sector
    ))

    AND (filter_skill IS NULL OR u.user_id IN (
        SELECT user_id FROM user_skills
        WHERE skill_name = filter_skill
    ))

    GROUP BY u.user_id, p.full_name, p.bio, p.profile_photo_url, p.country, p.startup_stage
    LIMIT 1;
END //
-- RecordSwipe (match logic)
-- Records swipe action and checks for mutual LIKE
-- Automatically creates a match if both users LIKE each other
DROP PROCEDURE IF EXISTS RecordSwipe //

CREATE PROCEDURE RecordSwipe(
    IN swiper INT,
    IN swiped INT,
    IN action_type VARCHAR(10)
)
BEGIN
    INSERT INTO swipes (swiper_id, swiped_id, swipe_type, created_at)
    VALUES (swiper, swiped, action_type, NOW());

    IF action_type = 'LIKE' THEN
        IF EXISTS (
            SELECT 1 FROM swipes
            WHERE swiper_id = swiped
            AND swiped_id = swiper
            AND swipe_type = 'LIKE'
        ) THEN
            IF swiper < swiped THEN
                INSERT IGNORE INTO user_matches (user1_id, user2_id)
                VALUES (swiper, swiped);
            ELSE
                INSERT IGNORE INTO user_matches (user1_id, user2_id)
                VALUES (swiped, swiper);
            END IF;

            SELECT 1 AS is_match;
        ELSE
            SELECT 0 AS is_match;
        END IF;
    ELSE
        SELECT 0 AS is_match;
    END IF;
END //

-- GetMyMatches
-- Retrieves all matches for a user with profile details
DROP PROCEDURE IF EXISTS GetMyMatches //

CREATE PROCEDURE GetMyMatches(
    IN my_user_id INT
)
BEGIN
    SELECT 
        m.id AS match_id,
        CASE 
            WHEN m.user1_id = my_user_id THEN m.user2_id 
            ELSE m.user1_id 
        END AS matched_user_id,
        p.full_name,
        IFNULL(p.profile_photo_url, '') AS photo_url,
        p.bio,
        p.country,
        p.startup_stage,
        GROUP_CONCAT(DISTINCT us.skill_name) AS skills,
        m.matched_at
    FROM user_matches m
    JOIN user_profile p 
        ON p.user_id = CASE 
            WHEN m.user1_id = my_user_id THEN m.user2_id 
            ELSE m.user1_id 
        END
    LEFT JOIN user_skills us ON p.user_id = us.user_id
    WHERE my_user_id IN (m.user1_id, m.user2_id)
    GROUP BY m.id, p.user_id;
END //

-- BlockUser (FR-021)
-- Blocks a user and removes any existing match
DROP PROCEDURE IF EXISTS BlockUser //

CREATE PROCEDURE BlockUser(
    IN blocker INT,
    IN blocked INT
)
BEGIN
    INSERT IGNORE INTO blocked_users (blocker_id, blocked_id)
    VALUES (blocker, blocked);

    DELETE FROM user_matches
    WHERE (user1_id = blocker AND user2_id = blocked)
       OR (user1_id = blocked AND user2_id = blocker);
END //


-- ReportUser (FR-021)
-- Allows users to report inappropriate behavior
DROP PROCEDURE IF EXISTS ReportUser //

CREATE PROCEDURE ReportUser(
    IN reporter INT,
    IN reported INT,
    IN report_reason VARCHAR(255)
)
BEGIN
    INSERT INTO reports (reporter_id, reported_id, reason)
    VALUES (reporter, reported, report_reason);
END //

delimiter ;