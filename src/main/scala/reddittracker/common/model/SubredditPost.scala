package com.brighton1101.reddittracker.common.model

case class SubredditPost(
  selftext: String,
  title: String,
  subreddit: String,
  total_awards_received: Int,
  ups: Int,
  downs: Int,
  num_comments: Int,
  permalink: String,
  is_video: Boolean,
  name: String,
  author: String,
  author_fullname: String
)
