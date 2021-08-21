package models.exceptions

case class UtilisateurInconnuException(idUtilisateur: Int) extends Throwable(s"Utilisateur $idUtilisateur inconnu")
