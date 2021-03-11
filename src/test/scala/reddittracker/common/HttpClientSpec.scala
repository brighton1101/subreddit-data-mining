package com.brighton1101.reddittracker.common

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import sttp.client3.testing.SttpBackendStub
import sttp.model.StatusCode
import sttp.client3.Response
import org.scalatest.FlatSpec

case class TestRedditRes(hello: String)

class HttpClientSpec extends FlatSpec {
  behavior of "client"

  "Client" should "respond to valid requests" in {
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
    val uri = "https://www.reddit.com/r/javascript.json"
    val sttpBackend = SttpBackendStub.asynchronousFuture.whenRequestMatchesPartial { case r =>
      assert(r.uri.toString == uri)
      Response("{\"hello\": \"world\"}", StatusCode.Ok)
    }
    val res = new SttpHttpClient(sttpBackend).asyncGet[TestRedditRes](uri) 
    res.map { compl =>
      assert(compl.right.get == TestRedditRes("world"))
    }
    Await.result(res, Duration.Inf)
  }

  "Client" should "handle bad requests" in {
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
    val uri = "https://www.reddit.com/r/notfound.json"
    val sttpBackend = SttpBackendStub.asynchronousFuture.whenRequestMatchesPartial { case r =>
      assert(r.uri.toString == uri)
      Response("NOT FOUND", StatusCode.NotFound)
    }
    val res = new SttpHttpClient(sttpBackend).asyncGet[TestRedditRes](uri)
    res.map { compl =>
      assert(compl.left.get == InvalidResponseError())
    }
    Await.result(res, Duration.Inf)
  }
}
