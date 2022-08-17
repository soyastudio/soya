package soya.framework.action.actions.document.csv;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CSVDynaClass implements DynaClass, Serializable {
    private final String name;
    protected Map<String, DynaProperty> propertiesMap = new LinkedHashMap<>();
    protected DynaProperty[] properties;

    protected List<DynaBean> beans = new ArrayList<>();

    protected CSVDynaClass(String name) {
        this.name = name;
    }

    public CSVDynaClass(String name, byte[] data){
        this.name = name;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream));

            String[] cols = csvReader.readNext();
            while (cols != null) {
                if (propertiesMap.isEmpty()) {
                    for (String col : cols) {
                        String token = col.trim();
                        propertiesMap.put(token, new DynaProperty(token, String.class));
                    }

                    properties = propertiesMap.values().toArray(new DynaProperty[propertiesMap.size()]);

                } else {
                    DynaBean bean = newInstance();

                    int size = Math.min(properties.length, cols.length);

                    for (int i = 0; i < size; i++) {
                        String value = cols[i];
                        bean.set(properties[i].getName(), value);
                    }
                    beans.add(bean);
                }

                cols = csvReader.readNext();
            }
        } catch (IOException | CsvValidationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DynaProperty getDynaProperty(String s) {
        return propertiesMap.get(s);
    }

    @Override
    public DynaProperty[] getDynaProperties() {
        return this.properties;
    }

    @Override
    public DynaBean newInstance() {
        return new BasicDynaBean(this);
    }

    public CSVDynaClass refine(String[] labels)  {
        CSVDynaClass csvDynaClass = new CSVDynaClass(name);
        int len = Math.min(properties.length, labels.length);
        for(int i = 0; i < len; i ++) {
            String col = properties[i].getName();
            String lab = labels[i];
            csvDynaClass.propertiesMap.put(lab, new DynaProperty(lab, String.class));
        }
        csvDynaClass.properties = csvDynaClass.propertiesMap.values().toArray(new DynaProperty[len]);

        beans.forEach(e -> {
            DynaBean bean = csvDynaClass.newInstance();
            for(int i = 0; i < len; i ++) {
                String col = properties[i].getName();
                String lab = labels[i];

                String value = (String) e.get(col);
                bean.set(lab, value);
            }
            csvDynaClass.beans.add(bean);
        });

        return csvDynaClass;
    }

    public void include(Filter filter) {
        List<DynaBean> list = new ArrayList<>();
        beans.forEach(e -> {
            if(filter.match(e)) {
                list.add(e);

            }
        });

        beans = list;
    }

    public void exclude(Filter filter) {
        List<DynaBean> list = new ArrayList<>();
        beans.forEach(e -> {
            if(!filter.match(e)) {
                list.add(e);

            }
        });

        beans = list;
    }

    public String toCSV() {
        StringBuilder builder = new StringBuilder();
        for(DynaProperty property: properties) {
            builder.append("\"").append(property.getName()).append("\"").append(",");
        }
        builder.deleteCharAt(builder.length() - 1).append("\n");

        beans.forEach(e -> {
            for(DynaProperty property: properties) {
                String value = (String)e.get(property.getName());
                if(value == null) {
                    value = "";
                }

                builder.append("\"").append(value).append("\"").append(",");
            }

            builder.deleteCharAt(builder.length() -1 ).append("\n");
        });

        return builder.toString();
    }


}
