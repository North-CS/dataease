package io.dataease.plugins.datasource.iotdb.provider;

import com.alibaba.fastjson.JSONObject;
import io.dataease.plugins.common.request.datasource.DatasourceRequest;
import junit.framework.TestCase;

import java.util.List;
import java.util.Map;

public class IotdbDsProviderTest extends TestCase {

    public void testGetData() {
    }

    public void testFetchResultAndField() {
//        IotdbDsProvider provider = new IotdbDsProvider();
//        DatasourceRequest datasourceRequest = new DatasourceRequest();
//
//        String json = "{\"datasource\":{\"configuration\":\"{\\\"initialPoolSize\\\":5,\\\"extraParams\\\":\\\"\\\",\\\"minPoolSize\\\":5,\\\"maxPoolSize\\\":50,\\\"maxIdleTime\\\":30,\\\"acquireIncrement\\\":5,\\\"idleConnectionTestPeriod\\\":5,\\\"connectTimeout\\\":5,\\\"customDriver\\\":\\\"default\\\",\\\"queryTimeout\\\":30,\\\"username\\\":\\\"root\\\",\\\"password\\\":\\\"root\\\",\\\"host\\\":\\\"10.1.13.137\\\",\\\"port\\\":\\\"6667\\\"}\",\"createBy\":\"admin\",\"createTime\":1726801386230,\"enableDataFill\":false,\"enableDataFillCreateTable\":false,\"id\":\"4745bb05-2e6e-4fb4-b248-bd25c5262840\",\"name\":\"iot\",\"status\":\"Success\",\"type\":\"iotdb\",\"updateTime\":1726801386230},\"fetchSize\":10000,\"lowerCaseTaleNames\":false,\"pageable\":false,\"previewData\":false,\"query\":\"SELECT * FROM root.ln.wf01.wt01\",\"rEG_WITH_SQL_FRAGMENT\":\"((?i)WITH[\\\\s\\\\S]+(?i)AS?\\\\s*\\\\([\\\\s\\\\S]+\\\\))\\\\s*(?i)SELECT\",\"table\":\"root.ln.wf01.wt01\",\"totalPageFlag\":false,\"wITH_SQL_FRAGMENT\":\"((?i)WITH[\\\\s\\\\S]+(?i)AS?\\\\s*\\\\([\\\\s\\\\S]+\\\\))\\\\s*(?i)SELECT\"}";
//        datasourceRequest = JSONObject.parseObject(json, DatasourceRequest.class);
//
//        try {
//            Map<String, List> map = provider.fetchResultAndField(datasourceRequest);
//            System.out.println("111");
//        } catch (Exception exception) {
//            System.out.println(exception.getMessage());
//        }
    }


