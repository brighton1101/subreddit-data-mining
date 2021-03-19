package reddittracker.common.fs

import scala.concurrent.{ExecutionContext, Future}

trait FileWriterStatus {
  def reason: String
}

trait FileWriter {
  def fromString(body: String)(implicit ec: ExecutionContext): Future[FileWriterStatus]
}
