package reddittracker.common.fs

import scala.concurrent.{ExecutionContext, Future}
import java.nio.charset.StandardCharsets.UTF_8

import com.google.cloud.storage.{BlobId, BlobInfo, Storage, StorageException, StorageOptions}

object BucketUploadSuccess extends FileWriterStatus {
  def reason = "Uploaded to bucket successfully"
}

object BucketUploadFailure extends FileWriterStatus {
  def reason: String = "Could not write content to specified bucket"
}

case class GcsFileWriterConfig(
  bucketName: String,
  gcsPath: String,
                              )

class GcsFileWriter(config: GcsFileWriterConfig, storage: Storage = StorageOptions.getDefaultInstance.getService) extends FileWriter {
  def fromString(body: String)(implicit ec: ExecutionContext): Future[FileWriterStatus] = {
    val blobId = BlobId.of(config.bucketName, config.gcsPath)
    val blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build
    Future {
      try {
        storage.create(blobInfo, body.getBytes(UTF_8))
        BucketUploadSuccess
      } catch {
        case _: StorageException => BucketUploadFailure
      }
    }
  }
}

case class GcsUri(
                 originalUri: String,
                 bucketName: String,
                 objectName: String
                 )

object GcsFileWriter {

  val gcsUriPrefix = "gs://"

  def shouldTargetGcs(uri: String): Boolean = {
    if (uri.startsWith(gcsUriPrefix)) true
    else false
  }

  def parseGcsUri(uri: String): Option[GcsUri] = {
    def extractParts(u: String) = u.stripPrefix(gcsUriPrefix).split("/", 2)
    if (!shouldTargetGcs(uri)) None
    else if (extractParts(uri).size < 2) None
    else {
      val parts = extractParts(uri)
      Some(GcsUri(uri, parts(0), parts(1)))
    }
  }
}