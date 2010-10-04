package claro.scalaweb

import xml.{Node, NodeSeq, Elem, MetaData, NamespaceBinding}

trait XmlProducer extends Function0[NodeSeq] {
	def children = Seq[XmlProducer]()
}

case class NodeProducer(node : Node) extends XmlProducer {
	def apply = node
}

case class ElemProducer(prefix : String, label : String, attributes : MetaData, scope : NamespaceBinding) extends XmlProducer {
	def apply = new Elem(prefix, label, attributes, scope, children.flatMap(_()):_*)
}

