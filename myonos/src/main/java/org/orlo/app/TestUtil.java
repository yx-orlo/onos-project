package org.orlo.app;

import org.onlab.packet.IpPrefix;
import org.onlab.packet.MacAddress;
import org.onosproject.net.DeviceId;
import org.onosproject.net.PortNumber;

import java.util.HashMap;

/**
 * 测试节点的映射配置文件.
 */
public final class TestUtil {
    // 对switch的映射
    private static HashMap<String, DeviceId> swMap = new HashMap<>();
    // 对host mac的映射
    private static HashMap<String, MacAddress> hostMacMap = new HashMap<>();
    // 对host ip的映射
    private static HashMap<String, IpPrefix> hostIpMap = new HashMap<>();
    // 对host接入对应swithc的端口的映射
    private static HashMap<String, PortNumber> portMap = new HashMap<>();

    // IP对switch的映射
    private static HashMap<String, DeviceId> ip2swMap = new HashMap<>();
    static {
        swMap.put("0", DeviceId.deviceId("of:0000000000000001"));
        swMap.put("1", DeviceId.deviceId("of:0000000000000002"));
        swMap.put("2", DeviceId.deviceId("of:0000000000000003"));
        swMap.put("3", DeviceId.deviceId("of:0000000000000004"));
        swMap.put("4", DeviceId.deviceId("of:0000000000000005"));
        swMap.put("5", DeviceId.deviceId("of:0000000000000006"));
        swMap.put("6", DeviceId.deviceId("of:0000000000000007"));
        swMap.put("7", DeviceId.deviceId("of:0000000000000008"));
        swMap.put("8", DeviceId.deviceId("of:0000000000000009"));

        hostMacMap.put("0", MacAddress.valueOf("00:00:00:00:00:01"));
        hostMacMap.put("1", MacAddress.valueOf("00:00:00:00:00:02"));
        hostMacMap.put("2", MacAddress.valueOf("00:00:00:00:00:03"));
        hostMacMap.put("3", MacAddress.valueOf("00:00:00:00:00:04"));
        hostMacMap.put("4", MacAddress.valueOf("00:00:00:00:00:05"));
        hostMacMap.put("5", MacAddress.valueOf("00:00:00:00:00:06"));
        hostMacMap.put("6", MacAddress.valueOf("00:00:00:00:00:07"));
        hostMacMap.put("7", MacAddress.valueOf("00:00:00:00:00:08"));
        hostMacMap.put("8", MacAddress.valueOf("00:00:00:00:00:09"));

        hostIpMap.put("0", IpPrefix.valueOf("10.0.0.1/32"));
        hostIpMap.put("1", IpPrefix.valueOf("10.0.0.2/32"));
        hostIpMap.put("2", IpPrefix.valueOf("10.0.0.3/32"));
        hostIpMap.put("3", IpPrefix.valueOf("10.0.0.4/32"));
        hostIpMap.put("4", IpPrefix.valueOf("10.0.0.5/32"));
        hostIpMap.put("5", IpPrefix.valueOf("10.0.0.6/32"));
        hostIpMap.put("6", IpPrefix.valueOf("10.0.0.7/32"));
        hostIpMap.put("7", IpPrefix.valueOf("10.0.0.8/32"));
        hostIpMap.put("8", IpPrefix.valueOf("10.0.0.9/32"));

        portMap.put("0", PortNumber.portNumber(3));
        portMap.put("1", PortNumber.portNumber(4));
        portMap.put("2", PortNumber.portNumber(3));
        portMap.put("3", PortNumber.portNumber(4));
        portMap.put("4", PortNumber.portNumber(5));
        portMap.put("5", PortNumber.portNumber(4));
        portMap.put("6", PortNumber.portNumber(3));
        portMap.put("7", PortNumber.portNumber(4));
        portMap.put("8", PortNumber.portNumber(3));

        ip2swMap.put("10.0.0.1/32", DeviceId.deviceId("of:0000000000000001"));
        ip2swMap.put("10.0.0.2/32", DeviceId.deviceId("of:0000000000000002"));
        ip2swMap.put("10.0.0.3/32", DeviceId.deviceId("of:0000000000000003"));
        ip2swMap.put("10.0.0.4/32", DeviceId.deviceId("of:0000000000000004"));
        ip2swMap.put("10.0.0.5/32", DeviceId.deviceId("of:0000000000000005"));
        ip2swMap.put("10.0.0.6/32", DeviceId.deviceId("of:0000000000000006"));
        ip2swMap.put("10.0.0.7/32", DeviceId.deviceId("of:0000000000000007"));
        ip2swMap.put("10.0.0.8/32", DeviceId.deviceId("of:0000000000000008"));
        ip2swMap.put("10.0.0.9/32", DeviceId.deviceId("of:0000000000000009"));
    }

    private TestUtil() {

    }
    public static HashMap<String, DeviceId> getSwMap() {
        return swMap;
    }

    public static HashMap<String, MacAddress> getHostMacMap() {
        return hostMacMap;
    }

    public static HashMap<String, IpPrefix> getHostIpMap() {
        return hostIpMap;
    }

    public static HashMap<String, PortNumber> getPortMap() {
        return portMap;
    }
    public static HashMap<String, DeviceId> getIp2swMap() {
        return ip2swMap;
    }
}
