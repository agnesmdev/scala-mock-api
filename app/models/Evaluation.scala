package models

import play.api.libs.json.{Format, JsResult, JsValue, Json}

case class Evaluation(idUtilisateur: Int,
                      idJeu: Int,
                      tempsDeJeu: Duree,
                      note: Double,
                      commentaire: String) {
  def egal(evaluation: Evaluation): Boolean = {
    idUtilisateur == evaluation.idUtilisateur && idJeu == evaluation.idJeu
  }
}

object Evaluation {
  implicit val formateurEvaluation: Format[Evaluation] = new Format[Evaluation] {
    override def writes(e: Evaluation): JsValue = Json.obj(
      "idUtilisateur" -> e.idUtilisateur,
      "idJeu" -> e.idJeu,
      "tempsDeJeu" -> e.tempsDeJeu,
      "note" -> e.note,
      "commentaire" -> e.commentaire
    )

    override def reads(json: JsValue): JsResult[Evaluation] = for {
      idUtilisateur <- (json \ "idUtilisateur").validateOpt[Int]
      idJeu <- (json \ "idJeu").validateOpt[Int]
      tempsDeJeu <- (json \ "tempsDeJeu").validate[Duree]
      note <- (json \ "note").validate[Double]
      commentaire <- (json \ "commentaire").validate[String]
    } yield {
      Evaluation(idUtilisateur.getOrElse(0), idJeu.getOrElse(0), tempsDeJeu, note, commentaire)
    }
  }
}