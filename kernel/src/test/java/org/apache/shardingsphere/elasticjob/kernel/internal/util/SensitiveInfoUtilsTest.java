/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.elasticjob.kernel.internal.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class SensitiveInfoUtilsTest {
    
    @Test
    void assertFilterContentWithoutIp() {
        List<String> actual = Arrays.asList("/simpleElasticDemoJob/servers", "/simpleElasticDemoJob/leader");
        assertThat(SensitiveInfoUtils.filterSensitiveIps(actual), is(actual));
    }
    
    @Test
    void assertFilterContentWithSensitiveIp() {
        List<String> actual = Arrays.asList("/simpleElasticDemoJob/servers/127.0.0.1", "/simpleElasticDemoJob/servers/192.168.0.1/hostName | 192.168.0.1",
                "/simpleElasticDemoJob/servers/192.168.0.11", "/simpleElasticDemoJob/servers/192.168.0.111");
        List<String> expected = Arrays.asList("/simpleElasticDemoJob/servers/ip1", "/simpleElasticDemoJob/servers/ip2/hostName | ip2",
                "/simpleElasticDemoJob/servers/ip3", "/simpleElasticDemoJob/servers/ip4");
        assertThat(SensitiveInfoUtils.filterSensitiveIps(actual), is(expected));
    }
}
