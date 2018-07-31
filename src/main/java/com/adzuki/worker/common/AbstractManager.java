package com.adzuki.worker.common;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import tk.mybatis.mapper.entity.Condition;

public abstract class AbstractManager<T>  {
	

    private static final Logger logger = LoggerFactory.getLogger(AbstractManager.class);

    @Autowired(required=false)
    protected CoreMapper<T> mapper;

    private Class<T> modelClass;    // 当前泛型真实类型的Class

    @SuppressWarnings("unchecked")
	public AbstractManager() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        modelClass = (Class<T>) pt.getActualTypeArguments()[0];
    }

    public int save(T model) {
        return mapper.insertSelective(model);
    }

    public int save(List<T> models) {
        return mapper.insertList(models);
    }

    public int deleteById(Integer id) {
        return mapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public void deleteByIds(String ids) {
        Stream.of(ids.split(",")).map(Integer::valueOf).forEach(this::deleteById);
    }
    
    public int deleteLogicById(Integer id) throws Exception {
        try {
            T model = modelClass.newInstance();
            Field field = modelClass.getDeclaredField("id");
            field.setAccessible(true);
            field.set(model, id);//id
            field = modelClass.getDeclaredField("logicState");
            field.setAccessible(true);
            field.set(model, false);//logicState
            return mapper.updateByPrimaryKeySelective(model);
        } catch (ReflectiveOperationException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    public int update(T model) {
    	try {
	        Field field = modelClass.getDeclaredField("updateTime");
	        field.setAccessible(true);
	        field.set(model, new Date());
    	} catch(Exception e) {}
        return mapper.updateByPrimaryKeySelective(model);
    }
    
    public int updateSelective(T model) {
    	try {
	        Field field = modelClass.getDeclaredField("updateTime");
	        field.setAccessible(true);
	        field.set(model, new Date());
    	} catch(Exception e) {}
        return mapper.updateByPrimaryKeySelective(model);
    }

    public T findById(Integer id) {
        return mapper.selectByPrimaryKey(id);
    }

    public T findBy(String fieldName, Object value) throws Exception {
        try {
            T model = modelClass.newInstance();
            Field field = modelClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(model, value);
            return mapper.selectOne(model);
        } catch (ReflectiveOperationException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    public List<T> findByIds(String ids) {
        return mapper.selectByIds(ids);
    }

    public List<T> findByCondition(Condition condition) {
        return mapper.selectByCondition(condition);
    }

    public List<T> findAll() {
        return mapper.selectAll();
    }

}
