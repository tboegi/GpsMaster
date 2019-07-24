CREATE TABLE dat_gps
(
id 		integer primary key,
color 		integer,
name		text,
start_dt	integer,
end_dt		integer,
distance	integer NOT NULL DEFAULT 0,
duration	integer NOT NULL DEFAULT 0,
min_lat		real NOT NULL DEFAULT 0,
max_lat		real NOT NULL DEFAULT 0,
min_lon		real NOT NULL DEFAULT 0,
max_lon		real NOT NULL DEFAULT 0,
activity		text,
loader_class	text,
fileformat		text,
prog_version	text,
data		BLOB NOT NULL,
source_urn	text,
user_id		integer NOT NULL default 0,
compressed	integer NOT NULL default 0,
entry_dt		integer,
checksum	text
)
