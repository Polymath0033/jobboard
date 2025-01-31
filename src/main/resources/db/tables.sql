-- Users Table
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role user_role NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- Job Seekers Table
CREATE TABLE job_seekers (
                             id SERIAL PRIMARY KEY,
                             user_id INT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                             first_name VARCHAR(100) NOT NULL,
                             last_name VARCHAR(100) NOT NULL,
                             resume_url VARCHAR(255),
                             skills TEXT,
                             experience TEXT
);
-- Employers Table
CREATE TABLE employers (
                           id SERIAL PRIMARY KEY,
                           user_id INT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                           company_name VARCHAR(255) NOT NULL,
                           company_description TEXT,
                           logo_url VARCHAR(255),
                           website VARCHAR(255)
);
-- Jobs Table
CREATE TABLE jobs (
                      id SERIAL PRIMARY KEY,
                      employer_id INT NOT NULL REFERENCES employers(id) ON DELETE CASCADE,
                      title VARCHAR(255) NOT NULL,
                      description TEXT NOT NULL,
                      location VARCHAR(255),
                      salary NUMERIC(10, 2),
                      category VARCHAR(100),
                      posted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      expires_at TIMESTAMP,
                      status job_status DEFAULT 'ACTIVE'
);

-- Applications Table
CREATE TABLE applications (
                              id SERIAL PRIMARY KEY,
                              job_id INT NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
                              job_seeker_id INT NOT NULL REFERENCES job_seekers(id) ON DELETE CASCADE,
                              resume_url VARCHAR(255) NOT NULL,
                              cover_letter TEXT,
                              applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              status application_status DEFAULT 'PENDING'
);
-- Saved Jobs Table
CREATE TABLE saved_jobs (
                            id SERIAL PRIMARY KEY,
                            job_seeker_id INT NOT NULL REFERENCES job_seekers(id) ON DELETE CASCADE,
                            job_id INT NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
                            saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Job Alerts Table
CREATE TABLE job_alerts (
                            id SERIAL PRIMARY KEY,
                            job_seeker_id INT NOT NULL REFERENCES job_seekers(id) ON DELETE CASCADE,
                            search_query TEXT NOT NULL,
                            frequency alert_frequency NOT NULL
);