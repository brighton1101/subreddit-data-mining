package com.brighton1101.reddittracker.common.client

import scala.io
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.scalatest.FlatSpec

import com.brighton1101.reddittracker.common.model.SubredditPost
import com.brighton1101.reddittracker.common.{HttpClient, HttpClientError, Json}

class RedditHttpClientMock(
  expectedUri: String,
  expectedHeaders: Map[String, String] = Map(),
  expectedParams: Map[String, String] = Map()
) extends HttpClient {
  def asyncGet[T](
    url: String,
    params: Map[String, String] = Map(),
    headers: Map[String, String] = Map()
  )(implicit mt: Manifest[T]): Future[Either[HttpClientError, T]] = {
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
    assert(url == expectedUri)
    assert(params == expectedParams)
    assert(headers == expectedHeaders)
    Future {
      Right(Json.fromJson[T](io.Source.fromResource("test_reddit.json").mkString))
    }
  }
}

class RedditClientSpec extends FlatSpec {
  behavior of "Reddit Client"

  "Reddit client" should "process valid requests" in {
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
    val httpClientMock = new RedditHttpClientMock(
      "https://www.reddit.com/r/scala.json",
      expectedHeaders = Map("User-Agent" -> "Reddit Tracker 0.1"),
      expectedParams = Map("after" -> "helloworld")
    )
    val redditClient = new RedditClientImpl(httpClientMock)
    val res = redditClient
      .getSubredditData("scala", after=Some("helloworld"))
      .map { res =>
        assert(res.right.get(0).subreddit == "scala") 
        res
      }
    Await.result(res, Duration.Inf)
  }
}
