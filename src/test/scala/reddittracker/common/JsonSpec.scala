package com.brighton1101.reddittracker.common

import org.scalatest.FlatSpec

case class TestClass(
  name: String
)

class JsonSpec extends FlatSpec {
  behavior of "Json objects"

  "Json" should "deserialize object" in {
    assert(
      Json.fromJson[TestClass]("{\"name\": \"helloworld\"}") == TestClass("helloworld")
    )
  }

  "Json" should "serialize object" in {
    assert(
      Json.toJson(TestClass("helloworld")) == "{\"name\":\"helloworld\"}"
    )
  }
}