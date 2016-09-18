CREATE TABLE global_settings (
	font_size integer,
	text_wrap integer,
	json_pretty integer,
	json_regex text,
	current_profile_id integer,
	auto_scroll integer,
	bar_show integer,
	filter_show integer
);

CREATE TABLE profiles (
	id integer PRIMARY KEY AUTOINCREMENT,
	name text,
	url text
);

CREATE TABLE headers (
	id integer PRIMARY KEY AUTOINCREMENT,
	profile_id integer,
	name text,
	value text
);

CREATE TABLE messages_rx (
	id integer PRIMARY KEY AUTOINCREMENT,
	profile_id integer,
	type text,
	time text,
	value text
);

CREATE TABLE messages_tx (
	id integer PRIMARY KEY AUTOINCREMENT,
	profile_id integer,
	auto_send integer,
	value text
);

CREATE TABLE filters (
	id integer PRIMARY KEY AUTOINCREMENT,
	profile_id integer,
	value text
);
