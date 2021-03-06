package ar.com.caeldev.bsacore.services

import ar.com.caeldev.bsacore.dao.{ GenericDao, MongoDaoImpl }
import ar.com.caeldev.bsacore.services.exceptions.ServiceException
import ar.com.caeldev.bsacore.config.ConfigContext
import scala.util.control.Exception._
import ar.com.caeldev.bsacore.dao.exceptions.DaoException
import org.slf4j.LoggerFactory

trait Service[T <: AnyRef] {

  implicit val mot: Manifest[T]
  def logger = LoggerFactory.getLogger(this.getClass)

  val dao: GenericDao[T] = new MongoDaoImpl[T]()

  val appConfigContext: ConfigContext = new ConfigContext("errors.conf")
  val catcher = catching(classOf[DaoException]).withApply(e => throw new ServiceException(appConfigContext.get("errors.services.1101.description"), e))

  def add(entity: T): T

  def delete(id: Any)

  def update(entity: T): T

  def get(id: Any): T

  def get(field: String, id: Any): List[T] = {
    logger.info(s"Enter get ($field, $id) method")
    dao.findBy(field, id)
  }

  def getAll: List[T] = {
    logger.info("Enter getAll method")
    dao.findAll()
  }

}
