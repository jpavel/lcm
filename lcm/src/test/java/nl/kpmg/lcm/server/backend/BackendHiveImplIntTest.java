/*
 * Copyright 2015 KPMG N.V. (unless otherwise stated).
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
package nl.kpmg.lcm.server.backend;

import java.io.InputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.kpmg.lcm.server.data.BackendModel;
import nl.kpmg.lcm.server.data.MetaData;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jpavel
 */
public class BackendHiveImplIntTest {
    
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";
    
    private final String TEST_STORAGE_PATH = "hive://localhost:10000";
    
    /**
     * Common access tool for all backends.
     */
    private final BackendModel backendModel;

    /**
     * Default constructor.
     */
    public BackendHiveImplIntTest() {
        backendModel = new BackendModel();
        backendModel.setName("test");
        backendModel.setOptions(new HashMap());
        backendModel.getOptions().put("storagePath", TEST_STORAGE_PATH);
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    
    
    /**
     * Test of getSupportedUriSchema method, of class BackendHiveImpl. 
     * Method returns supported uri schema ("hive") and tests fails if it is 
     * not the case.
     * 
     */
    @Test
    public void testGetSupportedUriSchema() {
        System.out.println("getSupportedUriSchema");
        BackendHiveImpl instance = new BackendHiveImpl(backendModel);
        String expResult = "hive";
        String result = instance.getSupportedUriSchema();
        assertEquals(expResult, result);
    }
    /**
     * Test of connection to the hive server.
     * Method connects to the hive2 server specified in "storagePath" of backendModel.
     * It assumes existence of a database called "default" and lists all tables in this
     * database.
     *
     * @throws SQLException if there is a problem with connection or sql query
     * @throws BackendException if it is not possible to retrieve hive server address from the 
     * {@link BackendModel} instance that initialized the {@link BackendHiveImp} instance.
     */
    @Test
    public final void testConnection() throws SQLException, BackendException {
        System.out.println("testConnection will print all tables in \'default\' db");
        BackendHiveImpl testBackend = new BackendHiveImpl(backendModel);
        URI hiveUri;
        hiveUri = testBackend.parseUri((String) backendModel.getOptions().get("storagePath"));
        String hostName = hiveUri.getHost();
        String port = Integer.toString(hiveUri.getPort());
        String server = testBackend.getURIscheme() + hostName + ":" + port + "/default";
        String user = "";
        String passwd = "";
        
        try {
            Class.forName(driverName);
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(BackendHiveImplIntTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Connection con = DriverManager.getConnection(server, user, passwd);
        Statement stmt = con.createStatement();
        ResultSet res = stmt.executeQuery("show tables in default");
       while (res.next()) {
                String resString = res.getString(1);
                System.out.println(resString);
            }
        con.close();
        
    }
    /**
     * Tests what happens if {@link BackendHiveImp} gathers information using
     * {@link MetaData} object with valid URI pointing to existing location. The
     * {@link DataSetInformation} object should has isAttached() method equal to
     * true.
     *
     * @throws BackendException if it is not possible to gather information
     * about the dataset
     */
    @Test
    public final void testGatherDatasetInformation() throws BackendException {
        System.out.println("testGatherDatasetInformation");
        MetaData metaData = new MetaData();
        final String fileUri = "hive://jpavel@localhost:10000/default/batting";
        metaData.put("data", new HashMap() { { put("uri", fileUri); } });
        BackendHiveImpl testBackend = new BackendHiveImpl(backendModel);
        DataSetInformation dataSetInformation = testBackend.gatherDataSetInformation(metaData);
        System.out.println("modification time: " + dataSetInformation.getModificationTime());
        System.out.println("is readable: " + dataSetInformation.isReadable());
        System.out.println("byte size is: " + dataSetInformation.getByteSize());
        assertEquals(dataSetInformation.isAttached(), true);
    }
    
    /**
     * Tests what happens if {@link BackendHiveImp} gathers information using
     * empty {@link MetaData} object. Exception is expected.
     *
     * @throws BackendException if empty metadata are supplied.
     */
    @Test(expected = BackendException.class)
    public final void testGatherDatasetInformationEmptyMetadata() throws BackendException {
        System.out.println("testGatherDatasetInformationEmptyMetadata");
        MetaData metaData = new MetaData();
        BackendHiveImpl testBackend = new BackendHiveImpl(backendModel);
        DataSetInformation dataSetInformation = testBackend.gatherDataSetInformation(metaData);
        fail("testGatherDatasetInformationEmptyMetadata did not thrown BackendException!");
    }
//    
    /**
     * Tests what happens if {@link BackendHiveImp} gathers information using
     * {@link MetaData} object with invalid URI. The method should fail as well 
     * because invalid uri scheme is supplied.
     *
     * @throws BackendException if invalid URI is supplied.
     */
    @Test(expected = BackendException.class)
    public final void testGatherDatasetInformationWrongMetadata() throws BackendException {
        System.out.println("testGatherDatasetInformationWrongMetadata");
        MetaData metaData = new MetaData();
        final String fileUri = "NotAnUri";
        metaData.put("data", new HashMap() { { put("uri", fileUri); } });
        BackendHiveImpl testBackend = new BackendHiveImpl(backendModel);
        DataSetInformation dataSetInformation = testBackend.gatherDataSetInformation(metaData);
        fail("testGatherDatasetInformationWrongMetadata did not thrown BackendException!");
    }
    
     /**
     * Tests what happens if {@link BackendHiveImp} gathers information using
     * {@link MetaData} object with valid but incomplete URI. The method should fail as well 
     * because the URI is not complete
     *
     * @throws BackendException if incomplete URI is supplied.
     */
    @Test(expected = BackendException.class)
    public final void testGatherDatasetInformationWrongLink() throws BackendException {
        System.out.println("testGatherDatasetInformationWrongLink");
        MetaData metaData = new MetaData();
        final String fileUri = "hive://NoPath";
        metaData.put("data", new HashMap() { { put("uri", fileUri); } });
        BackendHiveImpl testBackend = new BackendHiveImpl(backendModel);
        DataSetInformation dataSetInformation = testBackend.gatherDataSetInformation(metaData);
       fail("testGatherDatasetInformationWrongLink did not thrown BackendException!");
    }
    
     /**
     * Tests what happens if {@link BackendHiveImp} gathers information using
     * {@link MetaData} object with valid URI pointing to non-existing location. The
     * {@link DataSetInformation} object should has isAttached() method equal to
     * false.
     *
     * @throws BackendException if it is not possible to gather information
     * about the dataset
     */
    @Test
    public final void testGatherDatasetInformationWrongDest() throws BackendException {
        System.out.println("testGatherDatasetInformationWrongDest");
        MetaData metaData = new MetaData();
        final String fileUri = "hive://jpavel@localhost:10000/default/batling";
        metaData.put("data", new HashMap() { { put("uri", fileUri); } });
        BackendHiveImpl testBackend = new BackendHiveImpl(backendModel);
        DataSetInformation dataSetInformation = testBackend.gatherDataSetInformation(metaData);
        System.out.println("modification time: " + dataSetInformation.getModificationTime());
        System.out.println("is readable: " + dataSetInformation.isReadable());
        System.out.println("byte size is: " + dataSetInformation.getByteSize());
        assertEquals(dataSetInformation.isAttached(), false);
    }

   
    

    /**
     * Test of gatherDataSetInformation method, of class BackendHiveImpl.
     */
//    @Test
//    public void testGatherDataSetInformation() throws Exception {
//        System.out.println("gatherDataSetInformation");
//        MetaData metadata = null;
//        BackendHiveImpl instance = new BackendHiveImpl();
//        DataSetInformation expResult = null;
//        DataSetInformation result = instance.gatherDataSetInformation(metadata);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of store method, of class BackendHiveImpl.
//     */
//    @Test
//    public void testStore() throws Exception {
//        System.out.println("store");
//        MetaData metadata = null;
//        InputStream content = null;
//        BackendHiveImpl instance = new BackendHiveImpl();
//        instance.store(metadata, content);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of read method, of class BackendHiveImpl.
//     */
//    @Test
//    public void testRead() throws Exception {
//        System.out.println("read");
//        MetaData metadata = null;
//        BackendHiveImpl instance = new BackendHiveImpl();
//        InputStream expResult = null;
//        InputStream result = instance.read(metadata);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of delete method, of class BackendHiveImpl.
//     */
//    @Test
//    public void testDelete() throws Exception {
//        System.out.println("delete");
//        MetaData metadata = null;
//        BackendHiveImpl instance = new BackendHiveImpl();
//        boolean expResult = false;
//        boolean result = instance.delete(metadata);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
}
