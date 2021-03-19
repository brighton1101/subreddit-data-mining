package reddittracker.common.fs

import scala.concurrent.{ExecutionContext, Future}

object BucketNotFoundError extends FileWriterStatus {
  def reason: String = "Could not find bucket with specified name."
}

class GcsFileWriter extends FileWriter {
  def fromString(body: String)(implicit ec: ExecutionContext): Future[FileWriterStatus] = ???
}
