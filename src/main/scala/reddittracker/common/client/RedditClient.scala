package com.brighton1101.reddittracker.common.client

import com.brighton1101.reddittracker.common.{HttpClient, HttpClientError, SttpHttpClient}
import com.brighton1101.reddittracker.common.model.SubredditResponse
import scala.concurrent.Future


trait RedditClient {
  def getSubredditData(
    subreddit: String,
    after: Option[String] = None,
    before: Option[String] = None,
    limit: Option[Int] = None
  ): Future[Either[HttpClientError, SubredditResponse]]
}

class RedditClientImpl(httpClient: HttpClient) extends RedditClient {

  val afterKey = "after"
  val beforeKey = "before"
  val limitKey = "limit"
  val defaultHeaders = Map("User-Agent" -> "Reddit Tracker 0.1")

  def getSubredditData(
    subreddit: String,
    after: Option[String] = None,
    before: Option[String] = None,
    limit: Option[Int] = None
  ): Future[Either[HttpClientError, SubredditResponse]] = {
    def paramMapping_(k: String, v: Option[String]): Map[String, String] =
      v.map { value: String => Map(k -> value) }.getOrElse(Map.empty[String, String])
    def mkUrl_(subreddit: String) = f"https://www.reddit.com/r/${subreddit}.json"
    val urlParams = paramMapping_(afterKey, after) ++
      paramMapping_(beforeKey, before) ++
      paramMapping_(limitKey, limit.map(_.toString))
    httpClient.asyncGet[SubredditResponse](mkUrl_(subreddit), params=urlParams, headers=defaultHeaders)
  }
}

