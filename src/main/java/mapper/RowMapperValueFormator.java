package mapper;

import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Chaminda Wijayasundara on 11 Jan 2018
 */

public enum RowMapperValueFormator implements ValueFormatorIntferface {

    DATE(Types.DATE) {
        @Override
        public Object formatThisValue(Object value, String format) {
            String[] tokens = format.split("#");
            String timezone = tokens.length == 1 ? System.getProperty("user.timezone") : tokens[1];
            Date date = (Date) value;
            return formatDate(date , tokens[0] , timezone );
        }
    },
    TIMESTAMP(Types.TIMESTAMP) {
        @Override
        public Object formatThisValue(Object value, String format) {

            if (value != null){
                String[] tokens = format.split("#");
                String timezone = tokens.length == 1 ? System.getProperty("user.timezone") : tokens[1];
                Timestamp date = (Timestamp) value;
                return formatDate(new Date(date.getTime()) , tokens[0] , timezone);
            }
            return null;
        }
    },

    STRING(Types.VARCHAR) {
        @Override
        public Object formatThisValue(Object value, String format) {

            String newValue = "";

            if (format.contains("#")){
                // Gets the digit enclosed by specified characters.
                String [] separator = format.split("#");
                String strValue = (String) value;

                if (strValue.contains(separator[0]) && strValue.contains(separator[1]))
                    newValue = StringUtils.substringBetween(strValue, separator[0], separator[1]);
                else
                    newValue = strValue;
            }

            return newValue;
        }

    };

    private static final Map<Integer, RowMapperValueFormator> formatorMap = new HashMap<Integer, RowMapperValueFormator>();

    static {

        formatorMap.put(Types.DATE, RowMapperValueFormator.DATE);
        formatorMap.put(Types.TIMESTAMP, RowMapperValueFormator.TIMESTAMP);
        formatorMap.put(Types.VARCHAR, RowMapperValueFormator.STRING);
    }


    public static RowMapperValueFormator getFormatorByClass(int typeValue) {
        RowMapperValueFormator formator = formatorMap.get(typeValue);
        if (formator == null) {
            throw new RuntimeException("Type not supported");
        }
        return formator;
    }

    int typeValue;

    private RowMapperValueFormator(int classType) {
        this.typeValue = classType;
    }

    public int getClassType() {
        return typeValue;
    }

    public void setClassType(int classType) {
        this.typeValue = classType;
    }

    @Override
    public Object formatThisValue(Object value, String format) {
        return null;
    }

    private static String formatDate(Date inputDate , String format , String timezone){
        final DateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        return dateFormat.format(inputDate);
    }

}