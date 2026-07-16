CREATE DATABASE IF NOT EXISTS tutorflow_auth;
CREATE DATABASE IF NOT EXISTS tutorflow_tutor;
CREATE DATABASE IF NOT EXISTS tutorflow_execution;
CREATE DATABASE IF NOT EXISTS tutorflow_dryrun;
CREATE DATABASE IF NOT EXISTS tutorflow_ai;
CREATE DATABASE IF NOT EXISTS tutorflow_parent;

GRANT ALL PRIVILEGES ON tutorflow_auth.* TO 'tutorflow'@'%';
GRANT ALL PRIVILEGES ON tutorflow_tutor.* TO 'tutorflow'@'%';
GRANT ALL PRIVILEGES ON tutorflow_execution.* TO 'tutorflow'@'%';
GRANT ALL PRIVILEGES ON tutorflow_dryrun.* TO 'tutorflow'@'%';
GRANT ALL PRIVILEGES ON tutorflow_ai.* TO 'tutorflow'@'%';
GRANT ALL PRIVILEGES ON tutorflow_parent.* TO 'tutorflow'@'%';