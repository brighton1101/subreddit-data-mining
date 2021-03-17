package com.brighton1101.reddittracker.common

import sttp.client3._
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.{ExecutionContext, Future}

sealed trait HttpClientError
case class UnknownRequestError() extends HttpClientError
case class InvalidResponseError() extends HttpClientError

trait HttpClient {
  def asyncGet[T](
                   url: String,
                   params: Map[String, String] = Map(),
                   headers: Map[String, String] = Map()
                 )(implicit mt: Manifest[T], ec: ExecutionContext): Future[Either[HttpClientError, T]]
}

class SttpHttpClient(backend: SttpBackend[Future, Any]) extends HttpClient {

  def asyncGet[T](
    url: String,
    params: Map[String, String] = Map(),
    headers: Map[String, String] = Map()
  )(implicit mt: Manifest[T], ec: ExecutionContext): Future[Either[HttpClientError, T]] = {
    try {
      basicRequest
        .get(uri"$url?$params")
        .headers(headers)
        .send(backend)
        .map { res =>
          res.body match {
            case Right(body) => Right(Json.fromJson[T](body))
            case Left(_) => Left(InvalidResponseError())
          }
        }
    } catch {
      case _: Throwable => Future { Left(UnknownRequestError()) }
    }
  }
}

object SttpHttpClient {
  def getClient(implicit ec: ExecutionContext): SttpHttpClient = new SttpHttpClient(AsyncHttpClientFutureBackend())
}

