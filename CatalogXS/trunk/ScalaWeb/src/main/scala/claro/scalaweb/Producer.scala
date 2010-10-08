package claro.scalaweb

import xml.{Node, NodeSeq, Elem, MetaData, NamespaceBinding}

trait Producer extends Function0[NodeSeq] {
	def children = Seq[Producer]()
}

case class NodeProducer(node : Node) extends Producer {
	def apply = node
}

case class ElemProducer(prefix : String, label : String, attributes : MetaData, scope : NamespaceBinding) extends Producer {
	def apply = new Elem(prefix, label, attributes, scope, children.flatMap(_()):_*)
}

