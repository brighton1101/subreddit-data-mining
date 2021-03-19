package reddittracker.common.fs

import java.io.{File, FileNotFoundException, PrintWriter}

import scala.concurrent.{ExecutionContext, Future}

object LocalFileWriteSuccess extends FileWriterStatus {
  def reason = "Success"
}

object LocalFileNotFoundError extends FileWriterStatus {
  def reason = "Specified file could not be written to. Please ensure valid path provided"
}

object LocalFilePermissionsError extends FileWriterStatus {
  def reason = "Specified file could not be written to. Please ensure valid permissions for file writing are present."
}


class LocalFileWriter(val destAbsPath: String) extends FileWriter {
  def fromString(body: String)(implicit ec: ExecutionContext): Future[FileWriterStatus] = {
    Future {
      try {
        val writer = new PrintWriter(new File(destAbsPath))
        writer.write(body)
        writer.close()
        LocalFileWriteSuccess
      } catch {
        case _: FileNotFoundException => LocalFileNotFoundError
        case _: SecurityException => LocalFilePermissionsError
      }
    }
  }
}
