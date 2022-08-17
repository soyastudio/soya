package soya.framework.action.actions.document.csv;

import org.apache.commons.beanutils.DynaBean;

public interface Filter {
    boolean match(DynaBean bean);
}
