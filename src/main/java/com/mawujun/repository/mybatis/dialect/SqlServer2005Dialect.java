package com.mawujun.repository.mybatis.dialect;

import java.util.Map;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.RowBounds;

import com.mawujun.utils.DateUtils;

/**
 * https://www.cnblogs.com/fengxiaojiu/p/7994124.html 分页方法
 * 两种方式：select * from ( 
　　　　select *, ROW_NUMBER() OVER(Order by ArtistId ) AS RowId from ArtistModels 
　　) as b
  where RowId between 10 and 20 
  
  方式2：注意这里的top
  select top pageSize o.* from (select row_number() over(order by orderColumn) as rownumber,* from(sql) as o where rownumber>firstIndex;
      
 * @author mwj
 */
public class SqlServer2005Dialect extends AbstractDialect{


	@Override
	public String getPageSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds,
			CacheKey pageKey) {
		//如果有order by子句，那就把order by子句提取出来放到ROW_NUMBER() OVER(Order by name )里面
		//如果没有order by子句，就提醒用户添加order by子句
		String sql = boundSql.getSql();
	    String orderBy=super.getEndOrderBy(sql);
	    if("".equals(orderBy) || null==orderBy) {
	    	throw new RuntimeException("请在sql的最后加上order by子句。");
	    }
	    //清除所有的order by子句
	    sql=super.removeOrderby(sql);
	    
	    //在第一个form处加上row_number()
	     sql=replaceSql(sql,orderBy);
	     
		return sql;
	}
	
	public String replaceSql(String sql_orginal,String orderBy) {
		String sql="select * from ( "+sql_orginal.replaceFirst("from", ", ROW_NUMBER() OVER("+orderBy+") AS RowId from ")+") as b"
				+ " where RowId between ? and ? ";
		return sql;
	}

	@Override
	public Object processPageParameter1(MappedStatement ms, Map<String, Object> paramMap, RowBounds rowBounds,
			BoundSql boundSql, CacheKey pageKey) {
		paramMap.put(PAGEPARAMETER_FIRST, rowBounds.getOffset());
        paramMap.put(PAGEPARAMETER_SECOND, rowBounds.getLimit());
        //处理pageKey
        pageKey.update(rowBounds.getOffset());
        pageKey.update(rowBounds.getOffset()+rowBounds.getLimit());
        //处理参数配置
        handleParameter(boundSql, ms);
        return paramMap;
	}
	
	@Override
	public String getDateFormatFunction() {
		// TODO Auto-generated method stub
		return "CONVERT";
	}

	@Override
	public String getDateFormatStr(String dateStr) {
		String date_pattern=DateUtils.resolverDateFormat(dateStr);
		String db_pattern=SqlServer2012Dialect.date_pattern_map.get(date_pattern);
		if(db_pattern==null) {
			throw new IllegalArgumentException("当前的日期格式不支持:"+dateStr);
		}
		//return new String[] {"varchar(100)",db_pattern};
		return db_pattern;
	}
	
//
//	public boolean supportsLimitOffset(){
//		return false;
//	}
//	
//	public boolean supportsLimit() {
//		return true;
//	}
//	
//	static int getAfterSelectInsertPoint(String sql) {
//		int selectIndex = sql.toLowerCase().indexOf( "select" );
//		final int selectDistinctIndex = sql.toLowerCase().indexOf( "select distinct" );
//		return selectIndex + ( selectDistinctIndex == selectIndex ? 15 : 6 );
//	}
//
//	public String getLimitString(String sql, int offset, int limit) {
//		return getLimitString(sql,offset,null,limit,null);
//	}
//
//	public String getLimitString(String querySelect, int offset,String offsetPlaceholder, int limit, String limitPlaceholder) {
//		if ( offset > 0 ) {
//			throw new UnsupportedOperationException( "sql server has no offset" );
//		}
////		if(limitPlaceholder != null) {
////			throw new UnsupportedOperationException(" sql server not support variable limit");
////		}
//		
//		return new StringBuffer( querySelect.length() + 8 )
//				.append( querySelect )
//				.insert( getAfterSelectInsertPoint( querySelect ), " top " + limit )
//				.toString();
//	}
//	
//	// TODO add Dialect.supportsVariableLimit() for sqlserver 
////	public boolean supportsVariableLimit() {
////		return false;
////	}

}
