/*
 * Copyright 2020 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.orlo.app;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onlab.packet.Ethernet;
import org.onlab.packet.IPv4;
import org.onlab.packet.IpPrefix;
import org.onlab.packet.MacAddress;
import org.onlab.packet.TpPort;
import org.onlab.packet.VlanId;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.DeviceId;
import org.onosproject.net.PortNumber;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.DefaultFlowRule;
import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onosproject.net.flow.FlowEntry;
import org.onosproject.net.flow.FlowRuleOperation;
import org.onosproject.net.flow.FlowRuleOperations;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.flow.criteria.Criterion;
import org.onosproject.net.flowobjective.DefaultForwardingObjective;
import org.onosproject.net.flowobjective.FlowObjectiveService;
import org.onosproject.net.flowobjective.ForwardingObjective;
import org.onosproject.net.host.HostService;
import org.onosproject.net.packet.PacketService;
import org.onosproject.net.topology.Topology;
import org.onosproject.net.topology.TopologyEdge;
import org.onosproject.net.topology.TopologyGraph;
import org.onosproject.net.topology.TopologyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
public class AppComponent {

    private final  Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationId appId;
    private HashMap<String, DeviceId> swMap = MyUtil.getSwMap();
    private HashMap<String, MacAddress> hostMap = MyUtil.getHostMap();
    private HashMap<String, PortNumber> portMap = MyUtil.getPortMap();
    private HashMap<String, PortNumber> testPortMap = TestUtil.getPortMap();
    private HashMap<String, DeviceId> testSwMap = TestUtil.getSwMap();
    private HashMap<String, IpPrefix> testHostIpMap = TestUtil.getHostIpMap();
    private HashMap<String, MacAddress> testHostMacMap = TestUtil.getHostMacMap();
    private HashMap<String, DeviceId> testip2swMap = TestUtil.getIp2swMap();
    private HashMap<String, PortNumber> stringPortNumberHashMap = new HashMap<>();
    private int flowTableCnt = 0;
    private int timesCnt = 0;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected TopologyService topologyService;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected PacketService packetService;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected HostService hostService;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowObjectiveService flowObjectiveService;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowRuleService flowRuleService;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    private ExecutorService executorService = Executors.newFixedThreadPool(6);
    private Timer timer = new Timer();
    private HashMap<String, HashMap<String, Long>> matrixMapStore = new HashMap<>();
    private  ConcurrentLinkedQueue<String> routingClq = new ConcurrentLinkedQueue<>();
    private  ConcurrentLinkedQueue<String> flowClq = new ConcurrentLinkedQueue<>();

    @Activate
    protected void activate() {
        appId = coreService.registerApplication("org.orlo.app");
        log.info("------------Activate Started-------------------------");
        //设置相邻switch间的端口信息
        setPortInfo();
        //初始化流量矩阵Map
        initmatrixMapStore();
        //安装与host直连switch的默认路由
        testinstallHostToSW();
        //安装table1与table2的默认流表项
        testInstallTcpTable();
        //开启获取host信息的线程,并且配置table0流表信息会添加到线程安全队列中
        Thread hostThread = new Thread(new HostModuleThread(flowClq));
        hostThread.start();
        //开启定时上传流量矩阵的任务,间隔为15s,不限次数
        timeMission(15, -1);
        //开启下发优化路由的线程
        Thread installRoutingThread = new Thread(new RoutingFlowThread());
        installRoutingThread.start();
        //开启安装入口交换机table0流表项的线程
        Thread installFlowThread = new Thread(new InstallFlowByClqThread());
        installFlowThread.start();
        log.info("----------------Activated end-------------------------");
    }

    @Deactivate
    protected void deactivate() {
        executorService.shutdown();
        timer.cancel();
        log.info("--------------------System Stopped-------------------------");
    }

    private void readSwInfo(int flowTableNum) {
        JsonObject members = parsePath(flowTableNum);
        log.info(members.toString());
        log.info("-----------------------------");
    }

    /**
     * 设置switch间port的信息.
     */
    private void setPortInfo() {
        Topology topology = topologyService.currentTopology();
        TopologyGraph graph = topologyService.getGraph(topology);
        Set<TopologyEdge> edges = graph.getEdges();
        for (TopologyEdge edge : edges) {
            ConnectPoint src = edge.link().src();
            ConnectPoint dst = edge.link().dst();
            String s1 = src.deviceId().toString() + '-' + dst.deviceId().toString();
            stringPortNumberHashMap.put(s1, src.port());
        }
    }
    /**
     * 获取相邻switch间连接的port.
     * @param srcId
     * @param dstId
     * @return
     */
    private PortNumber getPortInfo(DeviceId srcId, DeviceId dstId) {
        String s = srcId.toString() + '-' + dstId.toString();
        if (stringPortNumberHashMap.containsKey(s)) {
            return stringPortNumberHashMap.get(s);
        } else {
            log.error("------交换机不相邻-------");
            return PortNumber.portNumber(0);
        }
    }

    /**
     * 解析json文件中的路由信息.
     * @param flowTableNum
     * @return
     */
    private  JsonObject parsePath(int flowTableNum) {
        String filePath = "/home/sdn/Delay_infoJ.txt";
        String jsonContent = new ReadFile.FileUtil().readFile(filePath);
        JsonNode flowTableArray = null;
        try {
            flowTableArray = new ObjectMapper().readTree(jsonContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonNode aFrameDict = flowTableArray.get(flowTableNum);
        Iterator<String> stringIterator = aFrameDict.fieldNames();
        JsonObject recordAll = new JsonObject();
        String s1, s2;
        int t;
        PortNumber portInfo = PortNumber.portNumber(0);
        while (stringIterator.hasNext()) {
            String key = stringIterator.next();
            JsonNode item = aFrameDict.get(key);
            String out = "";
            Iterator<String> stringIterator1 = item.fieldNames();
            while (stringIterator1.hasNext()) {
                String ikey = stringIterator1.next();
                s1 = ikey.split("-")[0];
                s2 = ikey.split("-")[1];
                portInfo = getPortInfo(swMap.get(s1), swMap.get(s2));
                t = item.get(ikey).asInt();
                out += s1 + ":" +  portInfo.toString() + "*" + t + "-";
            }
            recordAll.set(key, out);
        }
        return recordAll;
    }

    /**
     * 安装host到连接switch的默认流表.
     */
    private void installHostToSW() {
        for (String s : hostMap.keySet()) {
            MacAddress dst = hostMap.get(s);
            DeviceId deviceId = swMap.get(s);
            PortNumber port = portMap.get(s);
            TrafficSelector.Builder selectBuilder = DefaultTrafficSelector.builder();
            TrafficTreatment.Builder trafficBuilder = DefaultTrafficTreatment.builder();
            selectBuilder.matchEthDst(dst);
            trafficBuilder.setOutput(port);
            DefaultForwardingObjective.Builder objBuilder = DefaultForwardingObjective.builder();
            objBuilder.withSelector(selectBuilder.build())
                    .withTreatment(trafficBuilder.build())
                    .withPriority(40000)
                    .withFlag(ForwardingObjective.Flag.VERSATILE)
                    .fromApp(appId)
                    .makeTemporary(6000);
            flowObjectiveService.apply(deviceId, objBuilder.add());

        }
        log.info("----------HostToSW complete----------------");
    }

    /**
     * 根据源目mac地址安装流表项.
     * @param src
     * @param dst
     * @param deviceId
     * @param port
     * @param time
     */
    private void installRule(MacAddress src, MacAddress dst, DeviceId deviceId, PortNumber port, int time) {
        TrafficSelector.Builder selectBuilder = DefaultTrafficSelector.builder();
        TrafficTreatment.Builder trafficBuilder = DefaultTrafficTreatment.builder();
        selectBuilder.matchEthSrc(src);
        selectBuilder.matchEthDst(dst);
        trafficBuilder.setOutput(port);
        DefaultForwardingObjective.Builder objBuilder = DefaultForwardingObjective.builder();
        objBuilder.withSelector(selectBuilder.build())
                     .withTreatment(trafficBuilder.build())
                     .withPriority(1000)
                     .withFlag(ForwardingObjective.Flag.VERSATILE)
                     .fromApp(appId)
                     .makeTemporary(5 * time);
           flowObjectiveService.apply(deviceId, objBuilder.add());
        FlowRuleOperation flo;

    }

    /**
     * 安装一个时间片的所有路由信息，一共有44个时间片.
     * @param flowTableNum
     */
    private void installFrame(int flowTableNum) {
        log.info("------------------start install---------------------------");
        JsonObject frame = parsePath(flowTableNum);
//        "611to51":"611:1*12-511:4*12-"
        int cnt = 0;
        for (String key : frame.names()) {
            String value = frame.get(key).asString();
            MacAddress src = hostMap.get(key.split("to")[0]);
            MacAddress dst = hostMap.get(key.split("to")[1]);
            String[] split = value.split("-");
            for (String s : split) {
                DeviceId deviceId = swMap.get(s.split(":")[0]);
                String s1 = s.split(":")[1];
                PortNumber port = PortNumber.portNumber(s1.split("\\*")[0]);
                int time = Integer.parseInt(s.split("\\*")[1]);
                installRule(src, dst, deviceId, port, time);
                cnt++;
            }
        }
        log.info("-----------------install complete---------------------");
        log.info("--------total:" + String.valueOf(cnt) + "-------------");
    }

    /**
     * 用schedule实现自动安装流表项.
     */
    private void updateCnt() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("---------install:" + flowTableCnt + "----------");
                installFrame(flowTableCnt);
                log.info("-------------complete------------------");
                flowTableCnt++;
            }
        }, 1000, 5000);
        while (true) {
            if (flowTableCnt >= 43) {
                break;
            }
        }
        timer.cancel();
    }

    /* 上面的方法可以忽略（没有调用）,下面的方法才是主要的.
     * *********************************************************************************************************
     *
     */

    /**
     * 通过五元组信息以及分类信息安装流表项到table0.此流表项优先级最高.
     * @param srcPort
     * @param dstPort
     * @param srcIp
     * @param dstIP
     * @param protocal
     * @param vlanId
     * @param deviceId
     */
    public void installBy5Tuple(String srcPort, String dstPort, String srcIp, String dstIP, String protocal,
                                String vlanId, DeviceId deviceId) {
        byte proto = IPv4.PROTOCOL_TCP;
        if (protocal.equals("UDP")) {
            proto = IPv4.PROTOCOL_UDP;
        }
        DefaultFlowRule.Builder ruleBuilder = DefaultFlowRule.builder();
        TrafficSelector.Builder selectBuilder = DefaultTrafficSelector.builder();
        selectBuilder.matchEthType(Ethernet.TYPE_IPV4)
                .matchVlanId(VlanId.ANY)
                .matchTcpSrc(TpPort.tpPort(Integer.parseInt(srcPort)))
                .matchTcpDst(TpPort.tpPort(Integer.parseInt(dstPort)))
                .matchIPSrc(IpPrefix.valueOf(srcIp))
                .matchIPDst(IpPrefix.valueOf(dstIP))
                .matchIPProtocol(proto);
        TrafficTreatment.Builder trafficBuilder = DefaultTrafficTreatment.builder();
        trafficBuilder.setVlanId(VlanId.vlanId(Short.parseShort(vlanId)))
                    .transition(1);
        ruleBuilder.withSelector(selectBuilder.build())
                .withTreatment(trafficBuilder.build())
                .withPriority(55000)
                .forTable(0)
                .fromApp(appId)
                .makeTemporary(100)
                .forDevice(deviceId);
        FlowRuleOperations.Builder flowRulebuilder = FlowRuleOperations.builder();
        flowRulebuilder.add(ruleBuilder.build());
        flowRuleService.apply(flowRulebuilder.build());
    }

    /**
     * 通过源目的IP信息，以及Switch间的Port信息安装流表项，安装到table2，优先级在table2中最高.
     * @param srcIP
     * @param dstIP
     * @param port
     * @param vlanid
     * @param deviceId
     */
    public void installFlow2Table2(IpPrefix srcIP, IpPrefix dstIP, PortNumber port,
                                         String vlanid, DeviceId deviceId) {
        DefaultFlowRule.Builder ruleBuilder = DefaultFlowRule.builder();
        TrafficSelector.Builder selectBuilder = DefaultTrafficSelector.builder();
        selectBuilder.matchEthType(Ethernet.TYPE_IPV4)
                .matchIPProtocol(IPv4.PROTOCOL_TCP)
                .matchIPSrc(srcIP)
                .matchIPDst(dstIP)
                .matchVlanId(VlanId.vlanId(Short.parseShort(vlanid)));
        TrafficTreatment.Builder trafficBuilder = DefaultTrafficTreatment.builder();
        trafficBuilder.setOutput(port);
        ruleBuilder.withSelector(selectBuilder.build())
                .withPriority(20000)
                .withTreatment(trafficBuilder.build())
                .forTable(2)
                .fromApp(appId)
                .makeTemporary(500)
                .forDevice(deviceId);
        FlowRuleOperations.Builder flowRulebuilder = FlowRuleOperations.builder();
        flowRulebuilder.add(ruleBuilder.build());
        flowRuleService.apply(flowRulebuilder.build());
    }

    /**
     * 通过List安装流表项,调用了installFlow2Table2()方法.
     * @param routingList
     * @param vlanid
     */
    public void installFlow2Table2ByList(List<String> routingList,  String vlanid) {
        int size = routingList.size() - 1;
        int src = 0;
        int dst = 0;
        String srcSW = routingList.get(0);
        String dstSW = routingList.get(size);
        IpPrefix srcIp = testHostIpMap.get(srcSW);
        IpPrefix dstIP = testHostIpMap.get(dstSW);
        for (int i = 0; i < size - 1; i++) {
            src = i;
            dst = src + 1;
            PortNumber port = getPortInfo(testSwMap.get(routingList.get(src)),
                    testSwMap.get(routingList.get(dst)));
            DeviceId deviceId = testSwMap.get(routingList.get(src));
            installFlow2Table2(srcIp, dstIP, port, vlanid, deviceId);
        }

    }

    /**
     * 安装table1中的流表项，完全用于统计流量矩阵信息，没有其它作用。
     * 根据源目的IP以及vland信息来统计.
     * @param srcIP
     * @param dstIP
     * @param vlanId
     * @param deviceId
     */
    public void installFlowTable1(IpPrefix srcIP, IpPrefix dstIP, String vlanId, DeviceId deviceId) {
            DefaultFlowRule.Builder ruleBuilder = DefaultFlowRule.builder();
            TrafficSelector.Builder selectBuilder = DefaultTrafficSelector.builder();
            selectBuilder.matchEthType(Ethernet.TYPE_IPV4)
                    .matchIPProtocol(IPv4.PROTOCOL_TCP)
                    .matchIPSrc(srcIP)
                    .matchIPDst(dstIP)
                    .matchVlanId(VlanId.vlanId(Short.parseShort(vlanId)));
            TrafficTreatment.Builder trafficBuilder = DefaultTrafficTreatment.builder();
            trafficBuilder.transition(2);
            ruleBuilder.withSelector(selectBuilder.build())
                    .withPriority(20000)
                    .withTreatment(trafficBuilder.build())
                    .forTable(1)
                    .fromApp(appId)
                    .makePermanent()
                    .forDevice(deviceId);
            FlowRuleOperations.Builder flowRulebuilder = FlowRuleOperations.builder();
            flowRulebuilder.add(ruleBuilder.build());
            flowRuleService.apply(flowRulebuilder.build());
    }

    /**
     * 安装table2的默认流表，没有匹配到的流，会交给控制器处理，优先级在table2中最低.
     * @param deviceId
     */
    private void tcpToController(DeviceId deviceId) {
        DefaultFlowRule.Builder ruleBuilder = DefaultFlowRule.builder();
        TrafficSelector.Builder selectBuilder = DefaultTrafficSelector.builder();
        selectBuilder.matchEthType(Ethernet.TYPE_IPV4)
                .matchIPProtocol(IPv4.PROTOCOL_TCP);
        TrafficTreatment.Builder trafficBuilder = DefaultTrafficTreatment.builder();
        trafficBuilder.punt();
        ruleBuilder.withSelector(selectBuilder.build())
                .withPriority(100)
                .withTreatment(trafficBuilder.build())
                .forTable(2)
                .fromApp(appId)
                .makePermanent()
                .forDevice(deviceId);
        FlowRuleOperations.Builder flowRulebuilder = FlowRuleOperations.builder();
        flowRulebuilder.add(ruleBuilder.build());
        flowRuleService.apply(flowRulebuilder.build());
    }

    /**
     * 配置所有switch的table1与table2的默认流表项.
     */
    private void testInstallTcpTable() {
        Set<String> keySet = testSwMap.keySet();
        for (String key : keySet) {
            DeviceId deviceId = testSwMap.get(key);
            IpPrefix srcIp = testHostIpMap.get(key);
            HashSet<String> hostSet = new HashSet<>(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8"));
            hostSet.remove(key);
            for (String dst : hostSet) {
                IpPrefix dstIp = testHostIpMap.get(dst);
                //安装table1中的测量流表
                installFlowTable1(srcIp, dstIp, "1", deviceId);
                installFlowTable1(srcIp, dstIp, "2", deviceId);
            }
            //配置table2中上传控制器的流表.
            tcpToController(deviceId);
        }
    }

    /**
     * 初始化存储流量矩阵的Map.
     */
    private void initmatrixMapStore() {
        for (int i = 0; i < 9; i++) {
            HashMap<String, Long> map = new HashMap<>();
            map.put("1", 0L);
            map.put("2", 0L);
            map.put("3", 0L);
            map.put("4", 0L);
            map.put("5", 0L);
            map.put("6", 0L);
            map.put("7", 0L);
            map.put("8", 0L);
            map.put("9", 0L);
            map.put("10", 0L);
            map.put("11", 0L);
            map.put("12", 0L);
            map.put("13", 0L);
            map.put("14", 0L);
            map.put("15", 0L);
            map.put("16", 0L);
            map.put("17", 0L);
            map.put("18", 0L);
            matrixMapStore.put(String.valueOf(i), map);
        }

    }

    /**
     *  获取流量矩阵.
     * @return
     */
    public String getTraficMatrix() {
        Set<String> keySet = testSwMap.keySet();
        HashMap<String, HashMap<String, Long>> matrixMap1 = new HashMap<>();
        for (String key : keySet) {
            DeviceId deviceId = testSwMap.get(key);
            Iterable<FlowEntry> flowEntries = flowRuleService.getFlowEntries(deviceId);
            Iterator<FlowEntry> iterator = flowEntries.iterator();
            HashMap<String, Long> stringLongHashMap1 = new HashMap<>();
            stringLongHashMap1.put("1", 0L);
            stringLongHashMap1.put("2", 0L);
            stringLongHashMap1.put("3", 0L);
            stringLongHashMap1.put("4", 0L);
            stringLongHashMap1.put("5", 0L);
            stringLongHashMap1.put("6", 0L);
            stringLongHashMap1.put("7", 0L);
            stringLongHashMap1.put("8", 0L);
            stringLongHashMap1.put("9", 0L);
            stringLongHashMap1.put("10", 0L);
            stringLongHashMap1.put("11", 0L);
            stringLongHashMap1.put("12", 0L);
            stringLongHashMap1.put("13", 0L);
            stringLongHashMap1.put("14", 0L);
            stringLongHashMap1.put("15", 0L);
            stringLongHashMap1.put("16", 0L);
            stringLongHashMap1.put("17", 0L);
            stringLongHashMap1.put("18", 0L);

            while (iterator.hasNext()) {
                FlowEntry flowEntry = iterator.next();
                TrafficSelector selector = flowEntry.selector();
                Criterion vlanIdcriterion = selector.getCriterion(Criterion.Type.VLAN_VID);
                //有vlanid才进行下一步操作
                if (vlanIdcriterion != null && flowEntry.tableId() == 1) {
                    char cVlanid = vlanIdcriterion.toString().charAt(9);
                    Criterion dstIpcriterion = selector.getCriterion(Criterion.Type.IPV4_DST);
                    char c = dstIpcriterion.toString().charAt(16);
                    if (flowEntry.priority() == 20000 && cVlanid == '1') {
                        long bytes = flowEntry.bytes();
                        String s = String.valueOf(c);
                        stringLongHashMap1.put(s, bytes);
                    }
                    if (flowEntry.priority() == 20000 && cVlanid == '2') {
                        long bytes = flowEntry.bytes();
                        String s = String.valueOf(c);
                        String s1 = String.valueOf(Integer.parseInt(s) + 9);
                        stringLongHashMap1.put(s1, bytes);
                    }
                }
//                iterator.remove();
                }
            matrixMap1.put(key, stringLongHashMap1);
        }

        //遍历matrixMap1，与marixMapStore 获取他们间的差值，再存储为一个map
        HashMap<String, HashMap<String, Long>> subHashMap = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            HashMap<String, Long> stringLongHashMap = matrixMap1.get(String.valueOf(i));
            HashMap<String, Long> stringLongHashMapStore = matrixMapStore.get(String.valueOf(i));
            HashMap<String, Long> newHashMap = new HashMap<>();
            for (int j = 1; j < 19; j++) {
                Long aLong = stringLongHashMap.get(String.valueOf(j));
                Long aLongOld = stringLongHashMapStore.get(String.valueOf(j));
                newHashMap.put(String.valueOf(j), aLong - aLongOld);
            }
            subHashMap.put(String.valueOf(i), newHashMap);
        }
        //更新marixMapstore里的值
        matrixMapStore.putAll(matrixMap1);

        JsonObject matrixRes = new JsonObject();
        JsonArray jsonArray = new JsonArray();

        for (int key = 0; key < 9; key++) {
            HashMap<String, Long> hashMap = subHashMap.get(String.valueOf(key));
            int f = key + 1;
            for (int i = 1; i < 10; i++) {
                if (i != f) {
                    jsonArray.add(hashMap.get(String.valueOf(i)));
                }
            }
        }

        for (int key = 0; key < 9; key++) {
            HashMap<String, Long> hashMap = subHashMap.get(String.valueOf(key));
            int f = key + 1;
            for (int i = 1; i < 10; i++) {
                if (i != f) {
                    jsonArray.add(hashMap.get(String.valueOf(i + 9)));
                }
            }
        }
        matrixRes.set("volumes", jsonArray);
        log.info("----------------------the matrix had been get-------------------------------");
        return matrixRes.toString();
    }

    /**
     * 安装与host相连接switch到host的默认路由,安装在了table0中,优先级最高.
     * 即当目的IP匹配到是交换机所直接连接的host,则直接将数据包交给host.
     */
    private void testinstallHostToSW() {
        for (String s : testHostMacMap.keySet()) {
            IpPrefix ipPrefix = testHostIpMap.get(s);
            DeviceId deviceId = testSwMap.get(s);
            PortNumber port = testPortMap.get(s);
            TrafficSelector.Builder selectBuilder = DefaultTrafficSelector.builder();
            TrafficTreatment.Builder trafficBuilder = DefaultTrafficTreatment.builder();
            selectBuilder.matchEthType(Ethernet.TYPE_IPV4)
                        .matchIPDst(ipPrefix);
            trafficBuilder.setOutput(port);
            DefaultForwardingObjective.Builder objBuilder = DefaultForwardingObjective.builder();
            objBuilder.withSelector(selectBuilder.build())
                    .withTreatment(trafficBuilder.build())
                    .withPriority(60000)
                    .withFlag(ForwardingObjective.Flag.VERSATILE)
                    .fromApp(appId)
                    .makePermanent();
            flowObjectiveService.apply(deviceId, objBuilder.add());

        }
        log.info("----------testHostToSW complete----------------");
    }

