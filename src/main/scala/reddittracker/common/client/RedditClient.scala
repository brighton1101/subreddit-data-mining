package com.brighton1101.reddittracker.common.client

import scala.concurrent.ExecutionContext
import com.brighton1101.reddittracker.common.{HttpClient, HttpClientError, SttpHttpClient}
import com.brighton1101.reddittracker.common.model.SubredditPost
import scala.concurrent.Future

trait RedditClient {

  def getSubredditPosts(
    subreddit: String,
    after: Option[String] = None,
    before: Option[String] = None,
    limit: Option[Int] = None
  )(implicit ec: ExecutionContext): Future[Either[HttpClientError, Seq[SubredditPost]]]
}

/**
 * The below case classes are to represent the
 * responses we get from reddit's http api. these
 * get mapped onto `SubredditPost` objects in the
 * implementation
 **/

case class SubredditResponseChildData(
  approved_at_utc: Option[String],
  subreddit: String,
  selftext: String,
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
  quarantine: Boolean,
  link_flair_text_color: Option[String],
  upvote_ratio: Int,
  author_flair_background_color: Option[String],
  subreddit_type: String,
  ups: Int,
  total_awards_received: Int,
  num_comments: Int,
  permalink: String,
  is_video: Boolean,
  name: String,
  author: String,
  author_fullname: String
)

case class SubredditResponseChild(
  kind: String,
  data: SubredditResponseChildData
) {
  def toSubredditPost: SubredditPost = SubredditPost(
    data.selftext,
    data.title,
    data.subreddit,
    data.total_awards_received,
    data.ups,
    data.downs,
    data.num_comments,
    data.permalink,
    data.is_video,
    data.name,
    data.author,
    data.author_fullname
  )
}

case class SubredditResponseData(
  modhash: String,
  dist: Int,
  children: Seq[SubredditResponseChild],
  after: Option[String],
  before: Option[String]
)

case class SubredditResponse(
  kind: String,
  data: SubredditResponseData
)

class RedditClientImpl(httpClient: HttpClient) extends RedditClient {

  val afterKey = "after"
  val beforeKey = "before"
  val limitKey = "limit"
  val defaultHeaders = Map("User-Agent" -> "Reddit Tracker 0.1")

  def getSubredditPosts(
    subreddit: String,
    after: Option[String] = None,
    before: Option[String] = None,
    limit: Option[Int] = None
  )(implicit ec: ExecutionContext): Future[Either[HttpClientError, Seq[SubredditPost]]] = {
    def paramMapping_(k: String, v: Option[String]): Map[String, String] =
      v.map { value: String => Map(k -> value) }.getOrElse(Map.empty[String, String])
    def mkUrl_(subreddit: String) = f"https://www.reddit.com/r/${subreddit}.json"
    val urlParams = paramMapping_(afterKey, after) ++
      paramMapping_(beforeKey, before) ++
      paramMapping_(limitKey, limit.map(_.toString))
    httpClient
      .asyncGet[SubredditResponse](mkUrl_(subreddit), params=urlParams, headers=defaultHeaders)
      .map {
        case Right(subRes) => Right(subRes.data.children.map(_.toSubredditPost))
        case Left(e) => Left(e)
      }
  }
}

