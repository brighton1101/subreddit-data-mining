package com.brighton1101.reddittracker.fetchreddit

import java.io.{File, PrintWriter}

import com.brighton1101.reddittracker.common.client.{RedditClient, RedditClientImpl}
import com.brighton1101.reddittracker.common.{CliAction, HttpClient, Json, SttpHttpClient}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

case class FetchRedditConfig(
  resultsDestination: String,
  subreddit: String,
  after: Option[String],
  before: Option[String],
  limit: Option[Int]
)


class FetchRedditAction extends CliAction {

  def run(args: Seq[String]): Unit = {
    lazy val httpClient: HttpClient = SttpHttpClient.getClient
    lazy val redditClient: RedditClient = new RedditClientImpl(httpClient)
    if (args.size < 1) return
    val fileSrc = scala.io.Source.fromFile(args(0))
    val config = Json.fromJson[FetchRedditConfig](fileSrc.mkString)
    fileSrc.close
    val res = redditClient.getSubredditPosts(config.subreddit, config.after, config.before, config.limit)
      .map(_.map { posts =>
        /** Terrible code here but wanted to do it quickly */
        val writer = new PrintWriter(new File(config.resultsDestination))
        writer.write(Json.toDelimJson(posts))
        writer.close()
        ()
      })
    res onComplete {
      case Success(Right(_)) => {
        println("Success")
        java.lang.System.exit(0)
      }
      case _ => {
        println("Failure")
        java.lang.System.exit(1)
      }
    }
  }
}
