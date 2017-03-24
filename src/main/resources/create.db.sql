CREATE TABLE global_settings (
	font_size SMALLINT,
	text_wrap BOOL,
	json_pretty BOOL,
	json_regex VARCHAR(50),
	current_profile_id SMALLINT,
	auto_scroll BOOL,
	bar_show BOOL,
	filter_show BOOL
);

CREATE TABLE profiles (
	id SMALLINT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(50),
	url CLOB
);

CREATE TABLE headers (
	id SMALLINT PRIMARY KEY AUTO_INCREMENT,
	profile_id SMALLINT,
	name VARCHAR,
	value VARCHAR
);

CREATE TABLE messages_rx (
	id SMALLINT PRIMARY KEY AUTO_INCREMENT,
	profile_id SMALLINT,
	type VARCHAR,
	time VARCHAR,
	value CLOB
);

CREATE TABLE messages_tx (
	id SMALLINT PRIMARY KEY AUTO_INCREMENT,
	profile_id SMALLINT,
	auto_send BOOL,
	value CLOB
);

CREATE TABLE filters (
	id SMALLINT PRIMARY KEY AUTO_INCREMENT,
	profile_id SMALLINT,
	value CLOB
);
