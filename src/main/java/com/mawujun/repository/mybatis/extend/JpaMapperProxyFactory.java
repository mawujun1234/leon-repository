package com.mawujun.repository.mybatis.extend;

import java.lang.reflect.Proxy;

import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.session.SqlSession;

public class JpaMapperProxyFactory<T> extends MapperProxyFactory<T> {
	public JpaMapperProxyFactory(Class<Object> mapperInterface) {
		super((Class<T>)mapperInterface);
		// TODO Auto-generated constructor stub
	}

	protected T newInstance(MapperProxy<T> mapperProxy) {
		return (T) Proxy.newProxyInstance(this.getMapperInterface().getClassLoader(), new Class[]{this.getMapperInterface()},
				mapperProxy);
	}

	public T newInstance(SqlSession sqlSession) {
		JpaMapperProxy<T> mapperProxy = new JpaMapperProxy(sqlSession, this.getMapperInterface(), this.getMethodCache());
		return this.newInstance(mapperProxy);
	}
}