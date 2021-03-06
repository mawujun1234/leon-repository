package com.mawujun.generator.code;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mawujun.generator.db.DbColumn;
import com.mawujun.generator.db.IDbQuery;
import com.mawujun.generator.other.DefaultNameStrategy;
import com.mawujun.generator.other.NameStrategy;
import com.mawujun.repository.mybatis.dialect.AbstractDialect;
import com.mawujun.repository.mybatis.dialect.DBAlias;
import com.mawujun.repository.mybatis.dialect.DialectUtils;
import com.mawujun.util.PropertiesUtils;
import com.mawujun.util.StringUtil;

/**
 * 专门用来读取表结构的
 * 
 * @author mawujun 16064988
 *
 */
public class DbTableMetadataService {
	NameStrategy nameStrategy = new DefaultNameStrategy();

	private String db_driverName;
	private String db_url;
	private String db_username;
	private String db_password;
	private String db_schemaname;
	private boolean db_uselombok;

	//private String basepackage;

	private String[] tablePrefix;// 前缀,原始数据是以逗号进行分隔,复核前缀的肯定加进去
	private String[] columnPrefix;// 列的前缀，以逗号分隔
	private String[] include;// 包括的表，不复核前缀，但是在include中的也加进去
	private String[] exclude;// 排除的表,优先级比include和tablePrefix高，肯定会被排除
	
	private String[] version;
	private String[] logicdelete;

	IDbQuery dbQuery;

