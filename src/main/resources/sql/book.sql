-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id`        INTEGER(20)  NOT NULL UNIQUE,
  `loginname`      VARCHAR(64) NOT NULL UNIQUE,
  `password`      VARCHAR(128) NOT NULL,
  `nickname`      VARCHAR(128) NOT NULL UNIQUE,
  `face_url`      TEXT           DEFAULT NULL,
  `create_time`     INTEGER(13) DEFAULT '0',
  `status`     INTEGER(2) DEFAULT '1',
  PRIMARY KEY (`id`)
);

-- ----------------------------
-- Table structure for book
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
  `id`        INTEGER(20)  NOT NULL UNIQUE,
  `author`      VARCHAR(64) NOT NULL,
  `face_url`    TEXT DEFAULT 'http://book.puyixiaowo.win/static/images/face_pic.png',
  `book_id_third`      VARCHAR(128) NOT NULL,
  `name`      VARCHAR(128) NOT NULL,
  `url`      VARCHAR(512)           DEFAULT NULL,
  `create_time`     INTEGER(13) DEFAULT '0',
  `is_over`     INTEGER(2) DEFAULT '0',
  PRIMARY KEY (`id`)
);

-- ----------------------------
-- Table structure for bookshelf
-- ----------------------------
DROP TABLE IF EXISTS `bookshelf`;
CREATE TABLE `bookshelf` (
  `id`        INTEGER(20)  NOT NULL UNIQUE,
  `user_id`      INTEGER(20) NOT NULL,
  `book_id`      INTEGER(128) NOT NULL,
  `create_time`     INTEGER(13) DEFAULT '0',

  PRIMARY KEY (`id`)
);


-- ----------------------------
-- Table structure for book_read
-- ----------------------------
DROP TABLE IF EXISTS `book_read`;
CREATE TABLE `book_read` (
  `id`        INTEGER(20)  NOT NULL UNIQUE,
  `user_id`      INTEGER(20) NOT NULL,
  `book_id`      INTEGER(20) NOT NULL,
  `source`    VARCHAR(128),
  `last_reading_chapter`          VARCHAR(128) DEFAULT NULL,
  `last_reading_chapter_num`        INTEGER(6) DEFAULT '1',
  PRIMARY KEY (`id`)
);



-- ----------------------------
-- Table structure for book_read_setting
-- ----------------------------
DROP TABLE IF EXISTS `book_read_setting`;
CREATE TABLE `book_read_setting` (
  `id`        INTEGER(20)  NOT NULL UNIQUE,
  `user_id`      INTEGER(20) NOT NULL UNIQUE,
  `create_time`     INTEGER(13) DEFAULT '0',
  `page_method`        VARCHAR(32) DEFAULT '⇄',
  `color`      VARCHAR(64) DEFAULT '#131313',
  `bg_color`      VARCHAR(64) DEFAULT '#6d816f',
  `font_size`        INTEGER(6) DEFAULT '23',
  `line_height`        INTEGER(6) DEFAULT '28',
  `sort`        INTEGER(4) DEFAULT '0',
  `channel`     VARCHAR(32) DEFAULT 'boy',

  PRIMARY KEY (`id`)
);

-- ----------------------------
-- Table structure for pick_rules
-- ----------------------------
DROP TABLE IF EXISTS `pick_rules`;
CREATE TABLE `pick_rules` (
  `id`        INTEGER(20)  NOT NULL UNIQUE,
  `search_encoding`      TEXT,
  `search_link`     TEXT,
  `search_items`        TEXT,
  `search_item_book_id_third`        TEXT,
  `search_item_title`        TEXT,
  `search_item_author`        TEXT,
  `search_item_category`        TEXT,
  `search_item_update_date`        TEXT,
  `search_item_update_chapter`        TEXT,
  `search_item_face_url`        TEXT,
  `book_detail_link`        TEXT,
  `book_detail_title`        TEXT,
  `book_detail_author`        TEXT,
  `book_detail_update_date`        TEXT,
  `book_detail_update_chapter`        TEXT,
  `book_detail_category`        TEXT,
  `book_detail_description`        TEXT,
  `book_detail_face_url`        TEXT,
  `chapter_list_items`        TEXT,
  `chapter_list_title`        TEXT,
  `chapter_list_link`        TEXT,
  `chapter_list_detail_link`        TEXT,
  `chapter_detail_title`        TEXT,
  `chapter_detail_content`        TEXT,
  PRIMARY KEY (`id`)
);