package agilexs.catalogxsadmin.businesslogic;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

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
}
