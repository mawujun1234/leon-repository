package com.mawujun.repository.mybatis.interceptor;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;

public class MSUtils {
	public static final String countSuffix = "_count";
    private static final List<ResultMapping> EMPTY_RESULTMAPPING = new ArrayList<ResultMapping>(0);

    /**
     * 新建count查询的MappedStatement
     *
     * @param ms
     * @param newMsId
     * @return
     */
    public static MappedStatement newCountMappedStatement(MappedStatement ms, String newMsId) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), newMsId, ms.getSqlSource(), ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        //count查询返回值int
        List<ResultMap> resultMaps = new ArrayList<ResultMap>();
        ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), ms.getId(), Long.class, EMPTY_RESULTMAPPING).build();
        resultMaps.add(resultMap);
        builder.resultMaps(resultMaps);
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    /**
     * 新建count查询的MappedStatement
     *
     * @param ms
     * @return
     */
    public static MappedStatement newCountMappedStatement(MappedStatement ms) {
        return newCountMappedStatement(ms, ms.getId() + countSuffix);
    }
}
