package com.mawujun.repository.mybatis.dialect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.RowBounds;

import com.mawujun.date.DateUtil;
import com.mawujun.generator.db.DbColumn;
import com.mawujun.repository.mybatis.interceptor.MetaObjectUtil;

/**
 * A dialect compatible with the H2 database.
 * 
 * @author mwj
 *
 */
public class H2Dialect extends AbstractDialect {

//    public boolean supportsLimit() {
//        return true;
//    }
//
//	public String getLimitString(String sql, int offset,String offsetPlaceholder, int limit, String limitPlaceholder) {
//		return new StringBuffer(sql.length() + 40).
//			append(sql).
//			append((offset > 0) ? " limit "+limitPlaceholder+" offset "+offsetPlaceholder : " limit "+limitPlaceholder).
//			toString();
//	}
//
//	@Override
//	public boolean supportsLimitOffset() {
//		return true;
//	}

	 @Override
	public Object processPageParameter1(MappedStatement ms, Map<String, Object> paramMap, RowBounds rowBounds, BoundSql boundSql, CacheKey pageKey) {
		paramMap.put(PAGEPARAMETER_FIRST, rowBounds.getLimit());
		paramMap.put(PAGEPARAMETER_SECOND, rowBounds.getOffset());
		// 处理pageKey
		//pageKey.update(rowBounds.getOffset());
		//pageKey.update(rowBounds.getLimit());
		// 处理参数配置
		if (boundSql.getParameterMappings() != null) {
			List<ParameterMapping> newParameterMappings = new ArrayList<ParameterMapping>(boundSql.getParameterMappings());
//	            if (page.getStartRow() == 0) {
//	                newParameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), PAGEPARAMETER_SECOND, Integer.class).build());
//	            } else {
			newParameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), PAGEPARAMETER_FIRST, Integer.class).build());
			newParameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), PAGEPARAMETER_SECOND, Integer.class).build());
//	            }
			MetaObject metaObject = MetaObjectUtil.forObject(boundSql);
			metaObject.setValue("parameterMappings", newParameterMappings);
		}
		return paramMap;
	}
	
	@Override
	public String getPageSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey pageKey) {
		String sql = boundSql.getSql();
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append(sql);
		sqlBuilder.append(" limit ? offset ? ");
		return sqlBuilder.toString();
	}

	@Override
	public String getDateFormatFunction() {
		// TODO Auto-generated method stub
		return "FORMATDATETIME";
	}

	
	@Override
	public String getDateFormatStr(String dateStr) {
		//http://www.mamicode.com/info-detail-1026392.html#formatdatetime
		//和java一致
		String date_pattern=DateUtil.resolverDateFormat(dateStr);
		if(date_pattern==null) {
			throw new IllegalArgumentException("当前的日期格式不支持:"+dateStr+",需要新增的话，新建date.pattern.properties文件，按"+getAlias()+".yyyy-MM-dd=yyyy-MM-dd,同时添加regular.yyyy-MM-dd=^\\\\\\\\d{4}-\\\\\\\\d{1,2}-\\\\\\\\d{1,2}$模式编写");
		}
		//return new String[] {date_pattern};
		return date_pattern;
	}

	@Override
	public DBAlias getAlias() {
		// TODO Auto-generated method stub
		return DBAlias.h2;
	}

	@Override
	public void addDateFormatStr(String java_pattern, String db_pattern) {
		//不需要做任何事情，因为h2的日期格式化和java是一样的
	}

	@Override
	public DbColumn columnTypeToProertyType(String columnType) {
		// TODO Auto-generated method stub
		return null;
	}
    
//    public boolean bindLimitParametersInReverseOrder() {
//        return true;
//    }    
//
//    public boolean bindLimitParametersFirst() {
//        return false;
//    }

    

}