package mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chaminda Wijayasundara on 11 Jan 2018
 */

public class BaseRowMapper <T> implements RowMapper<T>, InitializingBean {

    /**
     * Property to create logger information.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** The class we are mapping to */
    private Class<T> mappedClass;

    private String fields;

    public Map<String, String> fieldsWithFormators = new HashMap<String, String>();

    private String[] fieldArray;

    private String feedDelimiter;
    private String escapedFeedDelimiter;
    private Boolean handleEscpadeCharaters = true;
    private Boolean isEscpaCharAdded = true;

    @Override
    public T mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Assert.state(this.mappedClass != null, "Mapped class was not specified");
        T mappedObject = BeanUtils.instantiate(this.mappedClass);
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);

        String fieldName = null;
        Object value = null;
        int columnIdx = 0;

        for (int fIndex = 0; fIndex < fieldArray.length; fIndex++) {
            fieldName = fieldArray[fIndex].trim();
            if (!StringUtils.hasText(fieldName))
                continue;

            columnIdx = fIndex + 1;
            value = JdbcUtils.getResultSetValue(resultSet, columnIdx);
            value = getFormatedValue(resultSet, fieldName, value, columnIdx);
            try {
                beanWrapper.setPropertyValue(fieldName, value);
            } catch (TypeMismatchException e) {
                if (value == null) {
                    logger.debug("Intercepted TypeMismatchException for row "
                            + rowNum + " and column index'" + fIndex
                            + "' with value " + value
                            + " when setting property '" + fieldName
                            + " on object: " + mappedObject);
                } else {
                    throw e;
                }
            }
        }
        return mappedObject;
    }

    private Object getFormatedValue(ResultSet resultSet, String fieldName,Object value, int columnIdx) throws SQLException {
        if (value != null && fieldsWithFormators.containsKey(fieldName)) {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            String format = fieldsWithFormators.get(fieldName);
            value = RowMapperValueFormator.getFormatorByClass(
                    resultSetMetaData.getColumnType(columnIdx)).formatThisValue(value,format);
        }

        if (isEscpaCharAdded){
            return handleEscapeCharacters( value);
        }
        else
            return value;
    }

    private Object handleEscapeCharacters(Object obj) {
        Object resultObject = obj;
        if(obj != null && obj.getClass().equals(String.class)) {
            String resultValue = EscapeCharacterUtil.handleEscape(obj.toString());
            if(StringUtils.hasText(feedDelimiter)){
                resultValue = resultValue.replace(feedDelimiter , escapedFeedDelimiter );
                resultValue = resultValue.trim();
            }
            resultObject = resultValue;
        }
        return resultObject;
    }

    public void setMappedClass(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public Boolean getHandleEscpadeCharaters() {
        return handleEscpadeCharaters;
    }

    public void setHandleEscpadeCharaters(Boolean handleEscpadeCharaters) {
        this.handleEscpadeCharaters = handleEscpadeCharaters;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String[] tempFieldArray = fields.split(",");
        fieldArray = new String[tempFieldArray.length];
        String key = null, value = null;
        String[] pair = null;
        int idx = 0;
        for (String field : tempFieldArray) {
            if (field.indexOf('=') != -1) {
                pair = field.split("=");
                key = pair[0];
                value = pair[1];
                fieldsWithFormators.put(key, value);
            }
            fieldArray[idx++] = field.indexOf('=') != -1 ? key : field;
        }
        escapedFeedDelimiter = "\\"+ feedDelimiter;
    }

    public void setFeedDelimiter(String feedDelimiter) {
        this.feedDelimiter = feedDelimiter;
    }

    public void setIsEscpaCharAdded(Boolean isEscpaCharAdded) {
        this.isEscpaCharAdded = isEscpaCharAdded;
    }
}
