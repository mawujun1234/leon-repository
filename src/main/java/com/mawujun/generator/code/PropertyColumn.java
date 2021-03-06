package com.mawujun.generator.code;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mawujun.util.StringUtil;

public class PropertyColumn {
	private String column;//列名
	private String columnType;//列的类型 如varcahr(20),主要用在逆向生成的时候
	
	private String property;//属性名称
	
	private String label;//列的中文名，如果没有设置，就使用column
	private String comment;//注释
	private String defaultValue;//默认值


	private Integer length=255;//列的长度,只有当类型为varchar等的时候才有用
	private Integer precision=null;//当为数字的时候有用
	private Integer scale=null;
	
	private boolean unique=false;
	private boolean nullable=true;
	private boolean insertable=true;
	private boolean updatable=true;
	
	private boolean persistable=true;//是否是進行持久化的字段
	
//	public boolean hidden=false;//是否是隐藏字段
//	private Boolean nullable=true;//true表示可以为空
//	private Integer sort=0;//显示的顺序
//	private boolean genQuery=false;//是否生成查询条件，主要是在grid
	
	private Boolean isEnum=false;
	private Map<String,Object> enumValues=new LinkedHashMap<String,Object>();
//	private String showType="none";//显示的类型，是textfield，还是combobox，还是radio，还是
//	private Map<String,String> showType_values=new HashMap<String,String>();

	private String basepackage;//包名
	private String className;
	private Class<?> clazz;
	private String simpleClassName;//其实不用定义这个属性，其实是可以直接写get方法，下同
//	private String jsType;
	
	private boolean isId;//是否是id的属性
	private boolean isCompositeId=false;
	private IDGenEnum idGenEnum=IDGenEnum.none;
	
	private boolean isDateProp=false;
	
	private boolean isVersion=false;
	private boolean isLogicDelete=false;
	
	//===========================主要用于前端
	private boolean cndable;//是否在前端生成查询条件
	private boolean numberValidRule=false;//只用于element-ui的async-validator验证的时候，number属性验证数值范围的时候，需要在vue上加上.number,比如“v-model.number”
	private boolean uploadable=false;//用于控制前端是否要生成上传的组件
	private boolean disabled=false;
	
	private Boolean isFk=false;//表示这一列的数据，是在表单上通过下拉框获取的
	private String fk_module;
	private String fk_entitySimpleClassName;
	private String fk_entitySimpleClassNameUncap;
	//private Map<String,Object> fkData=new LinkedHashMap<String,Object>();
	
	//private Boolean isIdProperty=false;//是不是属于id的列
	//private Boolean isComponentType=false;
	//private Boolean isAssociationType=false;
	//private Boolean isBaseType=false;
	//private Boolean isCollectionType=false;
	//private Boolean isConstantType=false;//判断是不是常数
	
	//List<PropertyColumn> propertyColumns=new ArrayList<PropertyColumn>();
	/**
	 * 判断这个属性是不是日期类型
	 */
	public boolean getIsDateProp() {
		return isDateProp;
	}
	/**
	 * 增加枚举类型值
	 * @param key
	 * @param value
	 */
	public void addEnumValues(String key,String value) {
		this.enumValues.put(key, value);
	}

	/**
	 * 获取@Column注解需要的属性值
	 * @return
	 */
	public String getColumnAnnotation() {
		//List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		StringBuilder builder=new StringBuilder();
		if(length!=null) {
			builder.append(",length="+length);
		}
		if(precision!=null) {
			builder.append(",precision="+precision);
		}
		if(scale!=null) {
			builder.append(",scale="+scale);
		}
		if(unique==true) {
			builder.append(",unique="+unique);
		}
		if(nullable=false) {
			builder.append(",nullable="+nullable);
		}
		
		//builder.insert(0, );
		if(builder.length()>0) {
			return "@Column("+builder.substring(1)+")";
		} else {
			return null;
		}
	}
	
	public String getColDefine() {
		StringBuilder builder=new StringBuilder();
		if(StringUtil.hasText(comment)) {
			builder.append(",comment=\""+comment+"\"");
		}
		if(StringUtil.hasText(label)) {
			builder.append(",label=\""+label+"\"");
		}
		if(StringUtil.hasText(defaultValue)) {
			builder.append(",defaultValue=\""+defaultValue+"\"");
		}
		if(builder.length()>0) {
			return "@ColDefine("+builder.substring(1)+")";
		} else {
			return null;
		}
	}

	public void setClazz(Class<?> clazz) {
		this.clazz=clazz;
		this.className = clazz.getName();
		if(clazz.isPrimitive()) {
			throw new RuntimeException("请把"+this.getProperty()+"转换成包装类型");
		}
		this.basepackage=clazz.getPackage().getName();
		//System.out.println(javaType);
		this.simpleClassName=className.substring(className.lastIndexOf('.')+1);
		this.isDateProp=Date.class.isAssignableFrom(clazz);
		
		
		
//		//System.out.println(this.javaTypeClassName);
//		if(jsJavaMapper.get(clazz)==null){
//			this.jsType="string";
//		} else {
//			this.jsType=jsJavaMapper.get(clazz);//映射好后就是替换 自己写的类然后测试
//		}
//		
		//System.out.println(this.jsType);
	}

