package com.scale.forum.server.mappers

import com.google.inject.Inject
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.inject.Logging

@com.google.inject.Singleton
class IllegalArgumentExceptionMapper @Inject()(response: ResponseBuilder)
  extends ExceptionMapper[IllegalArgumentException]
    with Logging {

  override def toResponse(request: Request, exception: IllegalArgumentException): Response = {
    error(
      s"Client bad request for: ${request.method} ${request.path}: ${exception.getLocalizedMessage}",
      exception)

    response.badRequest(exception.getMessage)
  }
}
