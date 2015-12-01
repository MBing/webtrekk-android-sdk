package com.webtrekk.webtrekksdk;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.Suppress;

import com.webtrekk.webbtrekksdk.R;
import com.webtrekk.webtrekksdk.TrackingParameter.Parameter;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


public class TrackingConfigurationXmlParserTest extends AndroidTestCase {

    private TrackingConfigurationXmlParser trackingConfigurationXmlParser;
    String invalidXmlConfiguration = "foo";
    String missingTagXmlConfiguration = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<configuration>\n" +
            "<trackdomain type=\"text\">http://q3.webtrekk.net</trackdomain>\n" +
            "<trackid type=\"text\">111111111111\n" +
            "\n" +
            "\n" +
            "    <activity>\n" +
            "        <classname type=\"text\">MainActivity</classname>\n" +
            "        <mappingname type=\"text\">Startseite</mappingname>\n" +
            "        <autotrack type=\"text\">true</autotrack>\n" +
            "    </activity>\n" +
            "\n" +
            "    <plugin>\n" +
            "        <name>HelloWorldPlugin</name>\n" +
            "        <value>hello</value>\n" +
            "    </plugin>\n" +
            "\n" +
            "    <customvalues>\n" +
            "        <exampleval1>custom example val 1</exampleval1>\n" +
            "    </customvalues>\n" +
            "\n" +
            "</configuration>";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // android bug workaround: 308
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().toString());

        trackingConfigurationXmlParser = new TrackingConfigurationXmlParser();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * test that the default value is used when an invalid sampling value is provided in the config
     */
    public void testSamplingOneUseDefault(){
        TrackingConfiguration config = null;

        String configString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><webtrekkConfiguration><sampling type=\"text\">1</sampling></webtrekkConfiguration>";
        try {
            config = trackingConfigurationXmlParser.parse(configString);
            assertNotNull(config);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(0,  config.getSampling());
    }

    /**
     * test that the default value is used when an invalid send delay value is provided in the config
     */
    public void testSendDelayUseDefault(){
        TrackingConfiguration config = null;

        String configString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><webtrekkConfiguration><sendDelay type=\"text\">1</sendDelay></webtrekkConfiguration>";
        try {
            config = trackingConfigurationXmlParser.parse(configString);
            assertNotNull(config);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals("error: config= " + config, 300, config.getSendDelay());
    }

    /**
     * test that the default value is used when an invalid send delay value is provided in the config
     */
    public void testMaxRequestUseDefault(){
        TrackingConfiguration config = null;

        String configString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><webtrekkConfiguration><maxRequests type=\"text\">16</maxRequests></webtrekkConfiguration>";
        try {
            config = trackingConfigurationXmlParser.parse(configString);
            assertNotNull(config);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals("error: config= " + config , 5000, config.getMaxRequests());
    }

    /**
     * test remove trailing slash from trackdomain
     */

    public void testTrailingSlash(){
        TrackingConfiguration config = null;

        String configString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><webtrekkConfiguration><trackDomain type=\"text\">http://test.de/</trackDomain></webtrekkConfiguration>";
        try {
            config = trackingConfigurationXmlParser.parse(configString);
            assertNotNull(config);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals("http://test.de", config.getTrackDomain());
    }



    public void testParseValidXmlGlobalSection() {
        TrackingConfiguration config = null;
        try {
            String trackingConfigurationString = HelperFunctions.stringFromStream(getContext().getResources().openRawResource(R.raw.webtrekk_config));
            config = trackingConfigurationXmlParser.parse(trackingConfigurationString);
        } catch (Exception e) {
            android.util.Log.d("WebtrekkSDK", "parsing error", e);
        }
        assertEquals(2, config.getVersion());
        assertEquals("http://trackingtest.nglab.org", config.getTrackDomain());
        assertEquals("1111111111112", config.getTrackId());
        assertEquals(0, config.getSampling());
        assertEquals(60, config.getSendDelay());
        assertEquals(5000, config.getMaxRequests());

        assertEquals(true, config.isAutoTracked());
        assertEquals(true, config.isAutoTrackAppUpdate());
        assertEquals(true, config.isAutoTrackAdvertiserId());
        assertEquals(true, config.isAutoTrackAppVersionName());
        assertEquals(true, config.isAutoTrackAppVersionCode());
        assertEquals(true, config.isAutoTrackAppPreInstalled());
        assertEquals(true, config.isAutoTrackPlaystoreUsername());
        assertEquals(true, config.isAutoTrackPlaystoreMail());
        assertEquals(true, config.isAutoTrackPlaystoreGivenName());
        assertEquals(true, config.isAutoTrackPlaystoreFamilyName());
        assertEquals(true, config.isAutoTrackApiLevel());
        assertEquals(true, config.isAutoTrackScreenorientation());
        assertEquals(true, config.isAutoTrackConnectionType());
        assertEquals(true, config.isAutoTrackAdvertismentOptOut());

        assertEquals(false, config.isEnableRemoteConfiguration());
        assertEquals("http://localhost/tracking_config.xml", config.getTrackingConfigurationUrl());
        assertEquals(true, config.isAutoTrackRequestUrlStoreSize());
        assertEquals(30, config.getResendOnStartEventTime());

    }

    public void testParseValidXmlGlobalTrackingParameter() {
        TrackingConfiguration config = null;
        try {
            String trackingConfigurationString = HelperFunctions.stringFromStream(getContext().getResources().openRawResource(R.raw.webtrekk_config));
            config = trackingConfigurationXmlParser.parse(trackingConfigurationString);
        } catch (Exception e) {
            android.util.Log.d("WebtrekkSDK", "parsing error", e);
        }
        TrackingParameter tp = config.getGlobalTrackingParameter();
        assertNotNull(tp);
        assertEquals("test_product", tp.getDefaultParameter().get(Parameter.PRODUCT));
        assertEquals("443", tp.getDefaultParameter().get(Parameter.PRODUCT_COST));

        // check if all parameter maps have been initialized
        assertNotNull(tp.getPageParameter());
        assertNotNull(tp.getSessionParameter());
        assertNotNull(tp.getEcomParameter());
        assertNotNull(tp.getUserCategories());
        assertNotNull(tp.getPageCategories());
        assertNotNull(tp.getAdParameter());
        assertNotNull(tp.getActionParameter());
        assertNotNull(tp.getProductCategories());
        assertNotNull(tp.getMediaCategories());

        // check for correct single values
        assertEquals("test_pageparam1", tp.getPageParameter().get("1"));
        assertEquals("test_pageparam2", tp.getPageParameter().get("2"));
        assertEquals("test_pageparam3", tp.getPageParameter().get("3"));

        assertEquals("test_sessionparam1", tp.getSessionParameter().get("1"));
        assertEquals("test_ecomparam1", tp.getEcomParameter().get("1"));
        assertEquals("test_usercategory1", tp.getUserCategories().get("1"));
        assertEquals("test_pagecategory1", tp.getPageCategories().get("1"));
        assertEquals("test_adparam", tp.getAdParameter().get("1"));
        assertEquals("test_actionparam1", tp.getActionParameter().get("1"));
        assertEquals("test_productcategory1", tp.getProductCategories().get("1"));
        assertEquals("test_mediacategory1", tp.getMediaCategories().get("1"));



    }

    @Suppress
    public void testParseValidXmlCustomParameters() {
        TrackingConfiguration config = null;
        try {
            String trackingConfigurationString = HelperFunctions.stringFromStream(getContext().getResources().openRawResource(R.raw.webtrekk_config));
            config = trackingConfigurationXmlParser.parse(trackingConfigurationString);
        } catch (Exception e) {
            android.util.Log.d("WebtrekkSDK", "parsing error", e);
        }
        assertNotNull(config.getCustomParameter());
        android.util.Log.d("WebtrekkSDK", "customParameter: " + config.getCustomParameter().toString());
        assertEquals(3, config.getCustomParameter().size());
        assertEquals("value1", config.getCustomParameter().get("test_customer_parameter_key1"));
        assertEquals("value2", config.getCustomParameter().get("test_customer_parameter_key2"));
        assertEquals("value3", config.getCustomParameter().get("test_customer_parameter_key3"));

    }
    public void testParseValidXmlActivityConfiguration() {
        TrackingConfiguration config = null;
        try {
            String trackingConfigurationString = HelperFunctions.stringFromStream(getContext().getResources().openRawResource(R.raw.webtrekk_config));
            config = trackingConfigurationXmlParser.parse(trackingConfigurationString);
        } catch (Exception e) {
            android.util.Log.d("WebtrekkSDK", "parsing error", e);
        }
        assertNotNull(config.getActivityConfigurations());
        assertEquals(1, config.getActivityConfigurations().size());
        assertTrue(config.getActivityConfigurations().containsKey("MainActivity"));
        ActivityConfiguration act = config.getActivityConfigurations().get("MainActivity");
        assertNotNull(act);
        assertEquals(act.getMappingName(), "Startseite");
        assertEquals(act.isAutoTrack(), true);
        TrackingParameter tp = act.getActivityTrackingParameter();

        // check if all parameter maps have been initialized
        assertNotNull(tp.getPageParameter());
        assertNotNull(tp.getSessionParameter());
        assertNotNull(tp.getEcomParameter());
        assertNotNull(tp.getUserCategories());
        assertNotNull(tp.getPageCategories());
        assertNotNull(tp.getAdParameter());
        assertNotNull(tp.getActionParameter());
        assertNotNull(tp.getProductCategories());
        assertNotNull(tp.getMediaCategories());

        // check for correct single values
        assertEquals("test_pageparam1", tp.getPageParameter().get("1"));
        assertEquals("test_pageparam2", tp.getPageParameter().get("2"));
        assertEquals("test_pageparam3", tp.getPageParameter().get("3"));

        assertEquals("test_sessionparam1", tp.getSessionParameter().get("1"));
        assertEquals("test_ecomparam1", tp.getEcomParameter().get("1"));
        assertEquals("test_usercategory1", tp.getUserCategories().get("1"));
        assertEquals("test_pagecategory1", tp.getPageCategories().get("1"));
        assertEquals("test_adparam", tp.getAdParameter().get("1"));
        assertEquals("test_actionparam1", tp.getActionParameter().get("1"));
        assertEquals("test_productcategory1", tp.getProductCategories().get("1"));
        assertEquals("test_mediacategory1", tp.getMediaCategories().get("1"));
    }


    public void testParseInvalidXML() {
        TrackingConfiguration config = null;
        try {
            String trackingConfigurationString = HelperFunctions.stringFromStream(getContext().getResources().openRawResource(R.raw.webtrekk_config));
            config = trackingConfigurationXmlParser.parse(invalidXmlConfiguration);
            fail("invalid xml configuration");
        }  catch (Exception e) {
        }

        try {
            String trackingConfigurationString = HelperFunctions.stringFromStream(getContext().getResources().openRawResource(R.raw.webtrekk_config));
            config = trackingConfigurationXmlParser.parse(missingTagXmlConfiguration);
            fail("invalid xml configuration");
        }  catch (Exception e) {
        }
    }

}