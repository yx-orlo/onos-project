package org.orlo.app;

import org.onlab.packet.MacAddress;
import org.onosproject.net.DeviceId;
import org.onosproject.net.PortNumber;

import java.util.HashMap;

/**
 * 此文件是66个节点的映射配置文件，测试代码中没有使用，可以忽略.
 */
public final class MyUtil {
    private static HashMap<String, DeviceId> swMap = new HashMap<>();
    private static HashMap<String, MacAddress> hostMap = new HashMap<>();
    private static HashMap<String, PortNumber> portMap = new HashMap<>();
    static {
        swMap.put("11", DeviceId.deviceId("of:000000000000000b"));
        swMap.put("12", DeviceId.deviceId("of:000000000000000c"));
        swMap.put("13", DeviceId.deviceId("of:000000000000000d"));
        swMap.put("14", DeviceId.deviceId("of:000000000000000e"));
        swMap.put("15", DeviceId.deviceId("of:000000000000000f"));
        swMap.put("16", DeviceId.deviceId("of:0000000000000010"));
        swMap.put("17", DeviceId.deviceId("of:0000000000000011"));
        swMap.put("18", DeviceId.deviceId("of:0000000000000012"));
        swMap.put("19", DeviceId.deviceId("of:0000000000000013"));
        swMap.put("110", DeviceId.deviceId("of:000000000000006e"));
        swMap.put("111", DeviceId.deviceId("of:000000000000006f"));

        swMap.put("21", DeviceId.deviceId("of:0000000000000015"));
        swMap.put("22", DeviceId.deviceId("of:0000000000000016"));
        swMap.put("23", DeviceId.deviceId("of:0000000000000017"));
        swMap.put("24", DeviceId.deviceId("of:0000000000000018"));
        swMap.put("25", DeviceId.deviceId("of:0000000000000019"));
        swMap.put("26", DeviceId.deviceId("of:000000000000001a"));
        swMap.put("27", DeviceId.deviceId("of:000000000000001b"));
        swMap.put("28", DeviceId.deviceId("of:000000000000001c"));
        swMap.put("29", DeviceId.deviceId("of:000000000000001d"));
        swMap.put("210", DeviceId.deviceId("of:00000000000000d2"));
        swMap.put("211", DeviceId.deviceId("of:00000000000000d3"));

        swMap.put("31", DeviceId.deviceId("of:000000000000001f"));
        swMap.put("32", DeviceId.deviceId("of:0000000000000020"));
        swMap.put("33", DeviceId.deviceId("of:0000000000000021"));
        swMap.put("34", DeviceId.deviceId("of:0000000000000022"));
        swMap.put("35", DeviceId.deviceId("of:0000000000000023"));
        swMap.put("36", DeviceId.deviceId("of:0000000000000024"));
        swMap.put("37", DeviceId.deviceId("of:0000000000000025"));
        swMap.put("38", DeviceId.deviceId("of:0000000000000026"));
        swMap.put("39", DeviceId.deviceId("of:0000000000000027"));
        swMap.put("310", DeviceId.deviceId("of:0000000000000136"));
        swMap.put("311", DeviceId.deviceId("of:0000000000000137"));

        swMap.put("41", DeviceId.deviceId("of:0000000000000029"));
        swMap.put("42", DeviceId.deviceId("of:000000000000002a"));
        swMap.put("43", DeviceId.deviceId("of:000000000000002b"));
        swMap.put("44", DeviceId.deviceId("of:000000000000002c"));
        swMap.put("45", DeviceId.deviceId("of:000000000000002d"));
        swMap.put("46", DeviceId.deviceId("of:000000000000002e"));
        swMap.put("47", DeviceId.deviceId("of:000000000000002f"));
        swMap.put("48", DeviceId.deviceId("of:0000000000000030"));
        swMap.put("49", DeviceId.deviceId("of:0000000000000031"));
        swMap.put("410", DeviceId.deviceId("of:000000000000019a"));
        swMap.put("411", DeviceId.deviceId("of:000000000000019b"));

        swMap.put("51", DeviceId.deviceId("of:0000000000000033"));
        swMap.put("52", DeviceId.deviceId("of:0000000000000034"));
        swMap.put("53", DeviceId.deviceId("of:0000000000000035"));
        swMap.put("54", DeviceId.deviceId("of:0000000000000036"));
        swMap.put("55", DeviceId.deviceId("of:0000000000000037"));
        swMap.put("56", DeviceId.deviceId("of:0000000000000038"));
        swMap.put("57", DeviceId.deviceId("of:0000000000000039"));
        swMap.put("58", DeviceId.deviceId("of:000000000000003a"));
        swMap.put("59", DeviceId.deviceId("of:000000000000003b"));
        swMap.put("510", DeviceId.deviceId("of:00000000000001fe"));
        swMap.put("511", DeviceId.deviceId("of:00000000000001ff"));

        swMap.put("61", DeviceId.deviceId("of:000000000000003d"));
        swMap.put("62", DeviceId.deviceId("of:000000000000003e"));
        swMap.put("63", DeviceId.deviceId("of:000000000000003f"));
        swMap.put("64", DeviceId.deviceId("of:0000000000000040"));
        swMap.put("65", DeviceId.deviceId("of:0000000000000041"));
        swMap.put("66", DeviceId.deviceId("of:0000000000000042"));
        swMap.put("67", DeviceId.deviceId("of:0000000000000043"));
        swMap.put("68", DeviceId.deviceId("of:0000000000000044"));
        swMap.put("69", DeviceId.deviceId("of:0000000000000045"));
        swMap.put("610", DeviceId.deviceId("of:0000000000000262"));
        swMap.put("611", DeviceId.deviceId("of:0000000000000263"));

        hostMap.put("11", MacAddress.valueOf("00:00:00:00:00:11"));
        hostMap.put("12", MacAddress.valueOf("00:00:00:00:00:12"));
        hostMap.put("13", MacAddress.valueOf("00:00:00:00:00:13"));
        hostMap.put("14", MacAddress.valueOf("00:00:00:00:00:14"));
        hostMap.put("15", MacAddress.valueOf("00:00:00:00:00:15"));
        hostMap.put("16", MacAddress.valueOf("00:00:00:00:00:16"));
        hostMap.put("17", MacAddress.valueOf("00:00:00:00:00:17"));
        hostMap.put("18", MacAddress.valueOf("00:00:00:00:00:18"));
        hostMap.put("19", MacAddress.valueOf("00:00:00:00:00:19"));
        hostMap.put("110", MacAddress.valueOf("00:00:00:00:00:1a"));
        hostMap.put("111", MacAddress.valueOf("00:00:00:00:00:1b"));

        hostMap.put("21", MacAddress.valueOf("00:00:00:00:00:21"));
        hostMap.put("22", MacAddress.valueOf("00:00:00:00:00:22"));
        hostMap.put("23", MacAddress.valueOf("00:00:00:00:00:23"));
        hostMap.put("24", MacAddress.valueOf("00:00:00:00:00:24"));
        hostMap.put("25", MacAddress.valueOf("00:00:00:00:00:25"));
        hostMap.put("26", MacAddress.valueOf("00:00:00:00:00:26"));
        hostMap.put("27", MacAddress.valueOf("00:00:00:00:00:27"));
        hostMap.put("28", MacAddress.valueOf("00:00:00:00:00:28"));
        hostMap.put("29", MacAddress.valueOf("00:00:00:00:00:29"));
        hostMap.put("210", MacAddress.valueOf("00:00:00:00:00:2a"));
        hostMap.put("211", MacAddress.valueOf("00:00:00:00:00:2b"));

        hostMap.put("31", MacAddress.valueOf("00:00:00:00:00:31"));
        hostMap.put("32", MacAddress.valueOf("00:00:00:00:00:32"));
        hostMap.put("33", MacAddress.valueOf("00:00:00:00:00:33"));
        hostMap.put("34", MacAddress.valueOf("00:00:00:00:00:34"));
        hostMap.put("35", MacAddress.valueOf("00:00:00:00:00:35"));
        hostMap.put("36", MacAddress.valueOf("00:00:00:00:00:36"));
        hostMap.put("37", MacAddress.valueOf("00:00:00:00:00:37"));
        hostMap.put("38", MacAddress.valueOf("00:00:00:00:00:38"));
        hostMap.put("39", MacAddress.valueOf("00:00:00:00:00:39"));
        hostMap.put("310", MacAddress.valueOf("00:00:00:00:00:3a"));
        hostMap.put("311", MacAddress.valueOf("00:00:00:00:00:3b"));

        hostMap.put("41", MacAddress.valueOf("00:00:00:00:00:41"));
        hostMap.put("42", MacAddress.valueOf("00:00:00:00:00:42"));
        hostMap.put("43", MacAddress.valueOf("00:00:00:00:00:43"));
        hostMap.put("44", MacAddress.valueOf("00:00:00:00:00:44"));
        hostMap.put("45", MacAddress.valueOf("00:00:00:00:00:45"));
        hostMap.put("46", MacAddress.valueOf("00:00:00:00:00:46"));
        hostMap.put("47", MacAddress.valueOf("00:00:00:00:00:47"));
        hostMap.put("48", MacAddress.valueOf("00:00:00:00:00:48"));
        hostMap.put("49", MacAddress.valueOf("00:00:00:00:00:49"));
        hostMap.put("410", MacAddress.valueOf("00:00:00:00:00:4a"));
        hostMap.put("411", MacAddress.valueOf("00:00:00:00:00:4b"));

        hostMap.put("51", MacAddress.valueOf("00:00:00:00:00:51"));
        hostMap.put("52", MacAddress.valueOf("00:00:00:00:00:52"));
        hostMap.put("53", MacAddress.valueOf("00:00:00:00:00:53"));
        hostMap.put("54", MacAddress.valueOf("00:00:00:00:00:54"));
        hostMap.put("55", MacAddress.valueOf("00:00:00:00:00:55"));
        hostMap.put("56", MacAddress.valueOf("00:00:00:00:00:56"));
        hostMap.put("57", MacAddress.valueOf("00:00:00:00:00:57"));
        hostMap.put("58", MacAddress.valueOf("00:00:00:00:00:58"));
        hostMap.put("59", MacAddress.valueOf("00:00:00:00:00:59"));
        hostMap.put("510", MacAddress.valueOf("00:00:00:00:00:5a"));
        hostMap.put("511", MacAddress.valueOf("00:00:00:00:00:5b"));

        hostMap.put("61", MacAddress.valueOf("00:00:00:00:00:61"));
        hostMap.put("62", MacAddress.valueOf("00:00:00:00:00:62"));
        hostMap.put("63", MacAddress.valueOf("00:00:00:00:00:63"));
        hostMap.put("64", MacAddress.valueOf("00:00:00:00:00:64"));
        hostMap.put("65", MacAddress.valueOf("00:00:00:00:00:65"));
        hostMap.put("66", MacAddress.valueOf("00:00:00:00:00:66"));
        hostMap.put("67", MacAddress.valueOf("00:00:00:00:00:67"));
        hostMap.put("68", MacAddress.valueOf("00:00:00:00:00:68"));
        hostMap.put("69", MacAddress.valueOf("00:00:00:00:00:69"));
        hostMap.put("610", MacAddress.valueOf("00:00:00:00:00:6a"));
        hostMap.put("611", MacAddress.valueOf("00:00:00:00:00:6b"));

        portMap.put("11", PortNumber.portNumber(4));
        portMap.put("12", PortNumber.portNumber(4));
        portMap.put("13", PortNumber.portNumber(4));
        portMap.put("14", PortNumber.portNumber(4));
        portMap.put("15", PortNumber.portNumber(4));
        portMap.put("16", PortNumber.portNumber(4));
        portMap.put("17", PortNumber.portNumber(4));
        portMap.put("18", PortNumber.portNumber(4));
        portMap.put("19", PortNumber.portNumber(4));
        portMap.put("110", PortNumber.portNumber(4));
        portMap.put("111", PortNumber.portNumber(4));

        portMap.put("21", PortNumber.portNumber(5));
        portMap.put("22", PortNumber.portNumber(5));
        portMap.put("23", PortNumber.portNumber(5));
        portMap.put("24", PortNumber.portNumber(5));
        portMap.put("25", PortNumber.portNumber(5));
        portMap.put("26", PortNumber.portNumber(5));
        portMap.put("27", PortNumber.portNumber(5));
        portMap.put("28", PortNumber.portNumber(5));
        portMap.put("29", PortNumber.portNumber(5));
        portMap.put("210", PortNumber.portNumber(5));
        portMap.put("211", PortNumber.portNumber(5));

        portMap.put("31", PortNumber.portNumber(5));
        portMap.put("32", PortNumber.portNumber(5));
        portMap.put("33", PortNumber.portNumber(5));
        portMap.put("34", PortNumber.portNumber(5));
        portMap.put("35", PortNumber.portNumber(5));
        portMap.put("36", PortNumber.portNumber(5));
        portMap.put("37", PortNumber.portNumber(5));
        portMap.put("38", PortNumber.portNumber(5));
        portMap.put("39", PortNumber.portNumber(5));
        portMap.put("310", PortNumber.portNumber(5));
        portMap.put("311", PortNumber.portNumber(5));

        portMap.put("41", PortNumber.portNumber(5));
        portMap.put("42", PortNumber.portNumber(5));
        portMap.put("43", PortNumber.portNumber(5));
        portMap.put("44", PortNumber.portNumber(5));
        portMap.put("45", PortNumber.portNumber(5));
        portMap.put("46", PortNumber.portNumber(5));
        portMap.put("47", PortNumber.portNumber(5));
        portMap.put("48", PortNumber.portNumber(5));
        portMap.put("49", PortNumber.portNumber(5));
        portMap.put("410", PortNumber.portNumber(5));
        portMap.put("411", PortNumber.portNumber(5));

        portMap.put("51", PortNumber.portNumber(5));
        portMap.put("52", PortNumber.portNumber(5));
        portMap.put("53", PortNumber.portNumber(5));
        portMap.put("54", PortNumber.portNumber(5));
        portMap.put("55", PortNumber.portNumber(5));
        portMap.put("56", PortNumber.portNumber(5));
        portMap.put("57", PortNumber.portNumber(5));
        portMap.put("58", PortNumber.portNumber(5));
        portMap.put("59", PortNumber.portNumber(5));
        portMap.put("510", PortNumber.portNumber(5));
        portMap.put("511", PortNumber.portNumber(5));

        portMap.put("61", PortNumber.portNumber(4));
        portMap.put("62", PortNumber.portNumber(4));
        portMap.put("63", PortNumber.portNumber(4));
        portMap.put("64", PortNumber.portNumber(4));
        portMap.put("65", PortNumber.portNumber(4));
        portMap.put("66", PortNumber.portNumber(4));
        portMap.put("67", PortNumber.portNumber(4));
        portMap.put("68", PortNumber.portNumber(4));
        portMap.put("69", PortNumber.portNumber(4));
        portMap.put("610", PortNumber.portNumber(4));
        portMap.put("611", PortNumber.portNumber(4));
    }

    public static HashMap<String, PortNumber> getPortMap() {
        return portMap;
    }

    private MyUtil() {
    }

    public static HashMap<String, MacAddress> getHostMap() {
        return hostMap;
    }

    public static HashMap<String, DeviceId> getSwMap() {
        return swMap;
    }

}
