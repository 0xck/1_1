package ru.otus.sc.echo.model

sealed trait EchoResponse
object EchoResponse {
  case class Response(answer: String) extends EchoResponse
  case class Error(error: String)   extends EchoResponse
}
