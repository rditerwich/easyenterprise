package agilexs.catalogxsadmin.businesslogic;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

import agilexs.catalogxsadmin.jpa.shop.Order;
import agilexs.catalogxsadmin.jpa.shop.ProductOrder;

@Stateless
public class ShopBean extends ShopBeanBase implements Shop {
  
  @EJB
  private Catalog catalogSessionBean;
  
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public Order findOrderById(Long id) {
    final Order order = super.findOrderById(id);

    if (order != null) {
      for (ProductOrder po : order.getProductOrders()) {
        po.setProduct(catalogSessionBean.findProductById(po.getProduct().getId()));
      }
    }
    return order;
  }
  
  @Override
  @SuppressWarnings("unchecked")
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public List<Order> findActualOrders(Integer fromIndex, Integer pageSize, Order filter) {
      // Validate the input arguments
      if (fromIndex == null || fromIndex < 0) {
          throw new IllegalArgumentException("fromIndex < 0, from index must be 0 at least");
      }
      if (pageSize == null || pageSize < 1) {
          throw new IllegalArgumentException("pageSize < 1, page size must be 1 at least");
      }

      String queryString = "select a from Order a";
      
      if (filter != null) {
        queryString += (" where a.status = :status");
      }
      Query query = entityManager.createQuery(queryString);

      if (filter != null) {
        query.setParameter("status", filter.getStatus());
      }
      query.setFirstResult(fromIndex);
      query.setMaxResults(pageSize);
      return query.getResultList();
  }
}
