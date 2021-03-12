package com.brighton1101.reddittracker

import com.brighton1101.reddittracker.common.client.RedditClient

import java.util.Date


case class SubredditPost(
  
)

trait DownloadingClient {
  def subredditData(
    subredditName: String,
    after: Option[Date] = None,
    before: Option[Date] = None
  ): Future[Option[Seq[SubredditPost]]]
}


class DownloadingClientImpl(redditClient: RedditClient) {
  def subredditData(
    subredditName: String,
    after: Option[Date] = None,
    before: Option[Date] = None
  ): Future[Option[Seq[SubredditPost]]] = {
  
  }
}
