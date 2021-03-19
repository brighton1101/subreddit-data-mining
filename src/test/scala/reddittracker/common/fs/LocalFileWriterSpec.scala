package reddittracker.common.fs

import java.io.File

import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.FlatSpec

class LocalFileWriterSpec extends FlatSpec {
  behavior of "Local file writer: writing files to local system"

  "LocalFileWriter" should "write valid file with valid params" in {
    val currDir: String = System.getProperty("user.dir")
    val path: String = f"$currDir/test.txt"
    val fw = new LocalFileWriter(path)
    val expectedBody = "hello world"
    val res = fw.fromString(expectedBody)
    res.map {
      case LocalFileWriteSuccess => new File(path).delete
      case _ => assert(true == false)
    }
  }

  "LocalFileWriter" should "not be able to write to invalid file" in {
    val path = "/ldjalkjdfkl/does/lsdjaflk/not/dlsakfaldflklkd/exist/___lsdakfl"
    val fw = new LocalFileWriter(path)
    val res = fw.fromString("hello world")
    res.map {
      case LocalFileNotFoundError => "works!"
      case LocalFileWriteSuccess => {
        new File(path).delete
        assert(true == false)
      }
      case _ => assert(true == false)
    }
  }
}
