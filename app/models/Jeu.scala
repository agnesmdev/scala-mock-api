package models

import play.api.libs.json.{Format, JsError, JsResult, JsString, JsSuccess, JsValue, Json}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

case class Jeu(id: Int,
               console: Console,
               nom: String,
               dateDeSortie: LocalDate) {
  def egal(jeu: Jeu): Boolean = {
    console == jeu.console && nom == jeu.nom && dateDeSortie.toString == jeu.dateDeSortie.toString
  }
}

object Jeu {
  private val formateurDate = DateTimeFormatter.ofPattern("DD/MM/YYYY")
  implicit val formateurLocalDate: Format[LocalDate] = new Format[LocalDate] {
    override def writes(ld: LocalDate): JsValue = JsString(formateurDate.format(ld))

    override def reads(json: JsValue): JsResult[LocalDate] = json match {
      case JsString(valeur) => try {
        JsSuccess(LocalDate.parse(valeur, formateurDate))
      } catch {
        case _: Exception => JsError("Mauvais format de date")
      }
    }
  }

  implicit val formateurJeu: Format[Jeu] = new Format[Jeu] {
    override def writes(j: Jeu): JsValue = Json.obj(
      "id" -> j.id,
      "console" -> j.console,
      "nom" -> j.nom,
      "dateDeSortie" -> j.dateDeSortie
    )

    override def reads(json: JsValue): JsResult[Jeu] = for {
      console <- (json \ "console").validate[Console]
      nom <- (json \ "nom").validate[String]
      dateDeSortie <- (json \ "email").validate[LocalDate]
    } yield {
      Jeu(0, console, nom, dateDeSortie)
    }
  }
}
