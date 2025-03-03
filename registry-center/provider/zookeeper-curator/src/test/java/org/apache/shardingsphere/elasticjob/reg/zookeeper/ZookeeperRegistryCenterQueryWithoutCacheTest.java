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

package org.apache.shardingsphere.elasticjob.reg.zookeeper;

import org.apache.shardingsphere.elasticjob.reg.zookeeper.fixture.EmbedTestingServer;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.util.ZookeeperRegistryCenterTestUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZookeeperRegistryCenterQueryWithoutCacheTest {
    
    private static final ZookeeperConfiguration ZOOKEEPER_CONFIGURATION =
            new ZookeeperConfiguration(EmbedTestingServer.getConnectionString(), ZookeeperRegistryCenterQueryWithoutCacheTest.class.getName());
    
    private static ZookeeperRegistryCenter zkRegCenter;
    
    @BeforeAll
    static void setUp() {
        EmbedTestingServer.start();
        ZOOKEEPER_CONFIGURATION.setConnectionTimeoutMilliseconds(30000);
        zkRegCenter = new ZookeeperRegistryCenter(ZOOKEEPER_CONFIGURATION);
        zkRegCenter.init();
        ZookeeperRegistryCenterTestUtil.persist(zkRegCenter);
        zkRegCenter.addCacheData("/other");
    }
    
    @AfterAll
    static void tearDown() {
        zkRegCenter.close();
    }
    
    @Test
    void assertGetFromServer() {
        assertThat(zkRegCenter.get("/test"), is("test"));
        assertThat(zkRegCenter.get("/test/deep/nested"), is("deepNested"));
    }
    
    @Test
    void assertGetChildrenKeys() {
        assertThat(zkRegCenter.getChildrenKeys("/test"), is(Arrays.asList("deep", "child")));
        assertThat(zkRegCenter.getChildrenKeys("/test/deep"), is(Collections.singletonList("nested")));
        assertThat(zkRegCenter.getChildrenKeys("/test/child"), is(Collections.<String>emptyList()));
        assertThat(zkRegCenter.getChildrenKeys("/test/notExisted"), is(Collections.<String>emptyList()));
    }
    
    @Test
    void assertGetNumChildren() {
        assertThat(zkRegCenter.getNumChildren("/test"), is(2));
        assertThat(zkRegCenter.getNumChildren("/test/deep"), is(1));
        assertThat(zkRegCenter.getNumChildren("/test/child"), is(0));
        assertThat(zkRegCenter.getNumChildren("/test/notExisted"), is(0));
    }
    
    @Test
    void assertIsExisted() {
        assertTrue(zkRegCenter.isExisted("/test"));
        assertTrue(zkRegCenter.isExisted("/test/deep/nested"));
        assertFalse(zkRegCenter.isExisted("/notExisted"));
    }
    
    @Test
    void assertGetRegistryCenterTime() {
        long regCenterTime = zkRegCenter.getRegistryCenterTime("/_systemTime/current");
        assertTrue(regCenterTime <= System.currentTimeMillis());
        long updatedRegCenterTime = zkRegCenter.getRegistryCenterTime("/_systemTime/current");
        assertTrue(regCenterTime < updatedRegCenterTime);
    }
    
    @Test
    void assertGetWithoutNode() {
        assertNull(zkRegCenter.get("/notExisted"));
    }
}
