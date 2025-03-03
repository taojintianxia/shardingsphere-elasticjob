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

package org.apache.shardingsphere.elasticjob.kernel.internal.server;

import org.apache.shardingsphere.elasticjob.infra.handler.sharding.JobInstance;
import org.apache.shardingsphere.elasticjob.kernel.internal.schedule.JobRegistry;
import org.apache.shardingsphere.elasticjob.kernel.internal.schedule.JobScheduleController;
import org.apache.shardingsphere.elasticjob.kernel.internal.storage.JobNodeStorage;
import org.apache.shardingsphere.elasticjob.kernel.util.ReflectionUtils;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServerServiceTest {
    
    @Mock
    private CoordinatorRegistryCenter regCenter;
    
    @Mock
    private JobScheduleController jobScheduleController;
    
    @Mock
    private JobNodeStorage jobNodeStorage;
    
    private ServerService serverService;
    
    @BeforeEach
    void setUp() {
        JobRegistry.getInstance().addJobInstance("test_job", new JobInstance("127.0.0.1@-@0", null, "127.0.0.1"));
        serverService = new ServerService(null, "test_job");
        ServerNode serverNode = new ServerNode("test_job");
        ReflectionUtils.setFieldValue(serverService, "serverNode", serverNode);
        ReflectionUtils.setFieldValue(serverService, "jobNodeStorage", jobNodeStorage);
    }
    
    @Test
    void assertPersistOnlineForInstanceShutdown() {
        JobRegistry.getInstance().shutdown("test_job");
        serverService.persistOnline(false);
        verify(jobNodeStorage, times(0)).fillJobNode("servers/127.0.0.1", ServerStatus.DISABLED.name());
    }
    
    @Test
    void assertPersistOnlineForDisabledServer() {
        JobRegistry.getInstance().registerRegistryCenter("test_job", regCenter);
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController);
        serverService.persistOnline(false);
        verify(jobNodeStorage).fillJobNode("servers/127.0.0.1", ServerStatus.DISABLED.name());
        JobRegistry.getInstance().shutdown("test_job");
    }
    
    @Test
    void assertPersistOnlineForEnabledServer() {
        JobRegistry.getInstance().registerRegistryCenter("test_job", regCenter);
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController);
        serverService.persistOnline(true);
        verify(jobNodeStorage).fillJobNode("servers/127.0.0.1", ServerStatus.ENABLED.name());
        JobRegistry.getInstance().shutdown("test_job");
    }
    
    @Test
    void assertHasAvailableServers() {
        when(jobNodeStorage.getJobNodeChildrenKeys("servers")).thenReturn(Arrays.asList("127.0.0.1", "127.0.0.2", "127.0.0.3"));
        when(jobNodeStorage.getJobNodeData("servers/127.0.0.1")).thenReturn(ServerStatus.DISABLED.name());
        when(jobNodeStorage.getJobNodeData("servers/127.0.0.2")).thenReturn(ServerStatus.ENABLED.name());
        when(jobNodeStorage.getJobNodeData("servers/127.0.0.3")).thenReturn(ServerStatus.ENABLED.name());
        when(jobNodeStorage.getJobNodeChildrenKeys("instances")).thenReturn(Collections.singletonList("127.0.0.3@-@0"));
        assertTrue(serverService.hasAvailableServers());
    }
    
    @Test
    void assertHasNotAvailableServers() {
        when(jobNodeStorage.getJobNodeChildrenKeys("servers")).thenReturn(Arrays.asList("127.0.0.1", "127.0.0.2"));
        when(jobNodeStorage.getJobNodeData("servers/127.0.0.1")).thenReturn(ServerStatus.DISABLED.name());
        when(jobNodeStorage.getJobNodeData("servers/127.0.0.2")).thenReturn(ServerStatus.DISABLED.name());
        assertFalse(serverService.hasAvailableServers());
    }
    
    @Test
    void assertIsNotAvailableServerWhenDisabled() {
        when(jobNodeStorage.getJobNodeData("servers/127.0.0.1")).thenReturn(ServerStatus.DISABLED.name());
        assertFalse(serverService.isAvailableServer("127.0.0.1"));
    }
    
    @Test
    void assertIsNotAvailableServerWithoutOnlineInstances() {
        when(jobNodeStorage.getJobNodeChildrenKeys("instances")).thenReturn(Collections.singletonList("127.0.0.2@-@0"));
        when(jobNodeStorage.getJobNodeData("servers/127.0.0.1")).thenReturn(ServerStatus.ENABLED.name());
        assertFalse(serverService.isAvailableServer("127.0.0.1"));
    }
    
    @Test
    void assertIsAvailableServer() {
        when(jobNodeStorage.getJobNodeChildrenKeys("instances")).thenReturn(Collections.singletonList("127.0.0.1@-@0"));
        when(jobNodeStorage.getJobNodeData("servers/127.0.0.1")).thenReturn(ServerStatus.ENABLED.name());
        assertTrue(serverService.isAvailableServer("127.0.0.1"));
    }
    
    @Test
    void assertIsNotEnableServer() {
        when(jobNodeStorage.getJobNodeData("servers/127.0.0.1")).thenReturn("", ServerStatus.DISABLED.name());
        assertFalse(serverService.isEnableServer("127.0.0.1"));
    }
    
    @Test
    void assertIsEnableServer() {
        when(jobNodeStorage.getJobNodeData("servers/127.0.0.1")).thenReturn("", ServerStatus.ENABLED.name());
        assertTrue(serverService.isEnableServer("127.0.0.1"));
    }
    
    @Test
    void assertServerNodeAbsent() {
        assertFalse(serverService.isEnableServer("127.0.0.1"));
    }
}
