package test.mawujun.repository;

import org.apache.ibatis.annotations.Mapper;

import com.mawujun.repository.mybatis.IRepository;

import test.mawujun.model.City;

@Mapper
public interface CityMapper extends IRepository<City,String>{


}
