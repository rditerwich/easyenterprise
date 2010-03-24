package agilexs.catalogxsadmin.businesslogic

import metaphor.psm.ejb.IOperation
import metaphor.psm.ejb.IParameter
import metaphor.psm.ejb.ISessionBean
import metaphor.psm.javaeeaspects.IEjbQueryImplementation
import metaphor.psm.javaeeaspects.QueryImplementation

[template FindActualPromotionsExecuteQuery(ISessionBean bean, IOperation operation, IEjbQueryImplementation queryImplementation) constraint operation.name.equals("findActualPromotions")joins QueryImplementation.ExecuteQuery]
    String queryString = "select a from Promotion a where a.shop = :shop and a.endDate >= :endDate order by a.endDate";

    final Query query = entityManager.createQuery(queryString);
    query.setParameter("shop", filter.getShop());
    query.setParameter("endDate", filter.getEndDate());
[/template]
