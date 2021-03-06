package com.mawujun.repository.utils;

/**
 * 数据库常用操作符
 * @author mawujun 16064988
 *
 */
public enum OpEnum {
	eq,eq_i,noteq,noteq_i,gt,ge,lt,le,between,
	in,notin,
	like,likeprefix,likesuffix,like_i,likeprefix_i,likesuffix_i,
	notlike,notlikeprefix,notlikesuffix,
	isnull,isnotnull;
}
