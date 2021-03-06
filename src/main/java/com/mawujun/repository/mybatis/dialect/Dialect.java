package com.mawujun.repository.mybatis.dialect;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.RowBounds;

import com.mawujun.generator.db.DbColumn;
import com.mawujun.repository.utils.Cnd;
import com.mawujun.repository.utils.Page;

/**
 *数据库方言，针对不同数据库进行实现
 * @author mwj
 */
public interface Dialect {
	//分页的id后缀
    String SUFFIX_PAGE = "_PageHelper";
	  //第一个分页参数
    String PAGEPARAMETER_FIRST = "First" + SUFFIX_PAGE;
    //第二个分页参数
    String PAGEPARAMETER_SECOND = "Second" + SUFFIX_PAGE;
    

    

    
    /**
     * 生成分页查询 sql
     *
     * @param ms              MappedStatement
     * @param boundSql        绑定 SQL 对象
     * @param parameterObject 方法参数
     * @param rowBounds       分页参数
     * @param pageKey         分页缓存 key
     * @return
     */
    String getCountSql(MappedStatement ms, BoundSql boundSql, Object parameterObject,  Cnd condition, CacheKey pageKey);

    public Object processParameterObject(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql, CacheKey pageKey);

	String getPageSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey pageKey);
	
	
	public DbColumn columnTypeToProertyType(String columnType) ;
	
//    public boolean supportsLimit(){
//    	return false;
//    }
//
//    public boolean supportsLimitOffset() {
//    	return supportsLimit();
//    }
//    
//    /**
//     * 将sql变成分页sql语句,直接使用offset,limit的值作为占位符.</br>
//     * 源代码为: getLimitString(sql,offset,String.valueOf(offset),limit,String.valueOf(limit))
//     */
//    public String getLimitString(String sql, int offset, int limit) {
//    	return getLimitString(sql,offset,Integer.toString(offset),limit,Integer.toString(limit));
//    }
//    
//    /**
//     * 将sql变成分页sql语句,提供将offset及limit使用占位符(placeholder)替换.
//     * <pre>
//     * 如mysql
//     * dialect.getLimitString("select * from user", 12, ":offset",0,":limit") 将返回
//     * select * from user limit :offset,:limit
//     * </pre>
//     * @return 包含占位符的分页sql
//     */
//    public String getLimitString(String sql, int offset,String offsetPlaceholder, int limit,String limitPlaceholder) {
//    	throw new UnsupportedOperationException("paged queries not supported");
//    }
//    
}
