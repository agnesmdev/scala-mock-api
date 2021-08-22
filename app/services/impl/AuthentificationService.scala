package services.impl

import play.api.{Configuration, Logging}
import services.TAuthentificationService

import javax.inject.{Inject, Singleton}
import javax.xml.bind.DatatypeConverter

@Singleton
class AuthentificationService @Inject()(configuration: Configuration) extends TAuthentificationService with Logging {

  private val secret: String = configuration.get[String]("api.secret")

  override def login(utilisateur: String, motDePasse: String): Boolean = {
    logger.debug(s"Tentative de connexion de l'utilisateur $utilisateur")

    val md = java.security.MessageDigest.getInstance("SHA-256")
    val pwd = s"$secret-$utilisateur"
    val hashedPassword = DatatypeConverter.printHexBinary(md.digest(pwd.getBytes))

    hashedPassword.toLowerCase() == motDePasse.toLowerCase()
  }
}