	public boolean getIsId() {
		return isId;
	}

	public void setIsId(boolean isID) {
		this.isId = isID;
	}

	public boolean isCompositeId() {
		return isCompositeId;
	}


	public void setIsCompositeId(boolean isCompositeId) {
		this.isCompositeId = isCompositeId;
	}

	public String getColumn() {
		return column;
	}


	public void setColumn(String column) {
		this.column = column;
	}


	public String getProperty() {
		return property;
	}


	public void setProperty(String property) {
		this.property = property;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	public String getDefaultValue() {
		return defaultValue;
	}


	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}


	public Integer getLength() {
		return length;
	}


	public void setLength(Integer length) {
		this.length = length;
	}


	public Integer getPrecision() {
		return precision;
	}


	public void setPrecision(Integer precision) {
		this.precision = precision;
	}


	public Integer getScale() {
		return scale;
	}


	public void setScale(Integer scale) {
		this.scale = scale;
	}


	public boolean isUnique() {
		return unique;
	}


	public void setUnique(boolean unique) {
		this.unique = unique;
	}


	public boolean isNullable() {
		return nullable;
	}


	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}


	public boolean isInsertable() {
		return insertable;
	}


	public void setInsertable(boolean insertable) {
		this.insertable = insertable;
	}


	public boolean isUpdatable() {
		return updatable;
	}


	public void setUpdatable(boolean updatable) {
		this.updatable = updatable;
	}


	public Boolean getIsEnum() {
		return isEnum;
	}


	public void setIsEnum(Boolean isEnum) {
		this.isEnum = isEnum;
	}


	public String getBasepackage() {
		return basepackage;
	}


	public void setBasepackage(String basepackage) {
		this.basepackage = basepackage;
	}


	public String getClassName() {
		return className;
	}


	public void setClassName(String className) {
		this.className = className;
	}


	public String getSimpleClassName() {
		return simpleClassName;
	}


	public void setSimpleClassName(String simpleClassName) {
		this.simpleClassName = simpleClassName;
	}


	

	public IDGenEnum getIdGenEnum() {
		return idGenEnum;
	}


	public void setIdGenEnum(IDGenEnum idGenEnum) {
		this.idGenEnum = idGenEnum;
	}


	public Class<?> getClazz() {
		return clazz;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	@Override
	public String toString() {
		return "PropertyColumn [column=" + column + ", columnType=" + columnType + ", property=" + property + ", label="
				+ label + ", comment=" + comment + ", defaultValue=" + defaultValue + ", length=" + length
				+ ", precision=" + precision + ", scale=" + scale + ", unique=" + unique + ", nullable=" + nullable
				+ ", insertable=" + insertable + ", updatable=" + updatable + ", isEnum=" + isEnum + ", basepackage="
				+ basepackage + ", className=" + className + ", clazz=" + clazz + ", simpleClassName=" + simpleClassName
				+ ", isId=" + isId + ", isCompositeId=" + isCompositeId + ", idGenEnum=" + idGenEnum + "]";
	}

	public void setId(boolean isId) {
		this.isId = isId;
	}

	public void setCompositeId(boolean isCompositeId) {
		this.isCompositeId = isCompositeId;
	}

	public boolean getIsVersion() {
		return isVersion;
	}

	public void setIsVersion(boolean isVersion) {
		this.isVersion = isVersion;
	}

	public boolean getIsLogicDelete() {
		return isLogicDelete;
	}

	public void setIsLogicDelete(boolean isLogicDelect) {
		this.isLogicDelete = isLogicDelect;
	}



	
	public boolean isCndable() {
		return cndable;
	}

	public void setCndable(boolean cndable) {
		this.cndable = cndable;
	}

	public Map<String, Object> getEnumValues() {
		return enumValues;
	}


	public void setVersion(boolean isVersion) {
		this.isVersion = isVersion;
	}

	public void setLogicDelete(boolean isLogicDelete) {
		this.isLogicDelete = isLogicDelete;
	}
	public boolean isPersistable() {
		return persistable;
	}

	public void setPersistable(boolean persistable) {
		this.persistable = persistable;
	}

	public boolean isNumberValidRule() {
		return numberValidRule;
	}

	public void setNumberValidRule(boolean numberValidRule) {
		this.numberValidRule = numberValidRule;
	}

	public boolean isUploadable() {
		return uploadable;
	}

	public void setUploadable(boolean uploadable) {
		this.uploadable = uploadable;
	}
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getFk_module() {
		return fk_module;
	}
	public void setFk_module(String fk_module) {
		this.fk_module = fk_module;
	}
	public String getFk_entitySimpleClassName() {
		return fk_entitySimpleClassName;
	}
	public void setFk_entitySimpleClassName(String fk_entitySimpleClassName) {
		this.fk_entitySimpleClassName = fk_entitySimpleClassName;
	}
	public Boolean getIsFk() {
		return isFk;
	}
	public void setIsFk(Boolean isFk) {
		this.isFk = isFk;
	}
	public String getFk_entitySimpleClassNameUncap() {
		return fk_entitySimpleClassNameUncap;
	}
	public void setFk_entitySimpleClassNameUncap(String fk_entitySimpleClassNameUncap) {
		this.fk_entitySimpleClassNameUncap = fk_entitySimpleClassNameUncap;
	}


	

}
