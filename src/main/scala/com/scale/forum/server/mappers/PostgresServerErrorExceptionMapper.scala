package com.scale.forum.server.mappers

import com.google.inject.Inject
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.postgres.codec.ServerError
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.inject.Logging


@com.google.inject.Singleton
class PostgresServerErrorExceptionMapper @Inject()(response: ResponseBuilder)
  extends ExceptionMapper[ServerError]
    with Logging {

  override def toResponse(request: Request, exception: ServerError): Response = {
    error(
      s"Error when calling ${request.method} ${request.path}: ${exception.sqlState.getOrElse("")} ${exception.getLocalizedMessage}",
      exception)

    exception.sqlState match {
      case Some("23505") => response.conflict
      case _             => response.internalServerError
    }
  }
}
