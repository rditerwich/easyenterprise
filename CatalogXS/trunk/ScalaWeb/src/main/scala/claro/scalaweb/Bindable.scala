package claro.scalaweb

trait Bindable[A] {
	val prefix : String
	val bindings : Set[Binding]
	val documentation : String
	
	def bind(label : String, doc : String) = new BindingCtor[A](label)
	implicit def ctor(label : String) = new BindingCtor[A](label)
	implicit def toSet(binding : Binding) = Set(binding)
	def @@?(label : String, default : Boolean, documentation : String = "") = this
}


class BindingCtor[A](label : String) {
	def apply(f : => Binding) = f
	def attr(label : String, default : Boolean, doc : String) = false
}

case class Person(firstName: String, lastName : String)

class PersonBindable extends Bindable[Person] {
	val prefix = "person"
	val documentation = ""

			
//	"first-name" text(_.firstName) @@("Outputs the first name of the person")
		
}