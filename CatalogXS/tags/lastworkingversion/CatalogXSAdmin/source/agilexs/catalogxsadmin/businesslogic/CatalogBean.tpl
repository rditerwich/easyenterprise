package agilexs.catalogxsadmin.businesslogic

import metaphor.psm.ejb.IOperation
import metaphor.psm.ejb.IParameter
import metaphor.psm.ejb.ISessionBean
import metaphor.psm.javaeeaspects.IEjbQueryImplementation
import metaphor.psm.javaeeaspects.QueryImplementation

[template findByStringValueShopProductsExecuteQuery(ISessionBean bean, IOperation operation, IEjbQueryImplementation queryImplementation) constraint operation.name.equals("findByStringValueShopProducts")joins QueryImplementation.ExecuteQuery]
    final String queryString = "select p from Product p, in(p.propertyValues) pv where p.catalog = :catalog and pv.stringValue like :stringValue";
    final Query query = entityManager.createQuery(queryString);

    query.setParameter("catalog", filter.getShop().getCatalog());
    query.setParameter("stringValue", "%" + filter.getStringValue() + "%");
//TODO filter by shop and filter out excluded items    query.setParameter("shop", filter.getShop());
[/template]
