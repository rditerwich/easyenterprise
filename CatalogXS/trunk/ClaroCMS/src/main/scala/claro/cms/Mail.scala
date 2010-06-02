package claro.cms

import claro.common.util.Conversions._
import javax.mail.{Authenticator,PasswordAuthentication}
import net.liftweb.util.Mailer
import net.liftweb.util.Mailer.{From,To,Subject,MailTypes}
import net.liftweb.common.Full

object Mail {

  def mail(subject : Subject, rest : MailTypes*) = {
    
    // confg mailer
    lazy val mailProperties = Website.instance.config.properties.findAll("mail.")
    System.setProperty("mail.smtp.auth", "true")
    System.setProperty("mail.smtp.starttls.enable", "true")
    for ((p, value) <- mailProperties) System.setProperty(p, value)
    val from = mailProperties.getOrElse("mail.smtp.from", "")
    val host = mailProperties.getOrElse("mail.smtp.host", "")
    val user = mailProperties.getOrElse("mail.smtp.user", "")
    val password = mailProperties.getOrElse("mail.smtp.password", "")
    Mailer.authenticator = Full(new Authenticator {
      override def getPasswordAuthentication = new PasswordAuthentication(user, password)
    })
    
    Mailer.sendMail(From(from), subject, rest:_*)
  }
}
