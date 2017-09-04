/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.identity.integration.test.scim2;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.identity.integration.common.clients.usermgt.remote.RemoteUserStoreManagerServiceClient;
import org.wso2.identity.integration.common.utils.ISIntegrationTest;
import org.wso2.identity.integration.test.application.mgt.AbstractIdentityFederationTestCase;
import org.wso2.identity.integration.test.utils.CommonConstants;
import org.wso2.identity.integration.test.utils.IdentityConstants;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.testng.asserts.Assertion;

public class SCIM2TestSuiteTestCase extends AbstractIdentityFederationTestCase{
    private static final int TOMCAT_8490 = 8490;
    private Map<Integer, Tomcat> tomcatServers;
    private Assertion hardAssert = new Assertion();
    private SoftAssert softAssert = new SoftAssert();
    org.json.simple.JSONArray jsonArray;
    private static final String SUCCESS = "success";

    @BeforeClass(alwaysRun = true)
    public void initTest() throws Exception {

        tomcatServers = new HashMap<Integer, Tomcat>();
        // Deploy webapp in Tomcat
        startTomcat(TOMCAT_8490);
        File configuredIdentityXML = new File(getISResourceLocation()
                + File.separator + "scim2" + File.separator
                + "scimproxycompliance.war");
        addWebAppToTomcat(TOMCAT_8490, "/scimproxycompliance", configuredIdentityXML.getAbsolutePath());
        runTestSuite();
    }

    public void addWebAppToTomcat(int port, String webAppUrl, String webAppPath)
            throws LifecycleException {
        tomcatServers.get(port).addWebapp(tomcatServers.get(port).getHost(), webAppUrl, webAppPath);
    }


    public void startTomcat(int port) throws LifecycleException {

        Tomcat tomcat = new Tomcat();
        tomcat.getService().setContainer(tomcat.getEngine());
        tomcat.setPort(port);
        tomcat.setBaseDir("");

        StandardHost stdHost = (StandardHost) tomcat.getHost();
        stdHost.setAppBase("");
        stdHost.setAutoDeploy(true);
        stdHost.setDeployOnStartup(true);
        stdHost.setUnpackWARs(true);
        tomcat.setHost(stdHost);

        setSystemProperties();
        tomcatServers.put(port, tomcat);
        tomcat.start();
    }

    private void setSystemProperties() {
        URL resourceUrl = getClass().getResource(File.separator + "keystores" + File.separator + "products" + File.separator + "wso2carbon.jks");
        System.setProperty("javax.net.ssl.trustStore", resourceUrl.getPath());
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
    }

    public void runTestSuite() throws Exception {

        List<NameValuePair> postParameters;
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://localhost:" + "8080" + "/scimproxycompliance/compliance2/test2");
//        HttpPost httpPost = new HttpPost("http://localhost:"+ TOMCAT_8490 + "/scimproxycompliance/compliance2/test2");
        //generate post request
//        httpPost.setHeader("Authorization", "Basic " + getBase64EncodedString(consumerKey, consumerSecret));
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("url", "https://localhost:9853/scim2"));
        postParameters.add(new BasicNameValuePair("authMethod", "basicAuth"));
        postParameters.add(new BasicNameValuePair("username", "admin"));
        postParameters.add(new BasicNameValuePair("password", "admin"));
        httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
        HttpResponse response = client.execute(httpPost);

        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";

        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        String a = result.toString();

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(a);
        jsonArray = (org.json.simple.JSONArray) json.get("results");
    }


    @Test
    public void test1(){
        Assert.assertEquals(((JSONObject) (jsonArray.get(0))).get("status_text").toString(), SUCCESS,
                ((JSONObject) (jsonArray.get(0))).get("name").toString());
    }

    @Test
    public void test2() {
        Assert.assertEquals(((JSONObject) (jsonArray.get(1))).get("status_text").toString(), SUCCESS,
                ((JSONObject) (jsonArray.get(1))).get("name").toString());    }

    @Test
    public void test3() {
        Assert.assertEquals(((JSONObject) (jsonArray.get(2))).get("status_text").toString(), SUCCESS,
                ((JSONObject) (jsonArray.get(2))).get("name").toString());
    }@Test
    public void test4() {
        Assert.assertEquals(((JSONObject) (jsonArray.get(3))).get("status_text").toString(), SUCCESS,
                ((JSONObject) (jsonArray.get(3))).get("name").toString());
    }@Test
    public void test5() {
        Assert.assertEquals(((JSONObject) (jsonArray.get(4))).get("status_text").toString(), SUCCESS,
                ((JSONObject) (jsonArray.get(4))).get("name").toString());
    }
}
