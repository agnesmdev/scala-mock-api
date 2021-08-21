package models.exceptions

case class JeuExistantException(idJeu: Int) extends Throwable(s"Jeu déjà existant avec l'identifiant $idJeu")
