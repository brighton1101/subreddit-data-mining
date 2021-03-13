package com.brighton1101.reddittracker.common

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object Json {
  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  def fromJson[T](json: String)(implicit m: Manifest[T]): T =
    mapper.readValue[T](json)

  def toJson(value: Any): String =
    mapper.writeValueAsString(value)

  def toDelimJson(values: Seq[Any], delim: String = "\n"): String =
    values.map(toJson(_)).mkString(delim) + delim
}

