CREATE TABLE global_settings (
	font_size integer,
	text_wrap integer,
	json_pretty integer,
	json_regex text,
	current_profile_id integer
);

CREATE TABLE profiles (
	id integer PRIMARY KEY AUTOINCREMENT,
	name text,
	url text,
	auto_scroll integer,
	bar_show integer,
	filter_show integer,
	filter_on integer
);

CREATE TABLE headers (
	id integer PRIMARY KEY AUTOINCREMENT,
	profile_id integer,
	name text,
	value text
);

CREATE TABLE msg_received (
	id integer PRIMARY KEY AUTOINCREMENT,
	profile_id integer,
	type text,
	time text,
	value text
);

CREATE TABLE msg_transferred (
	id integer PRIMARY KEY AUTOINCREMENT,
	profile_id integer,
	value text
);

CREATE TABLE filter (
	id integer PRIMARY KEY AUTOINCREMENT,
	profile_id integer,
	value text
);
