package agilexs.catalogxsadmin.presentation.server

import metaphor.psm.domaintogwt.ejb.SessionBeanField
import metaphor.psm.domaintogwt.ejb.StoreResult
import metaphor.psm.ejb.IOperation
import metaphor.psm.ejb.ISessionBean

/**
 * Set media value to null to avoid this blob value to be send to the client.
 */
[template FindPropertyValueByIdStoreResult(IOperation operation) constraint operation.name.equals("findPropertyValueById") extends StoreResult]
    [super(operation)]
        [content]
    [/super]
    ejbResult.setMediaValue(null);
[/template]