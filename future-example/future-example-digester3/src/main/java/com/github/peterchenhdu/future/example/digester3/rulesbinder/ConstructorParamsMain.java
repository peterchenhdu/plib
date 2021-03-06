
/*
 * Copyright (c) 2011-2025 PiChen
 */

package com.github.peterchenhdu.future.example.digester3.rulesbinder;

import java.io.IOException;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.ObjectCreateRule;
import org.xml.sax.SAXException;

import com.github.peterchenhdu.future.example.digester3.rulesbinder.pojo.MyBean;

/**
 * @author chenpi
 * @version 2017年6月5日
 */
public class ConstructorParamsMain {

    public static void main(String[] args) {
        try {

            ObjectCreateRule createRule = new ObjectCreateRule(MyBean.class);
            createRule.setConstructorArgumentTypes(Double.class, Boolean.class);

            Digester digester = new Digester();
            digester.addRule("root/bean", createRule);
            digester.addCallParam("root/bean", 1, "super");
            digester.addCallParam("root/bean/rate", 0);

            MyBean myBean = digester.parse(ConstructorParamsMain.class.getClassLoader()
                    .getResourceAsStream("constructor-params.xml"));

            System.out.println(myBean.getRate());
            System.out.println(myBean.isSuper_());

        } catch (IOException e) {

            e.printStackTrace();
        } catch (SAXException e) {

            e.printStackTrace();
        }
    }
}
