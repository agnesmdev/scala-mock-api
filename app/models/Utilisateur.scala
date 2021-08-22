package models

import play.api.libs.json.{Format, JsResult, JsValue, Json}

case class Utilisateur(id: Int,
                       nom: String,
                       prenom: String,
                       age: Int,
                       email: String) {
  def egal(utilisateur: Utilisateur): Boolean = {
    nom == utilisateur.nom && prenom == utilisateur.prenom && age == utilisateur.age && email == utilisateur.email
  }
}

object Utilisateur {
  implicit val formateurUtilisateur: Format[Utilisateur] = new Format[Utilisateur] {
    override def writes(u: Utilisateur): JsValue = Json.obj(
      "id" -> u.id,
      "nom" -> u.nom,
      "prenom" -> u.prenom,
      "age" -> u.age,
      "email" -> u.email
    )

    override def reads(json: JsValue): JsResult[Utilisateur] = for {
      id <- (json \ "id").validateOpt[Int]
      nom <- (json \ "nom").validate[String]
      prenom <- (json \ "prenom").validate[String]
      age <- (json \ "age").validate[Int]
      email <- (json \ "email").validate[String]
    } yield {
      Utilisateur(id.getOrElse(0), nom, prenom, age, email)
    }
  }
}
