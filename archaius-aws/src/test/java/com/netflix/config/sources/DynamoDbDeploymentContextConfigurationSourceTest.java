package com.netflix.config.sources;

import com.netflix.config.ConfigurationManager;
import com.netflix.config.DeploymentContext;
import com.netflix.config.PollResult;
import com.netflix.config.PropertyWithDeploymentContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * User: gorzell
 * Date: 1/17/13
 * Time: 10:18 AM
 * You should write something useful here.
 */
public class DynamoDbDeploymentContextConfigurationSourceTest {
    private static Map<String, Object> propMap1;
    private static Map<String, Object> propMap2;
    private static Collection<PropertyWithDeploymentContext> propCollection1;
    private static Collection<PropertyWithDeploymentContext> propCollection2;

    @BeforeClass
    public static void setUpClass() throws Exception {
        //Setup first map
        propMap1 = new HashMap<String, Object>();
        propMap1.put("foo", "bar");
        propMap1.put("goo", "foo");
        propMap1.put("boo", "who");

        //Setup second map
        propMap2 = new HashMap<String, Object>();
        propMap2.put("foo", "bar");
        propMap2.put("goo", "goo");
        propMap2.put("boo", "who");

        propCollection1 = new LinkedList<PropertyWithDeploymentContext>();
        propCollection1.add(new PropertyWithDeploymentContext(DeploymentContext.ContextKey.environment, "test", "foo", "bar"));
        propCollection1.add(new PropertyWithDeploymentContext(DeploymentContext.ContextKey.environment, "test", "goo", "goo"));
        propCollection1.add(new PropertyWithDeploymentContext(DeploymentContext.ContextKey.environment, "test", "boo", "who"));

        propCollection2 = new LinkedList<PropertyWithDeploymentContext>();
        propCollection2.add(new PropertyWithDeploymentContext(DeploymentContext.ContextKey.environment, "test", "foo", "bar"));
        propCollection2.add(new PropertyWithDeploymentContext(DeploymentContext.ContextKey.environment, "test", "goo", "boo"));
        propCollection2.add(new PropertyWithDeploymentContext(DeploymentContext.ContextKey.environment, "prod", "goo", "foo"));
        propCollection2.add(new PropertyWithDeploymentContext(DeploymentContext.ContextKey.environment, "test", "boo", "who"));

        ConfigurationManager.getConfigInstance().setProperty("@environment", "test");

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testPoll() throws Exception {
        DynamoDbDeploymentContextTableCache mockedCache = mock(DynamoDbDeploymentContextTableCache.class);
        when(mockedCache.getProperties()).thenReturn(propCollection1, propCollection2);

        DynamoDbDeploymentContextConfigurationSource testConfigSource =
                new DynamoDbDeploymentContextConfigurationSource(mockedCache, DeploymentContext.ContextKey.environment);

        PollResult result = testConfigSource.poll(false, null);
        assertEquals(3, result.getComplete().size());
        assertEquals(result.getComplete().get("foo"), "bar");
        assertEquals(result.getComplete().get("goo"), "goo");
        assertEquals(result.getComplete().get("boo"), "who");

        result = testConfigSource.poll(false, null);
        assertEquals(3, result.getComplete().size());
        assertEquals(result.getComplete().get("foo"),"bar");
        assertEquals(result.getComplete().get("goo"), "boo");
        assertEquals(result.getComplete().get("boo"), "who");
    }
}
