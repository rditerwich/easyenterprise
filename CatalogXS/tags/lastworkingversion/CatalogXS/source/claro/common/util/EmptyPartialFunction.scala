package claro.common.util

object EmptyPartialFunction extends PartialFunction[Any,Any] {
  def apply[A,B]() = this.asInstanceOf[PartialFunction[A,B]]
  override def isDefinedAt(x : Any) = false
  override def apply(obj : Any) = null
}
