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
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Port;
import org.onosproject.net.PortNumber;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.device.PortStatistics;
import org.onosproject.net.flow.DefaultFlowRule;
import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onosproject.net.flow.FlowEntry;
import org.onosproject.net.flow.FlowRuleOperation;
import org.onosproject.net.flow.FlowRuleOperations;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;
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
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
public class AppComponent {

    private final Logger log = LoggerFactory.getLogger(getClass());
//    private final TopologyListener topologyListener = new InternalTopologyListener();
    private ApplicationId appId;
    private HashMap<String, DeviceId> swMap = MyUtil.getSwMap();
    private HashMap<String, MacAddress> hostMap = MyUtil.getHostMap();
    private HashMap<String, PortNumber> portMap = MyUtil.getPortMap();
    private HashMap<String, PortNumber> stringPortNumberHashMap = new HashMap<>();
    private int flowTableCnt = 0;
//    private PacketProcessor processor = new MyProcessor();
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

    @Activate
    protected void activate() {
        appId = coreService.registerApplication("org.orlo.app");
//        packetService.addProcessor(processor, PacketProcessor.ADVISOR_MAX + 2);
//        topologyService.getGraph()
        log.info("Started-------------------------");
//        createSwMap();
//        installRule();
        setPortInfo();
//        installHostToSW();
//        updateCnt();
//        sendCommonFlowtable("10.0.1.1/24", "10.0.6.9/24", "2", 5, swMap.get("611"));
//        sendFlowtableByFT(hostMap.get("35"), hostMap.get("15"), "3", 3, swMap.get("611"));
        sendFlowtable0("8888", "9999", "10.0.1.1/24", "10.0.6.1/24", "1", swMap.get("611"));
//        getStatistics(swMap.get("611"));
        log.info("Activated end-------------------------");

    }

    @Deactivate
    protected void deactivate() {
//        packetService.removeProcessor(processor);
//        processor = null;
        log.info("Stopped-------------------------");
    }

    private void readSwInfo(int flowTableNum) {
        JsonObject members = parsePath(flowTableNum);
        log.info(members.toString());
        log.info("-----------------------------");
    }

    /**
     * 列出switch间port的信息.
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

/*    private void createSwMap() {
        Topology topology = topologyService.currentTopology();
        log.info(topology.toString());
        TopologyGraph graph = topologyService.getGraph(topology);
        for (TopologyVertex topologyVertex : graph.getVertexes()) {
            String s = topologyVertex.deviceId().toString();
            log.info(s);
        }
        log.info("---------------------------------");
        HashMap<String, DeviceId> swMap = MyUtil.getSwMap();
        DeviceId s110 = swMap.get("s110");
        Device device = deviceService.getDevice(s110);
        log.info(device.toString());
        log.info("------------end------------------");

    }*/
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

    public void sendFlowtableByFT(MacAddress src, MacAddress dst,
                                    String outPort, int tableNum, DeviceId deviceId) {
        DefaultFlowRule.Builder ruleBuilder = DefaultFlowRule.builder();
        TrafficSelector.Builder selectBuilder = DefaultTrafficSelector.builder();
        selectBuilder.matchEthType(Ethernet.TYPE_IPV4)
                        .matchIPProtocol(IPv4.PROTOCOL_UDP)
                        .matchIPSrc(IpPrefix.valueOf("10.0.5.4/24"))
                        .matchIPDst(IpPrefix.valueOf("10.0.1.5/24"))
                        .matchUdpSrc(TpPort.tpPort(6666))
                        .matchUdpDst(TpPort.tpPort(2333));

        TrafficTreatment.Builder trafficBuilder = DefaultTrafficTreatment.builder();
        trafficBuilder.setOutput(PortNumber.portNumber("3"));
        ruleBuilder.withSelector(selectBuilder.build())
                .withTreatment(trafficBuilder.build())
                .withPriority(10000)
                .forTable(tableNum)
                .fromApp(appId)
                .makeTemporary(5000)
                .forDevice(deviceId);
        FlowRuleOperations.Builder flowRulebuilder = FlowRuleOperations.builder();
        flowRulebuilder.add(ruleBuilder.build());
        flowRuleService.apply(flowRulebuilder.build());
    }

