package com.scale.forum.http.pagination

object PagedResult {
  def apply[A](pagination: Pagination,
               totalEntries: Int,
               entries: Seq[A]): PagedResult[A] = {

    val pageNumber = pagination.page
    val totalPages = getTotalPages(totalEntries, pagination.limit)

    PagedResult(entries, pageNumber, totalPages)
  }

  def getTotalPages(totalEntries: Int, limitPerPage: Int): Int = {
    math.ceil(totalEntries.toDouble / limitPerPage.toDouble).toInt
  }
}

case class PagedResult[A](entries: Seq[A],
                          pageNumber: Int,
                          totalPages: Int)
