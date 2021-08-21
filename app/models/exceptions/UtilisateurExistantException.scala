package models.exceptions

case class UtilisateurExistantException(idUtilisateur: Int) extends Throwable(s"Utilisateur déjà existant avec l'identifiant $idUtilisateur")
