package com.brighton1101.reddittracker.common.model

import scala.io
import com.brighton1101.reddittracker.common.Json
import org.scalatest.FlatSpec

class SubredditResponseSpec extends FlatSpec {
  behavior of "subreddit response model"

  "Example json response" should "be able to be deserialized into model" in {
    val body = io
      .Source
      .fromResource("test_reddit.json")
      .mkString
    val parsed = Json.fromJson[SubredditResponse](body)
    assert(parsed.kind == "Listing")
    assert(parsed.data.dist == 25)
    assert(parsed.data.children(0).data.subreddit == "scala")
  }
}
