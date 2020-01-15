package com.CommonUtils.Config.SQL.Jdbc.Config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

public final class TransactionAttributeConfig 
{
	private static final int TX_METHOD_TIMEOUT = 10;
	
	private TransactionAttributeConfig() {}
	
	public static TransactionAttributeSource getInstance()
	{ return NameMatchTransactionAttributeSourceSingletonContainer.instance; }
	
	private static class NameMatchTransactionAttributeSourceSingletonContainer
	{
		private static NameMatchTransactionAttributeSource instance = new NameMatchTransactionAttributeSource();
		
		static
		{			
			/*只读事务，不做更新操作*/
			RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
			readOnlyTx.setReadOnly(true);
			readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
			
			/*当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务*/
			RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
			requiredTx.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
			requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
			requiredTx.setTimeout(TX_METHOD_TIMEOUT);
			
			Map<String, TransactionAttribute> nameMap = new HashMap<>();
			nameMap.put("save*", requiredTx);
			nameMap.put("batchSave*", requiredTx);
			nameMap.put("insert*", requiredTx);
			nameMap.put("batchInsert*", requiredTx);
			nameMap.put("del*", requiredTx);
			nameMap.put("batchDel*", requiredTx);
			nameMap.put("edit*", requiredTx);
			nameMap.put("batchEdit*", requiredTx);
			nameMap.put("update*", requiredTx);
			nameMap.put("batchUpdate*", requiredTx);
			nameMap.put("get*", readOnlyTx);
			nameMap.put("find*", readOnlyTx);
			nameMap.put("select*", readOnlyTx);
			nameMap.put("query*", readOnlyTx);
			instance.setNameMap(nameMap);
		}
	}
}