/*    *//*
      通过源地址，来将数据包打上不同的vlandId.
      @param srcIp
     * @param vlanId
     * @param deviceId
     *//*
    private void testInstallTcpTable0(IpPrefix srcIp, String vlanId, DeviceId deviceId) {
        DefaultFlowRule.Builder ruleBuilder = DefaultFlowRule.builder();
        TrafficSelector.Builder selectBuilder = DefaultTrafficSelector.builder();
        selectBuilder.matchEthType(Ethernet.TYPE_IPV4)
                .matchIPProtocol(IPv4.PROTOCOL_TCP)
                .matchIPSrc(srcIp)
                .matchVlanId(VlanId.ANY);

        TrafficTreatment.Builder trafficBuilder = DefaultTrafficTreatment.builder();
        trafficBuilder.transition(1)
                        .setVlanId(VlanId.vlanId(Short.parseShort(vlanId)));
        ruleBuilder.withSelector(selectBuilder.build())
                .withTreatment(trafficBuilder.build())
                .withPriority(50000)
                .forTable(0)
                .fromApp(appId)
                .makePermanent()
                .forDevice(deviceId);
        FlowRuleOperations.Builder flowRulebuilder = FlowRuleOperations.builder();
        flowRulebuilder.add(ruleBuilder.build());
        flowRuleService.apply(flowRulebuilder.build());
    }

    private void testInstallTable0() {
        testInstallTcpTable0(testHostIpMap.get("0"), "1", testSwMap.get("0"));
        testInstallTcpTable0(testHostIpMap.get("1"), "1", testSwMap.get("1"));
        testInstallTcpTable0(testHostIpMap.get("2"), "1", testSwMap.get("2"));
        testInstallTcpTable0(testHostIpMap.get("3"), "1", testSwMap.get("3"));
        testInstallTcpTable0(testHostIpMap.get("4"), "1", testSwMap.get("4"));
        testInstallTcpTable0(testHostIpMap.get("5"), "2", testSwMap.get("5"));
        testInstallTcpTable0(testHostIpMap.get("6"), "2", testSwMap.get("6"));
        testInstallTcpTable0(testHostIpMap.get("7"), "2", testSwMap.get("7"));
        testInstallTcpTable0(testHostIpMap.get("8"), "2", testSwMap.get("8"));
    }*/

    /**
     * 通过ArrayList<List<String>> lists来安装优化的路由信息.
     * @param lists
     */
    public void testInstallRoutingByLists(ArrayList<List<String>> lists) {
        int size = lists.size();
        // 判断如果没有144条路由,则报错
        if (size != 144) {
            log.error("------the number of routing flow entries is wrong----------");
            return;
        }
        for (int i = 0; i < 143; i++) {
            String vlanid = "1";
            if (i >= 72) {
                vlanid = "2";
            }
            List<String> list = lists.get(i);
            installFlow2Table2ByList(list, vlanid);
        }
    }

    /**
     * 定时任务,用于上传流量矩阵,并获取优化后的路由.
     * @param interval
     * @param count  为-1时,代表无限循环.
     */
    private void timeMission(int interval, int count) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                    log.info("---------upload flow Matrix count " + timesCnt + "----------");
                    String  traficMatrix = getTraficMatrix();
                    executorService.submit(new FlowMarixThread(traficMatrix));
                    timesCnt++;
                if (count != -1 && timesCnt > count) {
                    timesCnt = 0;
                    cancel();
                }
            }
        }, 1000 * interval, 1000 * interval);
    }

    /**
     * 上传流量矩阵线程，并且获取返回的优化路由信息.
     */
    public class FlowMarixThread implements Runnable {
        private String matrixMap;
        public FlowMarixThread(String matrixMap) {
            this.matrixMap = matrixMap;
        }
        @Override
        public void run() {
            try {
                SocketChannel socketChannel = SocketChannel.open();
                socketChannel.connect(new InetSocketAddress("192.168.137.1", 1027));
//            socketChannel.connect(new InetSocketAddress("172.16.181.1", 1027));
                ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
                byteBuffer.put(matrixMap.getBytes());
                byteBuffer.flip();
                socketChannel.write(byteBuffer);
                byteBuffer.clear();
                //接收数据
                int len = 0;
                StringBuilder stringBuilder = new StringBuilder();
                while ((len = socketChannel.read(byteBuffer)) > 0) {
                    byteBuffer.flip();
                    String res = new String(byteBuffer.array(), 0, len);
                    byteBuffer.clear();
                    stringBuilder.append(res);
                }
                routingClq.offer(stringBuilder.toString());
                socketChannel.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过获取优化路由信息下发流表项.
     */
    public  class RoutingFlowThread implements Runnable {
        @Override
        public void run() {
            // 线程会一直运行,除非被中断掉.
            while (!Thread.currentThread().isInterrupted()) {
            while (!routingClq.isEmpty()) {
                log.info("-----------install routing flow entries---------------");
                String routingInfo = routingClq.poll();
                try {
                    JsonNode jsonNode = new ObjectMapper().readTree(routingInfo);
                    JsonNode node = jsonNode.get("res");
                    ArrayList<List<String>> arrayLists = new ArrayList<>();
                    if (node.isArray()) {
                        for (JsonNode next : node) {
                            String str = next.toString();
                            CharSequence charSequence = str.subSequence(1, str.length() - 1);
                            String[] split = charSequence.toString().split(",");
                            List<String> list1 = Arrays.asList(split);
                            arrayLists.add(list1);
                        }
                    }
                    testInstallRoutingByLists(arrayLists);
                    log.info("-------------routing has complete------------------");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
      }
    }

    /**
     * 通过flowClq中的信息安装流表项，此线程会一直存在，只要flowClq非空，就会取出数据，安装流表.
     */
    public class InstallFlowByClqThread implements Runnable {
        @Override
        public void run() {
            //线程会一直运行，除非被中断掉
            while (!Thread.currentThread().isInterrupted()) {
                while (!flowClq.isEmpty()) {
                    String poll = flowClq.poll();
                    try {
                        JsonNode jsonNode = new ObjectMapper().readTree(poll);
                        JsonNode specifier = jsonNode.get("specifier");
                        Iterator<JsonNode> iterator = specifier.iterator();
                        ArrayList<String> arrayList = new ArrayList<>();
                        while (iterator.hasNext()) {
                            arrayList.add(iterator.next().toString());
                        }
                        if (arrayList.size() == 5) {
                            String srcPort = arrayList.get(0);
                            String dstPort = arrayList.get(1);
                            String srcIP = arrayList.get(2) + "/32";
                            String dstIP = arrayList.get(3) + "/32";
                            String protocol = arrayList.get(4);
                            String vlanid = jsonNode.get("res").toString();
                            installBy5Tuple(srcPort, dstPort, srcIP, dstIP, protocol, vlanid, testip2swMap.get(srcIP));
                        } else {
                            log.error("----------------five tuple info error------------------------");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}

