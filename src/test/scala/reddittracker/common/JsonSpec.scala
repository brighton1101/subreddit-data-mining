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

  "Json" should "handle newline delimited format" in {
    case class Test1(a: String)
    case class Test2(b: String)
    val a: Seq[Any] = Seq(Test1("a"), Test2("b"))
    assert( Json.toDelimJson(a) == "{\"a\":\"a\"}\n{\"b\":\"b\"}\n" )
  }
}
