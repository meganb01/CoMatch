-- Add frontend skills that are missing from the default schema
-- Run this after co_match_schema.sql if profile save fails with "Unknown skill"
-- Usage: mysql -u team04_db -p co_match < add_skills_migration.sql

USE co_match;

INSERT IGNORE INTO skills (skill_name) VALUES
('Development'),
('Design'),
('HR'),
('Customer Support'),
('Legal'),
('Operations'),
('Data Science'),
('QA'),
('Marketing Research'),
('Business Development'),
('UI/UX'),
('Content Writing'),
('SEO'),
('Social Media'),
('Analytics'),
('Blockchain');
