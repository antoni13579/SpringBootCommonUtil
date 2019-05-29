package com.CommonUtils.Utils.DivideTextUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.CommonUtils.Utils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.CollectionUtils.JavaCollectionsUtil;
import com.CommonUtils.Utils.DivideTextUtils.Bean.Calculation;
import com.CommonUtils.Utils.StringUtils.StringUtil;

public final class DivideTextUtil 
{
	private DivideTextUtil () {}
	
	public static Optional<Calculation> statistics(final String text1, final String text2)
	{
		//计算类
        Calculation calculation = null;
        
		if (StringUtil.isStrEmpty(text1) || StringUtil.isStrEmpty(text2))
		{ return Optional.ofNullable(calculation); }
		
		Map<String,int[]> resultMap = new HashMap<>();
		
		//统计
        statistics(resultMap, StringUtil.divideText(text1),1);
        statistics(resultMap, StringUtil.divideText(text2),0);
        
        calculation = new Calculation();
        Iterator<Map.Entry<String, int[]>> entries = resultMap.entrySet().iterator();
		while (entries.hasNext())
		{
			Map.Entry<String, int[]> entry = entries.next();
			int[] value = entry.getValue();
			
			BigDecimal numerator = calculation.getNumerator();
			BigDecimal elementA = calculation.getElementA();
			BigDecimal elementB = calculation.getElementB();
			
			calculation.setNumerator(numerator.add(new BigDecimal(value[0] * value[1])));
            calculation.setElementA(elementA.add(new BigDecimal(value[0] * value[0])));
            calculation.setElementB(elementB.add(new BigDecimal(value[1] * value[1])));
		}
        
        return Optional.ofNullable(calculation); 
	}
	
	 /**
     * 组合词频向量
     
     * @param words
     * @param direction
     * @return
     */
    private static void statistics(Map<String,int[]> map,List<String> words ,int direction)
    {
        if(JavaCollectionsUtil.isCollectionEmpty(words))
        { return; }
        
        int[] in = null;
        boolean flag = direction == 1 ? true : false;     //判断不同句子
        for (String word : words)
        {
            int[] wordD = map.get(word);
            
            if(ArrayUtil.isArrayEmpty(wordD))
            {
                if(flag) { in = new int[]{1, 0}; }
                else { in = new int[]{0, 1}; }
                map.put(word,in);
            }
            
            else
            {
                if(flag) { wordD[0]++; }
                else { wordD[1]++; }
            }
        }
    }
}