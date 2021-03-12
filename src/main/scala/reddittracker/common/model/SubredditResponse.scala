package com.brighton1101.reddittracker.common.model

/**
 * This is a work in progress representation of the
 * subreddit listings response as case classes...
 * there are many properties and i got lazy, to
 * be continued
 **/

case class SubredditChildData(
  approved_at_utc: Option[String],
  subreddit: String,
  selftext: String,
  author_fullname: String,
  mod_reason_title: Option[String],
  gilded: Int,
  clicked: Boolean,
  title: String,
  link_flair_richtext: Seq[String],
  subreddit_name_prefixed: String,
  hidden: Boolean,
  pwls: Int,
  link_flair_css_class: Option[String],
  downs: Int,
  top_awarded_type: Option[String],
  hide_score: Boolean,
  name: String,
  quarantine: Boolean,
  link_flair_text_color: Option[String],
  upvote_ratio: Int,
  author_flair_background_color: Option[String],
  subreddit_type: String,
  ups: Int,
  total_awards_received: Int
)

case class SubredditChild(
  kind: String,
  data: SubredditChildData
)

case class SubredditData(
  modhash: String,
  dist: Int,
  children: Seq[SubredditChild],
  after: Option[String],
  before: Option[String]
)

case class SubredditResponse(
  kind: String,
  data: SubredditData
)
