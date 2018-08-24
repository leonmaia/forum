package com.scale.forum.http.pagination

object Pagination {
  val LIMIT = 20
}

case class Pagination(page: Int=1, limit: Int=Pagination.LIMIT) {
  if (page < 0)
    throw new IllegalArgumentException("Invalid paginated request. Negative page number.")
  if (limit < 0)
    throw new IllegalArgumentException("Invalid paginated request. Negative limit number.")

  val drops: Int = (page-1) * limit
}