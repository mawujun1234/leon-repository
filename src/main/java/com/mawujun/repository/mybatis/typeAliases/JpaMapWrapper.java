package com.mawujun.repository.mybatis.typeAliases;

import java.util.Map;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.MapWrapper;

import com.mawujun.util.StringUtil;

public class JpaMapWrapper extends MapWrapper {
	public JpaMapWrapper(MetaObject metaObject, Map<String, Object> map) {
		super(metaObject, map);
	}
	
	
	@Override
	public String findProperty(String name, boolean useCamelCaseMapping) {
		if(useCamelCaseMapping){      
			return StringUtil.underlineToCamel(name);
		}
		return name;
	}
}
