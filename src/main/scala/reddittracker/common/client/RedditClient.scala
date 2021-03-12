package com.brighton1101.reddittracker.common.client

import com.brighton1101.reddittracker.common.{HttpClient, HttpClientError, SttpHttpClient}
import com.brighton1101.reddittracker.common.model.SubredditData
import scala.concurrent.Future


trait RedditClient {
  def getSubredditData(
    subreddit: String,
    after: Option[String] = None,
    before: Option[String] = None,
    limit: Option[Int] = None
  ): Future[Either[HttpClientError, SubredditData]]
}

class RedditClientImpl(httpClient: HttpClient) {

  val defaultHeaders = Map("User-Agent" -> "Reddit Tracker 0.1")
  def mkUrl(subreddit: String) = f"https://www.reddit.com/r/${subreddit}.json"

  def getSubredditData(
    subreddit: String,
    after: Option[String] = None,
    before: Option[String] = None,
    limit: Option[Int] = None
  ): Future[Either[HttpClientError, SubredditData]] = {
    def paramMapping_(k: String, v: Option[String]): Map[String, String] =
      v.map { value: String => Map(k -> value) }.getOrElse(Map.empty[String, String])
    val urlParams: Map[String, String] =
      paramMapping_("after", after) ++ paramMapping_("before", before) ++ paramMapping_("limit", limit.map(_.toString))
    httpClient.asyncGet[SubredditData](mkUrl(subreddit), params=urlParams, headers=defaultHeaders)
  }
}

