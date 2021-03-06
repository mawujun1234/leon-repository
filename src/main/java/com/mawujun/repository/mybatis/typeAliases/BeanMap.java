package com.mawujun.repository.mybatis.typeAliases;

import java.util.Date;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.ibatis.type.Alias;

import com.mawujun.convert.Convert;

/**
 * 不区分大小写,主要可用于mybatis
 * 同时有类型转换
 * @author Administrator
 *
 */
@Alias("beanmap")
//public class BeanMap implements Map<String, Object>, Serializable{
public class BeanMap extends CaseInsensitiveMap{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8219979051333472569L;

	public Date getDate(String var1) {
		Object var2 = this.get(var1);
		return Convert.convert(Date.class,var2);
	}

//	public String getDateString(String var1) {
//		Date var2 = this.getDate(var1);
//		String var3 = "";
//		if (var2 != null) {
//			var3 = DateUtils.date2String(var2);
//		}
//
//		return var3;
//	}

	public double getdouble(String var1) {
		Object var2 = this.get(var1);
		return var2 == null ? 0.0D : str2double(var2);
	}

	public Double getDouble(String var1) {
		Object var2 = this.get(var1);
		return var2 == null ? null : str2double(var2);
	}

	public float getfloat(String var1) {
		Object var2 = this.get(var1);
		return var2 == null ? 0.0F : str2float(var2);
	}

	public Float getFloat(String var1) {
		Object var2 = this.get(var1);
		return var2 == null ? null : str2float(var2);
	}

	public int getInt(String var1) {
		Object var2 = this.get(var1);
		return var2 == null ? 0 : str2int(var2);
	}

	public Integer getInteger(String var1, int defaultValue) {
		Integer var3 = this.getInteger(var1);
		return var3 == null ? defaultValue : var3;
	}

	public Integer getInteger(String var1) {
		Integer var2 = null;
		Object var3 = this.get(var1);
		if (var3 != null) {
			var2 = str2int(var3);
		}

		return var2;
	}

	public long getlong(String var1) {
		Object var2 = this.get(var1);
		return var2 == null ? 0L : str2long(var2);
	}

	public Long getLong(String var1) {
		Object var2 = this.get(var1);
		return var2 == null ? null : str2long(var2);
	}
	
	public String getString(String var1) {
		Object var2 = this.get(var1);
		return var2 != null ? var2.toString() : "";
	}
	
	public Boolean getBoolean(String var1) {
		return Convert.convert(Boolean.class,var1);
//		boolean var2 = false;
//		Object var3 = this.get(var1);
//		if (var3 == null) {
//			return var2;
//		} else {
//			if (var3 instanceof Boolean) {
//				var2 = (Boolean) var3;
//			} else {
//				String var4 = null2string(var3);
//				var2 = var4.equalsIgnoreCase("true") || var4.equalsIgnoreCase("y") || var4.equalsIgnoreCase("1");
//			}
//
//			return var2;
//		}
	}

	public boolean getboolean(String var1) {
		return this.getBoolean(var1);
	}
	//===================================================================================================
	public static boolean isNumber(Object var0) {
		boolean var1 = false;
		return var0 == null ? var1 : var0.toString().trim().matches("^-?\\d+[\\.\\d]*$");
	}
	public static boolean isEmpty(Object var0) {
		return var0 == null || var0.toString().trim().length() == 0;
	}
	public static boolean isEmpty(Object var0, boolean var1) {
		if (var1) {
			if (var0 == null) {
				return true;
			} else {
				String var2 = var0.toString().trim();
				return var2.length() == 0 || var2.equals("\"\"") || var2.equals("''") || var2.equals("null")
						|| var2.equals("undefined");
			}
		} else {
			return var0 == null || var0.toString().trim().length() == 0;
		}
	}
	public static double str2double(Object var0) {
		return Convert.convert( Double.class,var0);
	}
//	public static double str2double(Object var0, double var1) {
//		double var3 = var1;
//		if (var0 == null) {
//			return var1;
//		} else if (var0 instanceof Number) {
//			return ((Number) var0).doubleValue();
//		} else {
//			try {
//				if (!isNumber(var0)) {
//					return var3;
//				}
//
//				var3 = Double.parseDouble(var0.toString().trim());
//			} catch (Exception e) {
//				//_$14.warn("转换{" + var0.toString() + "}为double时异常!");
//				e.printStackTrace();
//				var3 = var1;
//			}
//
//			return var3;
//		}
//	}
	
	public static float str2float(Object var0) {
		return str2float(var0, 0.0F);
	}

	public static float str2float(Object var0, float var1) {
		return Convert.convert(float.class,var0);
//		if (var0 == null) {
//			return var1;
//		} else if (var0 instanceof Number) {
//			return ((Number) var0).floatValue();
//		} else {
//			float var2;
//			try {
//				var2 = Float.parseFloat(var0.toString());
//			} catch (Exception e) {
//				//_$14.warn("转换{" + var0.toString() + "}为float时异常!");
//				e.printStackTrace();
//				var2 = var1;
//			}
//
//			return var2;
//		}
	}
	public static int str2int(Object var0) {
		return object2int(var0, 0);
	}

	public static int str2int(Object var0, int var1) {
		return object2int(var0, var1);
	}

	public static int object2int(Object var0, int var1) {
		return Convert.convert( int.class,var0);
//		if (isEmpty(var0, true)) {
//			return var1;
//		} else {
//			int var2;
//			try {
//				if (var0 instanceof Number) {
//					return ((Number) var0).intValue();
//				}
//
//				if (isNumber(var0)) {
//					var2 = (int) str2double(var0);
//				} else {
//					var2 = Integer.parseInt(var0.toString().trim());
//				}
//			} catch (Exception e) {
//				//String var4 = "object2int:转换obj={" + var0 + "}时异常!";
//				e.printStackTrace();
//
//				var2 = var1;
//			}
//
//			return var2;
//		}
	}
	
	public static long str2long(Object var0) {
		return object2long(var0, 0L);
	}
	public static long object2long(Object var0, long var1) {
		return Convert.convert( long.class,var0);
//		if (isEmpty(var0, true)) {
//			return var1;
//		} else {
//			long var3;
//			if (var0 instanceof Number) {
//				var3 = Long.parseLong(var0.toString());
//			} else {
//				try {
//					if (isNumber(var0)) {
//						var3 = (long) str2double(var0);
//					} else {
//						var3 = Long.parseLong(var0.toString().trim());
//					}
//				} catch (Exception e) {
//					//String var6 = "object2long(" + var0 + ")转换为long型异常!";
//					System.out.println(e);
//
//					var3 = var1;
//				}
//			}
//
//			return var3;
//		}
	}
	
	public static String null2string(Object var0) {
		return var0 == null ? "" : var0.toString();
	}

	public static String null2string(Object var0, String var1) {
		return isEmpty(var0) ? var1 : var0.toString();
	}
}
