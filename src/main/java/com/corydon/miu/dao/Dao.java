package com.corydon.miu.dao;

import java.util.List;
import java.util.Map;

public abstract class Dao<T> {

    public abstract T add(T t);

    public abstract void update(Map<String,String> tp,Map<String,String> cp);

    public abstract void delete(Map<String,String> params);

    public abstract List<T> find(Map<String,String> params);

    public String generateCondition(Map<String,String> params){
        StringBuilder condition=new StringBuilder();
        if(params!=null&&params.size()!=0){
            condition.append(" WHERE ");
            for(Map.Entry<String,String> entry:params.entrySet()){
                condition.append(entry.getKey()+"="+"\'"+entry.getValue()+"\'"+" and ");
            }
            condition.delete(condition.lastIndexOf("a"),condition.length()-1);
            condition.append(";");
        }
        return condition.toString();
    }

    public String generateTarget(Map<String,String> params){
        StringBuilder target=new StringBuilder();
        if(params!=null&&params.size()!=0){
            target.append(" SET ");
            for(Map.Entry<String,String> entry:params.entrySet()){
                target.append(entry.getKey()+"="+"\'"+entry.getValue()+"\'"+",");
            }
            target.deleteCharAt(target.length()-1);
        }
        return target.toString();
    }


}
