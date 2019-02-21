package com.java.entity;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.lang.reflect.Field;
import java.util.List;

public class ClassPathXmlApplicationContext {

    private static String PATH;
    private static String ID;
    private static String CLASS;
    private static String NAME;
    private static String VALUE;

    public void init() {
        this.ID = "id";
        this.CLASS = "class";
        this.NAME = "name";
        this.VALUE = "value";


    }

    public ClassPathXmlApplicationContext(String path) {
        init();
        this.PATH = path;
    }

    public Object getBean(String beanId) throws Exception {

        if (StringUtils.isEmpty(beanId)) {
            return null;
        }

        // 1.XML解析
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(this.getClass().getClassLoader().getResourceAsStream(PATH));

        Element rootElement = document.getRootElement();
        List<Element> elements = rootElement.elements();
        for (Element element : elements) {
            String id = element.attributeValue(ID);
            if (!beanId.equals(id)) {
                continue;
            }
            String attClass = element.attributeValue(CLASS);
            Class<?> forName = Class.forName(attClass);
            Object newInstance = forName.newInstance();
            List<Element> filedEle = element.elements();
            for (Element fileds : filedEle) {
                String name = fileds.attributeValue(NAME);
                String value = fileds.attributeValue(VALUE);
                Field declaredField = forName.getDeclaredField(name);
                declaredField.setAccessible(true);
                declaredField.set(newInstance, value);
            }
            return  newInstance;
        }
        return  null;
    }

    public static void main(String[] args) throws Exception{
        ClassPathXmlApplicationContext ac =  new ClassPathXmlApplicationContext("applicationContext.xml");

        User user1 =(User)ac.getBean("user1");
        System.out.println(user1);
        User user2 =(User)ac.getBean("user2");
        System.out.println(user2);
    }


}
