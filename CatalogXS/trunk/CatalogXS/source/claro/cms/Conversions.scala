package claro.cms

import CMS.{Namespace,Tag}

object Conversions {
  implicit def bindingCtor(name : Tag) = new BindingCtor(name)
  implicit def rootBinding(c : ComplexBindingCtor2) = c.toRootBinding
  implicit def rootBinding(c : OptionBindingCtor2) = c.toRootBinding
  implicit def rootBinding(c : BindingCtor2) = c.toRootBinding
  implicit def toBinding(c : ComplexBindingCtor2) = c.toBinding
  implicit def toBinding(c : OptionBindingCtor2) = c.toBinding
  implicit def toBinding(c : BindingCtor2) = c.toBinding
  implicit def toTuple(c : BindingCtor2) = c.toTuple
  implicit def toTuple(c : OptionBindingCtor2) = c.toTuple
  implicit def toList(a : Scope) = List(a)
}
