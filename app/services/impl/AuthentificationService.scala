package services.impl

import play.api.{Configuration, Logging}
import services.TAuthentificationService

import javax.inject.{Inject, Singleton}
import javax.xml.bind.DatatypeConverter

@Singleton
class AuthentificationService @Inject()(configuration: Configuration) extends TAuthentificationService with Logging {

  private val secret: String = configuration.get[String]("api.secret")

  override def login(user: String, password: String): Boolean = {
    logger.debug(s"Tentative de connexion de l'utilisateur $password")

    val md = java.security.MessageDigest.getInstance("SHA-256")
    val pwd = s"$secret-$user"
    val hashedPassword = DatatypeConverter.printHexBinary(md.digest(pwd.getBytes))

    hashedPassword == password
  }
}
