-- Users Table
create table users(id serial primary key, email varchar(255) not null unique, password varchar(255) not null,role varchar(50) not null check(role in ( 'JOB_SEEKER','EMPLOYER','ADMIN' )), created_at timestamp default current_timestamp,updated_at timestamp default current_timestamp);
-- Job Seekers Table
create table job_seekers(id serial primary key,user_id int not null unique references users(id) on delete cascade, first_name varchar(100) not null, last_name varchar(100) not null, resume_url varchar(255), skills text,experiences text);
-- Employers Table
create table employers(id serial primary key, user_id int not null unique references users(id) on delete cascade, company_name varchar(255) not null, company_description text,logo_url varchar(255), website_url varchar(255));
-- Jobs Table
create table jobs(id serial primary key,employer_id int not null references employers(id) on delete  cascade,title varchar(255) not null, description text not null, location varchar(255), category varchar(255), salary numeric(10,2),posted_at timestamp default current_timestamp,expires_at timestamp, status varchar(50) not null check ( status in ('ACTIVE','FILLED','EXPIRED') ));

-- Applications Table
create table applications(id serial primary key,job_id int not null references jobs(id) on delete cascade,job_seeker_id int not null references job_seekers(id) on delete cascade,resume_url varchar(255) not null,cover_letter text,applied_at timestamp default current_timestamp,status varchar(50) not null default 'PENDING' check ( status in ('PENDING','REVIEWED','REJECTED','ACCEPTED') ),constraint unique_job_application unique (job_id,job_seeker_id));
);
-- Saved Jobs Table
create table saved_jobs(id serial primary key, job_id int not null unique references jobs(id) on delete cascade,job_seeker_id int not null unique references job_seekers(id) on delete cascade ,saved_at timestamp default current_timestamp, constraint unique_saved_jobs unique (job_id,job_seeker_id));

-- Job Alerts Table
create table job_alerts(id serial primary key,job_seeker_id int not null unique references job_seekers(id) on delete cascade, searched_query text not null, frequency varchar(100) not null default 'DAILY' check ( frequency in ('DAILY','WEEKLY') ));

-- Tokens Table
create table tokens(id uuid primary key default gen_random_uuid(),refresh_token varchar(255) not null unique, user_id int not null unique references users(id) on delete cascade, issued_at timestamp not null, expired_at timestamp not null, revoked boolean not null default false);