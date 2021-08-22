package services

import com.google.inject.ImplementedBy
import services.impl.AuthentificationService

@ImplementedBy(classOf[AuthentificationService])
trait TAuthentificationService {

  def login(utilisateur: String, motDePasse: String): Boolean
}