    public void sendFlowtable0(String srcPort, String dstPort, String srcIp, String dstIP,
                                     String vlanId, DeviceId deviceId) {
        DefaultFlowRule.Builder ruleBuilder = DefaultFlowRule.builder();
        TrafficSelector.Builder selectBuilder = DefaultTrafficSelector.builder();
        selectBuilder.matchEthType(Ethernet.TYPE_IPV4)
                .matchVlanId(VlanId.ANY)
                .matchTcpSrc(TpPort.tpPort(Integer.parseInt(srcPort)))
                .matchTcpDst(TpPort.tpPort(Integer.parseInt(dstPort)))
                .matchIPSrc(IpPrefix.valueOf(srcIp))
                .matchIPDst(IpPrefix.valueOf(dstIP))
                .matchIPProtocol(IPv4.PROTOCOL_TCP);
        TrafficTreatment.Builder trafficBuilder = DefaultTrafficTreatment.builder();
        trafficBuilder.setVlanId(VlanId.vlanId(Short.parseShort(vlanId)));
        ruleBuilder.withSelector(selectBuilder.build())
                .withTreatment(trafficBuilder.build())
                .withPriority(1000)
                .forTable(0)
                .fromApp(appId)
                .makeTemporary(5000)
                .forDevice(deviceId);
        FlowRuleOperations.Builder flowRulebuilder = FlowRuleOperations.builder();
        flowRulebuilder.add(ruleBuilder.build());
        flowRuleService.apply(flowRulebuilder.build());
    }

    public void getStatistics(DeviceId deviceId) {
        Iterable<FlowEntry> flowEntries = flowRuleService.getFlowEntries(deviceId);
        Iterator<FlowEntry> iterator = flowEntries.iterator();
        while (iterator.hasNext()) {
            FlowEntry flowEntry = iterator.next();
            log.info("--------------------");
            if (flowEntry.tableId() == 0) {
                log.info(String.valueOf(flowEntry.packets()));
                log.info(String.valueOf(flowEntry.bytes()));
            }
            log.info("--------------------");

        }


    }

    public void socketClient() {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("192.168.137.1", 9999));
            socketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_READ);
            ClientSendThread clientSendThread = new ClientSendThread(socketChannel);
            Thread thread = new Thread(clientSendThread);
            thread.start();
            while (selector.select() > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();
                    if (next.isReadable()) {
                        SocketChannel channel = (SocketChannel) next.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int len = 0;
                        while ((len = channel.read(buffer)) > 0) {
                            buffer.flip();
                            System.out.println(new String(buffer.array(),0,len));
                            buffer.clear();
                        }
                    }

                    iterator.remove();
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void myport() {
        log.info("-----------------------------------------------------------------------");
        Iterable<Device> devices = deviceService.getDevices();
        for (Device d : devices) {
            log.info("#### [viswa] Device id " + d.id().toString());
            List<Port> ports = deviceService.getPorts(d.id());
            List<PortStatistics> portStatistics = deviceService.getPortStatistics(d.id());
            List<PortStatistics> portDeltaStatistics = deviceService.getPortDeltaStatistics(d.id());
        /*    for(Port port : ports) {
                log.info("Getting info for port" + port.number());
                PortStatistics portstat = deviceService.getSpecificPortStatistics(d.id(), port.number());
                PortStatistics portdeltastat = deviceService.getSpecificPortDeltaStatistics(d.id(), port.number());
                if(portstat != null)
                    log.info("portstat bytes recieved" + portstat.bytesReceived());
                else
                    log.info("Unable to read portStats");

                if(portdeltastat != null)
                    log.info("portdeltastat bytes recieved" + portdeltastat.bytesReceived());
                else
                    log.info("Unable to read portDeltaStats");
            }*/

            /*
                List<PortStatistics> portStatisticsList = deviceService.getPortDeltaStatistics(d.id());
                for (PortStatistics portStats : portStatisticsList) {
                    try {
                        int port = portStats.port();
                        //log.info("#### Creating object for " + port);
                        portStatsReaderTask task = new portStatsReaderTask();
                        //Timer timer = new Timer();
                        task.setDelay(3);
                        task.setExit(false);
                        task.setLog(log);
                        task.setPort(port);
                        //task.setTimer(timer);
                        task.setDeviceService(deviceService);
                        task.setDevice(d);
                        map.put(port, task);
                        //log.info("#### Creating object for " + port + " Before calling scheulde()");
                        task.schedule();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } */

        }
    }

}