    public void testTestGetData() {
//        IotdbDsProvider provider = new IotdbDsProvider();
//        DatasourceRequest datasourceRequest = new DatasourceRequest();
//
//        String json = "{\"datasource\":{\"configuration\":\"{\\\"initialPoolSize\\\":5,\\\"extraParams\\\":\\\"\\\",\\\"minPoolSize\\\":5,\\\"maxPoolSize\\\":50,\\\"maxIdleTime\\\":30,\\\"acquireIncrement\\\":5,\\\"idleConnectionTestPeriod\\\":5,\\\"connectTimeout\\\":5,\\\"customDriver\\\":\\\"default\\\",\\\"queryTimeout\\\":30,\\\"username\\\":\\\"root\\\",\\\"password\\\":\\\"root\\\",\\\"host\\\":\\\"192.168.0.228\\\",\\\"port\\\":\\\"6667\\\"}\",\"createBy\":\"admin\",\"createTime\":1740623807562,\"enableDataFill\":false,\"enableDataFillCreateTable\":false,\"id\":\"d454805c-acbd-4318-9ffa-eeb79f9cc2d2\",\"name\":\"local_IOT\",\"status\":\"Success\",\"type\":\"iotdb\",\"updateTime\":1740623807562},\"fetchSize\":10000,\"lowerCaseTaleNames\":false,\"pageable\":false,\"previewData\":false,\"query\":\"SELECT\\n    hardware AS f_ax_0\\nFROM\\n    root.sg.device1   \\n LIMIT 11 offset 0\",\"rEG_WITH_SQL_FRAGMENT\":\"((?i)WITH[\\\\s\\\\S]+(?i)AS?\\\\s*\\\\([\\\\s\\\\S]+\\\\))\\\\s*(?i)SELECT\",\"table\":\"root.sg.device1\",\"totalPageFlag\":false,\"wITH_SQL_FRAGMENT\":\"((?i)WITH[\\\\s\\\\S]+(?i)AS?\\\\s*\\\\([\\\\s\\\\S]+\\\\))\\\\s*(?i)SELECT\",\"xAxis\":[{\"checked\":true,\"columnIndex\":4,\"dataeaseName\":\"C_f2a51daebdd828c8d634a8fe628ee8c6\",\"datePattern\":\"date_sub\",\"dateStyle\":\"y_M_d\",\"deExtractType\":0,\"deType\":0,\"drill\":false,\"extField\":0,\"filter\":[],\"groupType\":\"d\",\"id\":\"7fb3f50c-bfcc-470b-8c99-df60f9456885\",\"lastSyncTime\":1740650675427,\"name\":\"root.sg.device1.hardware\",\"originName\":\"root.sg.device1.hardware\",\"sort\":\"none\",\"tableId\":\"70b8c092-dfff-4f4c-a8ac-101e2dbd29f7\",\"type\":\"TEXT\"}],\"yAxis\":[]}";
//        datasourceRequest = JSONObject.parseObject(json, DatasourceRequest.class);
//
//        try {
//            List<String[]> list = provider.getData(datasourceRequest);
//            System.out.println("111");
//        } catch (Exception exception) {
//            System.out.println(exception.getMessage());
//        }

        IotdbDsProvider provider = new IotdbDsProvider();
        DatasourceRequest datasourceRequest = new DatasourceRequest();

        String json = "{\"datasource\":{\"configuration\":\"{\\\"initialPoolSize\\\":5,\\\"extraParams\\\":\\\"\\\",\\\"minPoolSize\\\":5,\\\"maxPoolSize\\\":50,\\\"maxIdleTime\\\":30,\\\"acquireIncrement\\\":5,\\\"idleConnectionTestPeriod\\\":5,\\\"connectTimeout\\\":5,\\\"customDriver\\\":\\\"default\\\",\\\"queryTimeout\\\":30,\\\"username\\\":\\\"root\\\",\\\"password\\\":\\\"root\\\",\\\"host\\\":\\\"192.168.0.228\\\",\\\"port\\\":\\\"6667\\\"}\",\"createBy\":\"admin\",\"createTime\":1740623807562,\"enableDataFill\":false,\"enableDataFillCreateTable\":false,\"id\":\"d454805c-acbd-4318-9ffa-eeb79f9cc2d2\",\"name\":\"local_IOT\",\"status\":\"Success\",\"type\":\"iotdb\",\"updateTime\":1740623807562},\"fetchSize\":10000,\"lowerCaseTaleNames\":false,\"pageable\":false,\"previewData\":false,\"query\":\"SELECT\\n    CAST(avg(CAST(temperature AS DOUBLE)) AS DOUBLE) AS f_ay_0,\\n    MAX_VALUE(CAST(temperature AS DOUBLE)) AS f_ay_1\\nFROM\\n    root.sg.device1   \\nWHERE\\n    ((temperature  > 10))\\n LIMIT 11 offset 0\",\"rEG_WITH_SQL_FRAGMENT\":\"((?i)WITH[\\\\s\\\\S]+(?i)AS?\\\\s*\\\\([\\\\s\\\\S]+\\\\))\\\\s*(?i)SELECT\",\"table\":\"root.sg.device1\",\"totalPageFlag\":false,\"wITH_SQL_FRAGMENT\":\"((?i)WITH[\\\\s\\\\S]+(?i)AS?\\\\s*\\\\([\\\\s\\\\S]+\\\\))\\\\s*(?i)SELECT\",\"xAxis\":[],\"yAxis\":[{\"chartType\":\"bar\",\"checked\":true,\"columnIndex\":1,\"compareCalc\":{\"custom\":{\"calcType\":\"0\",\"compareTime\":\"\",\"compareTimeRange\":[],\"currentTime\":\"\",\"currentTimeRange\":[],\"field\":\"\",\"timeType\":\"y_M_d\"},\"field\":\"031b8c4c-9ae4-4e15-a11d-9e50342fc813\",\"resultData\":\"percent\",\"type\":\"none\"},\"dataeaseName\":\"C_826ca39dc2e153e1dc0cfa4450ff0955\",\"deExtractType\":3,\"deType\":3,\"drill\":false,\"extField\":0,\"filter\":[{\"term\":\"gt\",\"value\":\"30\"}],\"filterType\":\"quota\",\"groupType\":\"q\",\"id\":\"02b64b97-e5c2-4a67-afab-567149117d8d\",\"lastSyncTime\":1740650675427,\"logic\":\"and\",\"name\":\"root.sg.device1.temperature\",\"originName\":\"root.sg.device1.temperature\",\"sort\":\"desc\",\"summary\":\"avg\",\"tableId\":\"70b8c092-dfff-4f4c-a8ac-101e2dbd29f7\",\"type\":\"DOUBLE\"},{\"checked\":true,\"columnIndex\":1,\"dataeaseName\":\"C_826ca39dc2e153e1dc0cfa4450ff0955\",\"deExtractType\":3,\"deType\":3,\"drill\":false,\"extField\":0,\"groupType\":\"q\",\"id\":\"02b64b97-e5c2-4a67-afab-567149117d8d\",\"lastSyncTime\":1740650675427,\"name\":\"root.sg.device1.temperature\",\"originName\":\"root.sg.device1.temperature\",\"summary\":\"max\",\"tableId\":\"70b8c092-dfff-4f4c-a8ac-101e2dbd29f7\",\"type\":\"DOUBLE\"}]}";
        datasourceRequest = JSONObject.parseObject(json, DatasourceRequest.class);

        try {
            List<String[]> list = provider.getData(datasourceRequest);
            System.out.println("111");
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }

    }


    public void testFetchResultField() {
    }


}

