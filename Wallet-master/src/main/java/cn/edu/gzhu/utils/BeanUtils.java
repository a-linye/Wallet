package cn.edu.gzhu.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther: linYe
 * @Date: 2020/4/30 15:10
 * @Description: 对象快捷操作工具类
 * @Version: 1.0
 */
public class BeanUtils {
    /**
     * 将两个JavaBean里相同的字段自动填充
     * @param obj 原JavaBean对象
     * @param toObj 将要填充的对象
     */
    public static void autoFillEqFields(Object obj, Object toObj) {
        try {
            Map<String, Method> getMaps = new HashMap<>();
            Method[] sourceMethods = obj.getClass().getMethods();
            for (Method m : sourceMethods) {
                if (m.getName().startsWith("get")) {
                    getMaps.put(m.getName(), m);
                }
            }

            Method[] targetMethods = toObj.getClass().getMethods();
            for (Method m : targetMethods) {
                if (!m.getName().startsWith("set")) {
                    continue;
                }
                String key = "g" + m.getName().substring(1);
                Method getm = getMaps.get(key);
                if (null == getm) {
                    continue;
                }
                Class<?> returnType = getm.getReturnType();
                Class<?>[] parameterTypes = m.getParameterTypes();
                if(parameterTypes.length>0 && parameterTypes[0].getName().equals(returnType.getName())) {
                    // 写入方法写入
                    m.invoke(toObj, getm.invoke(obj));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 转Map转换至对象
     * @param map
     * @param toObj
     */
    public static void mapToObject(Map<String, Object> map, Object toObj) {
        try{
            Map<String, Method> setMaps = new HashMap<>();
            Method[] sourceMethods = toObj.getClass().getMethods();
            for (Method m : sourceMethods) {
                if (m.getName().startsWith("set")) {
                    setMaps.put(m.getName().substring(3).toLowerCase(), m);
                }
            }
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String k = entry.getKey();
                Object v = entry.getValue();
                if (v != null) {
                    String valueStr=v.toString();
                    Method m = setMaps.get(k.toLowerCase());
                    if(m == null){
                        continue;
                    }
                    Class<?>[] parameterTypes = m.getParameterTypes();
                    String typeName = parameterTypes[0].getName();
                    switch (typeName){
                        case "java.util.Date":
                            SimpleDateFormat dateFormat =null;
                            String dataStr=valueStr;
                            switch (dataStr.length()){
                                case 19:
                                    dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    break;
                                case 16:
                                    dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    break;
                                case 13:
                                    dateFormat=new SimpleDateFormat("yyyy-MM-dd HH");
                                    break;
                                case 10:
                                    dateFormat=new SimpleDateFormat("yyyy-MM-dd");
                                    break;
                                default:
                                    String[] tempList=dataStr.split("T");
                                    dataStr=tempList[0]+" "+tempList[1].substring(0,tempList[1].indexOf("."));
                                    dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            }
                            Date date = dateFormat.parse(dataStr);
                            m.invoke(toObj, date);
                            break;
                        case "java.lang.Double":
                            m.invoke(toObj, Double.parseDouble(valueStr));break;
                        case "java.lang.Integer":
                            m.invoke(toObj, Integer.parseInt(valueStr));break;
                        case "java.lang.Long":
                            m.invoke(toObj, Long.parseLong(valueStr));break;
                        case "java.lang.Float":
                            m.invoke(toObj, Float.parseFloat(valueStr));break;
                        case "java.lang.Byte":
                            m.invoke(toObj, Byte.parseByte(valueStr));break;
                        case "java.lang.String":
                            m.invoke(toObj, valueStr);break;
                    }
                }
            }
        }catch (Exception e){
            e.getMessage();
        }
    }


    /**
     * 将对象转至Map
     * @param obj 目标对象
     * @param t Map指定泛型
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> objectToMap(Object obj,Class<T> t){
        Map<String, T> map = new HashMap<>();
        if(obj == null){
            return map;
        }
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            T value = null;
            try {
                value = (T)field.get(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if(value != null){
                map.put(fieldName, value);
            }
        }
        return map;
    }

    /**
     * JSON转对象
     * @param json
     * @param responseClass
     * @param <T>
     * @return
     */
    public static <T> T jsonToObject(String json,Class<T> responseClass){
        if(StringUtils.isEmpty(json) || json.equals("null")){
            return null;
        }
        Map dataMap;
        if(json.startsWith("[")){
            List<Map> dataMaps = JSONObject.parseArray(json, Map.class);
            dataMap=dataMaps.get(0);
        }else{
            dataMap= JSONObject.parseObject(json, Map.class);
        }
        T data =null;
        try {
            data = responseClass.newInstance();
            mapToObject(dataMap, data);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * JSON转对象集合实体
     * @param json
     * @param responseClass
     * @param <T>
     * @return
     */
    public static  <T> List<T> jsonToListObject(String json, Class<T> responseClass){
        if(StringUtils.isEmpty(json) || json.equals("null")){
            return null;
        }
        List<Map> dataMaps = JSONObject.parseArray(json, Map.class);
        List<T> datas =new LinkedList<>();
        try {
            if (dataMaps !=null){
                for (Map dataMap : dataMaps) {
                    T data = responseClass.newInstance();
                    mapToObject(dataMap, data);
                    datas.add(data);
                }
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return datas;
    }

    /**
     * 设置时间格式的转换
     * @param jsonStr
     * @param typeOfT
     * @param dateTimeFormat 时间格式，例：yyyy-MM-dd
     * @param <T>
     * @return
     * @throws IOException
     * @throws JsonSyntaxException
     */
    public static <T> T convertJson(String jsonStr, Type typeOfT, String dateTimeFormat)
            throws IOException, JsonSyntaxException {
        Gson gson = new GsonBuilder().setDateFormat(dateTimeFormat).create();
        return (T) gson.fromJson(jsonStr, typeOfT);
    }

    public static <T> T convertJson(String jsonStr, Type typeOfT)
            throws IOException, JsonSyntaxException {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return (T) gson.fromJson(jsonStr, typeOfT);
    }

    public static Gson getGson() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss") // 设置日期转换
                .create();
        return gson;
    }
}
