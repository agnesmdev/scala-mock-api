package models.exceptions

case class JeuInconnuException(idJeu: Int) extends Throwable(s"Jeu $idJeu inconnu")