	public DbTableMetadataService() {
		try {
			PropertiesUtils aa = PropertiesUtils.load("generator.properties");
			String className = aa.getProperty("nameStrategy");
			if (StringUtil.hasText(className)) {
				Class clazz = Class.forName(className);
				nameStrategy = (NameStrategy) clazz.newInstance();
			}

			db_driverName = aa.getProperty("db.driverName");
			db_url = aa.getProperty("db.url");
			db_username = aa.getProperty("db.username");
			db_password = aa.getProperty("db.password");
			db_schemaname = aa.getProperty("db.schemaname");
			db_uselombok = aa.getBoolean("db.uselombok",true);
			// code_basepackage=aa.getProperty("code.schemaname");

			String db_tablePrefix = aa.getProperty("db.tablePrefix");
			if (StringUtil.hasText(db_tablePrefix)) {
				tablePrefix = db_tablePrefix.split(",");
			} else {
				tablePrefix = new String[0];
			}
			String db_columnPrefix = aa.getProperty("db.columnPrefix");
			if (StringUtil.hasText(db_columnPrefix)) {
				columnPrefix = db_columnPrefix.split(",");
			} else {
				columnPrefix = new String[0];
			}

			String db_include = aa.getProperty("db.include");
			if (StringUtil.hasText(db_include)) {
				include = db_include.split(",");
			} else {
				include = new String[0];
			}
			String db_exclude = aa.getProperty("db.exclude");
			if (StringUtil.hasText(db_exclude)) {
				exclude = db_exclude.split(",");
			} else {
				exclude = new String[0];
			}
			
			
			
			String db_version = aa.getProperty("db.version.column");
			if (StringUtil.hasText(db_version)) {
				version = db_version.split(",");
			} else {
				version = new String[]{"version"};
			}
			
			String db_logicdelete = aa.getProperty("db.logicdelete.column");
			if (StringUtil.hasText(db_logicdelete)) {
				logicdelete = db_logicdelete.split(",");
			} else {
				logicdelete = new String[]{"deleted"};
			}

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public Connection getConn() {
		if (connection != null) {
			return connection;
		}
		Connection conn = null;
		try {
			Class.forName(db_driverName);
			conn = DriverManager.getConnection(db_url, db_username, db_password);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	private IDbQuery getDbQuery() {

		Connection connection = getConn();
		DBAlias dbAlias = DialectUtils.getDialect_name(connection);

		Class<? extends IDbQuery> dbQueryClass = dbAlias.getDbQueryClass();
		if (dbQueryClass == null) {
			throw new RuntimeException("当前数据库不支持反向生成代码");
		}
		IDbQuery dbQuery = dbAlias.getDbQuery();
//		try {
//			dbQuery= dbQueryClass.newInstance();
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally {
//			// 释放资源
//			try {
//				if (connection != null) {
//					connection.close();
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		try {
//			if (connection != null) {
//				connection.close();
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}

		return dbQuery;
	}

	Connection connection = null;

	/**
	 * 
	 * @param tablename 如果tablename=null，就表示读取所有的
	 * @return
	 */
	public List<EntityTable> getTablesInfo(String... tablenames) {
		boolean isInclude = (null != include && include.length > 0);
		boolean isExclude = (null != exclude && exclude.length > 0);
		boolean isTablePrefix = (null != tablePrefix && tablePrefix.length > 0);

		// 所有的表信息
		List<EntityTable> tableList = new ArrayList<>();
//		// 需要反向生成或排除的表信息
//		List<EntityTable> includeTableList = new ArrayList<>();
//		List<EntityTable> excludeTableList = new ArrayList<>();
//		// 不存在的表名
//		Set<String> notExistTables = new HashSet<>();
		connection = getConn();
		dbQuery = getDbQuery();

		PreparedStatement preparedStatement = null;
		try {
			String tablesSql = dbQuery.tablesSql();
			if (DBAlias.mysql == dbQuery.dbType()) {
				if (tablenames != null && tablenames.length > 0) {

					tablesSql = tablesSql + " where name in (" + StringUtil.joinAround(tablenames, ",", "'") + ")";
				}
			} else if (DBAlias.postgresql == dbQuery.dbType()) {
				tablesSql = String.format(tablesSql, db_schemaname);
			} else if (DBAlias.oracle == dbQuery.dbType()) {//// oracle数据库表太多，出现最大游标错误
				if (isInclude) {
					StringBuilder sb = new StringBuilder(tablesSql);
					sb.append(" WHERE ").append(dbQuery.tableName()).append(" IN (");
					for (String tbname : include) {
						sb.append("'").append(tbname.toUpperCase()).append("',");
					}
					sb.replace(sb.length() - 1, sb.length(), ")");
					tablesSql = sb.toString();
				}
				if (isExclude) {
					StringBuilder sb = new StringBuilder(tablesSql);
					sb.append(" WHERE ").append(dbQuery.tableName()).append(" NOT IN (");
					for (String tbname : exclude) {
						sb.append("'").append(tbname.toUpperCase()).append("',");
					}
					sb.replace(sb.length() - 1, sb.length(), ")");
					tablesSql = sb.toString();
				}
			}

			preparedStatement = connection.prepareStatement(tablesSql);
			ResultSet results = preparedStatement.executeQuery();
			EntityTable tableInfo;
			boolean isadd = true;// 判断是否要添加到生成的list中
			while (results.next()) {
				String tableComment = results.getString(dbQuery.tableComment());
				// if (config.isSkipView() && "VIEW".equals(tableComment)) {
				if ("VIEW".equals(tableComment)) {
					// 跳过视图
					continue;
				}
				String tableName = results.getString(dbQuery.tableName());

				if (StringUtil.isNotEmpty(tableName)) {
					// 把前缀不匹配，但是不在include中的都去掉
					isadd = true;

					if (tablenames == null || tablenames.length > 0) {

						if (isTablePrefix) {
							isadd = false;
							for (String str : tablePrefix) {
								if (tableName.toLowerCase().startsWith(str.toLowerCase())) {
									isadd = true;
									break;
								}
							}
						}

						if (!isadd && isInclude) {
							for (String includeTab : include) {
								if (includeTab.equalsIgnoreCase(tableName)) {
									isadd = true;
									break;
								}
							}
						}
						if (isadd && isExclude) {
							for (String excludeTab : exclude) {
								if (excludeTab.equalsIgnoreCase(tableName)) {
									isadd = false;
									break;
								}
							}
						}
					}

					if (!isadd) {
						continue;
					}

					tableInfo = new EntityTable();
					tableInfo.setEntityTableName(tableName);
					tableInfo.setComment(tableComment);

					tableList.add(tableInfo);
				} else {
					System.err.println("当前数据库为空！！！");
				}
			}
//			// 将已经存在的表移除，获取配置中数据库不存在的表
//			for (EntityTable tabInfo : tableList) {
//				notExistTables.remove(tabInfo.getEntityTableName());
//			}
//
//			if (notExistTables.size() > 0) {
//				System.err.println("表 " + notExistTables + " 在数据库中不存在！！！");
//			}
//			
//
//			// 需要反向生成的表信息
//			if (isExclude) {
//				tableList.removeAll(excludeTableList);
//				includeTableList = tableList;
//			}
//			if (!isInclude && !isExclude) {
//				includeTableList = tableList;
//			}

			for (EntityTable ti : tableList) {
				this.assignTableFields(ti);

				

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 释放资源
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return tableList;
	}
	
	public void convertTable2Class(String basepackage,EntityTable ti) {
		String tablename = ti.getEntityTableName();
		// 去掉前缀
		for (String str : tablePrefix) {
			if (tablename.toLowerCase().startsWith(str.toLowerCase())) {
				ti.setBasepackage(basepackage);
				ti.convertTableToClass(basepackage, tablename.substring(str.length()));
				break;
			}
		}
	}

	/**
	 * 把列名进行转换，根据命名策略进行转换
	 * 
	 * @param et
	 * @param nameStrategy
	 */
	public void assignTableFields(EntityTable tableInfo) {// nameStrategy
		// 判断是否是复核主键
		Set<PropertyColumn> ids = new HashSet<PropertyColumn>();

		List<PropertyColumn> fieldList = new ArrayList<>();
//        List<PropertyColumn> commonFieldList = new ArrayList<>();
		try {
			String tableFieldsSql = dbQuery.tableFieldsSql();
			if (DBAlias.postgresql == dbQuery.dbType()) {
				tableFieldsSql = String.format(tableFieldsSql, db_schemaname, tableInfo.getEntityTableName());
			} else {
				tableFieldsSql = String.format(tableFieldsSql, tableInfo.getEntityTableName());
			}
			PreparedStatement preparedStatement = connection.prepareStatement(tableFieldsSql);
			ResultSet results = preparedStatement.executeQuery();
			while (results.next()) {
				PropertyColumn field = new PropertyColumn();
				// 处理其它信息
				field.setColumn(results.getString(dbQuery.fieldName()));
				field.setComment(results.getString(dbQuery.fieldComment()));

				field.setDefaultValue(results.getString(dbQuery.defaultKey()));
				field.setNullable("YES".equals(results.getString(dbQuery.nullableKey())));

				String key = results.getString(dbQuery.fieldKey());

				boolean isId = StringUtil.isNotEmpty(key) && key.toUpperCase().equals("PRI");
				// 处理ID
				if (isId) {
					field.setIsId(true);

					tableInfo.addIdColumn(field.getColumn());

					ids.add(field);
				} else {
					field.setIsId(false);
				}

				if (DBAlias.mysql == dbQuery.dbType()) {
					if ("UNI".equals(key)) {
						field.setUnique(true);
					}
				}
				

				field.setProperty(processName(field.getColumn()));

				AbstractDialect dialect = dbQuery.dbType().getDialect();
				DbColumn dbColumn = dialect.columnTypeToProertyType(results.getString(dbQuery.fieldType()));
				field.setColumnType(dbColumn.getColumnType());
				field.setLength(dbColumn.getLength());
				field.setPrecision(dbColumn.getPrecision());
				field.setScale(dbColumn.getScale());
				field.setClazz(dbColumn.getJavaType());
				
				
				// 如果大于1，就设置为复核主键
				if (ids.size() > 1) {
					for (PropertyColumn id : ids) {
						id.setIsCompositeId(true);
						id.setIdGenEnum(IDGenEnum.none);
					}
					tableInfo.setIsCompositeId(true);

				} else {
					if (dbQuery.isKeyIdentity(results)) {
						field.setIdGenEnum(IDGenEnum.identity);
						tableInfo.setIdGenEnum(IDGenEnum.identity);
					} else {
						//如果是string类型，并且长度大于36就设置为uuid
						if(field.getClazz().isAssignableFrom(String.class) && field.getLength()>=36) {
							field.setIdGenEnum(IDGenEnum.uuid);
							tableInfo.setIdGenEnum(IDGenEnum.uuid);
						} else {
							field.setIdGenEnum(IDGenEnum.none);
							tableInfo.setIdGenEnum(IDGenEnum.none);
						}
					}
				}
				
				if(logicdelete!=null) {
					for(String ld:logicdelete) {
						if(ld.equalsIgnoreCase(field.getColumn())) {
							field.setIsLogicDelete(true);
							break;
						}
					}
				}
				if(version!=null) {
					for(String ld:version) {
						if(ld.equalsIgnoreCase(field.getColumn())) {
							field.setIsVersion(true);
							break;
						}
					}
				}
				

//                // 自定义字段查询
//                String[] fcs = dbQuery.fieldCustom();
//                if (null != fcs) {
//                    Map<String, Object> customMap = new HashMap<>();
//                    for (String fc : fcs) {
//                        customMap.put(fc, results.getObject(fc));
//                    }
//                    field.setCustomMap(customMap);
//                }

//                field.setPropertyName(strategyConfig, processName(field.getName(), strategy));
//                field.setColumnType(dataSourceConfig.getTypeConvert().processTypeConvert(field.getType()));

//                if (strategyConfig.includeSuperEntityColumns(field.getName())) {
//                    // 跳过公共字段
//                    commonFieldList.add(field);
//                    continue;
//                }
//                // 填充逻辑判断
//                List<TableFill> tableFillList = this.getStrategyConfig().getTableFillList();
//                if (null != tableFillList) {
//                    for (TableFill tableFill : tableFillList) {
//                        if (tableFill.getFieldName().equals(field.getName())) {
//                            field.setFill(tableFill.getFieldFill().name());
//                            break;
//                        }
//                    }
//                }
				fieldList.add(field);
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception：" + e.getMessage());
		}
		tableInfo.setPropertyColumns(fieldList);
		// tableInfo.setCommonFields(commonFieldList);
		// return tableInfo;
	}

	private String processName(String columnName) {
		// columnPrefix
		boolean removePrefix = false;
		if (columnPrefix != null && columnPrefix.length >= 1) {
			removePrefix = true;
		}
		String propertyName;
		if (removePrefix) {
			propertyName = nameStrategy.columnNameToProperty(columnName, columnPrefix);
		} else {
			// 不处理
			propertyName = columnName;
		}
		return propertyName;
	}

}
