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

import org.junit.Test;

/**
 * Set of tests of the ONOS application component.
 */
public class AppComponentTest {

    private AppComponent component;

    @Test
    public void testS() throws InterruptedException {
        /*String str = "\"1234\"";
        String substring = str.substring(1, str.length() - 1);
        System.out.println(str);
        System.out.println(substring);*/

      /*  String str = "\n" +
                "{\n" +
                "  \"specifier\": [\"45678\",\"443\",\"192.168.1.1\",\"192.168.1.2\",\"TCP\"],\n" +
                "  \"stats\": [\n" +
                "    \"min_pkt\",\n" +
                "    \"max_pkt\",\n" +
                "    \"mean_pkt\",\n" +
                "    \"var_pkt\",\n" +
                "    \"min_idt\",\n" +
                "    \"max_idt\",\n" +
                "    \"mean_idt\",\n" +
                "    \"var_idt\"\n" +
                "  ]\n" +
                "}";
        ConcurrentLinkedQueue<String> flowClq = new ConcurrentLinkedQueue<>();
        Thread thread = new Thread(new ClassifyModuleThread(str, flowClq));
        thread.start();
        thread.join();*/
//        String str = "\n" +
//                "{\n" +
//                "  \"specifier\": [\"45678\",\"443\",\"192.168.1.1\",\"192.168.1.2\",\"TCP\"],\n" +
//                "  \"stats\": [\n" +
//                "    \"min_pkt\",\n" +
//                "    \"max_pkt\",\n" +
//                "    \"mean_pkt\",\n" +
//                "    \"var_pkt\",\n" +
//                "    \"min_idt\",\n" +
//                "    \"max_idt\",\n" +
//                "    \"mean_idt\",\n" +
//                "    \"var_idt\"\n" +
//                "  ]\n" +
//                "}";
//        JsonNode jsonNode = null;
//        try {
//            jsonNode = new ObjectMapper().readTree(str);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        JsonNode specifier = jsonNode.get("specifier");
//        Iterator<JsonNode> iterator = specifier.iterator();
//        ArrayList<String> arrayList = new ArrayList<>();
//        while (iterator.hasNext()) {
//            arrayList.add(iterator.next().toString());
//        }
//        System.out.println(arrayList.size());
//        System.out.println(arrayList);
      /*  String str = "{\"res\":     1}";
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(str);
            JsonNode jsonNode1 = jsonNode.get("res");
            System.out.println(jsonNode1.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }*/
        /*String routingStr1 = JsonString.routingStr1;
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(routingStr1);
            JsonNode node = jsonNode.get("res");
            ArrayList<List<String>> arrayLists = new ArrayList<>();
            if (node.isArray()) {
                Iterator<JsonNode> iterator = node.iterator();
                while (iterator.hasNext()) {
                    JsonNode next = iterator.next();
                    String str = next.toString();
                    CharSequence charSequence = str.subSequence(1, str.length()-1);
                    String[] split = charSequence.toString().split(",");
                    List<String> list1 = Arrays.asList(split);
                    arrayLists.add(list1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
  /*  @Test
    public void testA() {
        String str = "[1,2,5,4,3,0]";
        CharSequence charSequence = str.subSequence(1, str.length()-1);
        String[] split = charSequence.toString().split(",");
        List<String> strings = Arrays.asList(split);
        System.out.println(strings);
    }*/

 /*   @Before
    public void setUp() {
        component = new AppComponent();
        component.activate();

export MAVEN_HOME=/home/sdn/Applications/apache-maven-3.3.9
export PATH=${MAVEN_HOME}/bin:${PATH}
    }

    @After
    public void tearDown() {
        component.deactivate();
    }

    @Test
    public void basics() {

    }
*/
}
