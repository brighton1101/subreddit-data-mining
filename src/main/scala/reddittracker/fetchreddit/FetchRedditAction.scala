package com.brighton1101.reddittracker.fetchreddit

import com.brighton1101.reddittracker.common.client.{RedditClient, RedditClientImpl}
import com.brighton1101.reddittracker.common.{CliAction, Json, SttpHttpClient}
import reddittracker.common.fs.{FileWriter, GcsFileWriter, GcsFileWriterConfig, LocalFileWriter}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

case class FetchRedditConfig(
  resultsDestination: String,
  subreddit: String,
  after: Option[String],
  before: Option[String],
  limit: Option[Int]
)

sealed trait FetchRedditResult
object FetchRedditSuccess extends FetchRedditResult
object FetchRedditFailure extends FetchRedditResult


class FetchRedditAction(redditClient: RedditClient = new RedditClientImpl(SttpHttpClient.getClient)) extends CliAction {

  def run(args: Seq[String]): Unit = {
    lazy val fw = FetchRedditAction.parseConfig(args)
      .flatMap(config => FetchRedditAction.getFileWriter(config.resultsDestination))
    val deps = for {
      config <- FetchRedditAction.parseConfig(args)
      fw <- FetchRedditAction.getFileWriter(config.resultsDestination)
    } yield (config, fw)
    deps.map { case (c, fw) => requestAndWrite(c, fw) }
      .getOrElse(Future { FetchRedditFailure })
      .onComplete {
        case Success(FetchRedditSuccess) => java.lang.System.exit(0)
        case _ => java.lang.System.exit(1)
      }
  }

  def requestAndWrite(c: FetchRedditConfig, fw: FileWriter): Future[FetchRedditResult] = {
    redditClient.getSubredditPosts(c.subreddit, c.after, c.before, c.limit)
      .map(_.map(Json.toDelimJson(_)))
      .flatMap {
        case Left(err) => Future { FetchRedditFailure }
        case Right(v) => fw.fromString(v).map(_ => FetchRedditSuccess)
      }
  }
}

object FetchRedditAction {
  def getFileWriter(resultsDestination: String): Option[FileWriter] = {
    if (GcsFileWriter.shouldTargetGcs(resultsDestination)) {
      GcsFileWriter
        .parseGcsUri(resultsDestination)
        .map(uri => GcsFileWriterConfig(uri.bucketName, uri.objectName))
        .map(new GcsFileWriter(_))
    } else {
      Some(new LocalFileWriter(resultsDestination))
    }
  }

  def parseConfig(args: Seq[String]): Option[FetchRedditConfig] = {
    if (args.size < 1) None
    else {
      try {
        val fileSrc = scala.io.Source.fromFile(args(0))
        val config = Json.fromJson[FetchRedditConfig](fileSrc.mkString)
        fileSrc.close
        Some(config)
      } catch {
        case _: Throwable => None
      }
    }
  }
}
