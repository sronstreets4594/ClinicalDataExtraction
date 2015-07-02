package com.care.platform;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.care.datatype.Component;
import com.care.datatype.ComponentType;

/**
 * Created by AMIT on 8/10/14.
 */
public class PlatformManagerTest
{
    @Test
    public void testStartComponent()
    {
        Component component = new Component();
        component.setType(ComponentType.PRE_PROCESSOR);
        component.setPath("./bin/resources");
        component.setClassName("resources.PreProcessor");

        String inputContent = "Hello";
        
        String outputPath="";

        try
        {
            PlatformManager manager = new PlatformManager();
            manager.InitializeClassComponent(component);
            List<String> output = null;
            output = manager.DoWork(inputContent,outputPath);

            List<String> expected = new ArrayList<String>(1);
            expected.add(inputContent);
            Assert.assertEquals(expected, output);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
