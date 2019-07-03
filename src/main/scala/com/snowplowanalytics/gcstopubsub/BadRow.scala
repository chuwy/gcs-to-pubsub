package com.snowplowanalytics.gcstopubsub

import cats.syntax.either._

import java.util.Base64.getDecoder

import io.circe.Decoder
import io.circe.parser.{ parse => parseJson }
import io.circe.generic.semiauto._


/**
  * Bad row extracted after BigQuery loader
  * @param line original base64-encoded TSV line
  * @param errors list of errors
  */
case class BadRow(line: String, errors: List[String]) {
  def getTsv: String =
    new String(BadRow.Base64Decoder.decode(line))
}

object BadRow {
  private val Base64Decoder = getDecoder

  implicit val originalDecoder: Decoder[BadRow] =
    deriveDecoder[BadRow]

  def parse(line: String): BadRow =
    parseJson(line)
      .flatMap(_.as[BadRow])
      .valueOr(throw new RuntimeException(s"Unexpected data in GCS: $line"))
}

