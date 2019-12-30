package com.CommonUtils.Config.SQL.Jdbc.Config;

import java.util.Collections;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.CustomCollections.HashMap;

public final class TransactionAttributeConfig 
{
	private static NameMatchTransactionAttributeSource INSTANCE = null;
	
	private static final int TX_METHOD_TIMEOUT = 10;
	
	private TransactionAttributeConfig() {}
	
	public static TransactionAttributeSource getInstance()
	{
		if (null == INSTANCE)
		{
			synchronized (TransactionAttributeConfig.class)
			{
				if (null == INSTANCE)
				{
					INSTANCE = new NameMatchTransactionAttributeSource();
					
					/*只读事务，不做更新操作*/
					RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
					readOnlyTx.setReadOnly(true);
					readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
					
					/*当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务*/
					RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
					requiredTx.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
					requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
					requiredTx.setTimeout(TX_METHOD_TIMEOUT);
					INSTANCE.setNameMap
					(
							new HashMap<String, TransactionAttribute>().put("save*", requiredTx)
																	   .put("batchSave*", requiredTx)
																	   .put("insert*", requiredTx)
																	   .put("batchInsert*", requiredTx)
																	   .put("del*", requiredTx)
																	   .put("batchDel*", requiredTx)
																	   .put("edit*", requiredTx)
																	   .put("batchEdit*", requiredTx)
																	   .put("update*", requiredTx)
																	   .put("batchUpdate*", requiredTx)
																	   .put("get*", readOnlyTx)
																	   .put("find*", readOnlyTx)
																	   .put("select*", readOnlyTx)
																	   .put("query*", readOnlyTx)
																	   .getMap()
				    );
				}
			}
		}
		
		return INSTANCE;
	}
}