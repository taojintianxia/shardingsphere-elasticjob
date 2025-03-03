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

package org.apache.shardingsphere.elasticjob.kernel.internal.election;

import org.apache.shardingsphere.elasticjob.infra.handler.sharding.JobInstance;
import org.apache.shardingsphere.elasticjob.kernel.internal.election.LeaderService.LeaderElectionExecutionCallback;
import org.apache.shardingsphere.elasticjob.kernel.internal.schedule.JobRegistry;
import org.apache.shardingsphere.elasticjob.kernel.internal.schedule.JobScheduleController;
import org.apache.shardingsphere.elasticjob.kernel.internal.server.ServerService;
import org.apache.shardingsphere.elasticjob.kernel.internal.storage.JobNodeStorage;
import org.apache.shardingsphere.elasticjob.kernel.util.ReflectionUtils;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaderServiceTest {
    
    @Mock
    private CoordinatorRegistryCenter regCenter;
    
    @Mock
    private JobScheduleController jobScheduleController;
    
    @Mock
    private JobNodeStorage jobNodeStorage;
    
    @Mock
    private ServerService serverService;
    
    private LeaderService leaderService;
    
    @BeforeEach
    void setUp() {
        JobRegistry.getInstance().addJobInstance("test_job", new JobInstance("127.0.0.1@-@0", null, "127.0.0.1"));
        leaderService = new LeaderService(null, "test_job");
        ReflectionUtils.setFieldValue(leaderService, "jobNodeStorage", jobNodeStorage);
        ReflectionUtils.setFieldValue(leaderService, "serverService", serverService);
    }
    
    @Test
    void assertElectLeader() {
        leaderService.electLeader();
        verify(jobNodeStorage).executeInLeader(eq("leader/election/latch"), ArgumentMatchers.<LeaderElectionExecutionCallback>any());
    }
    
    @Test
    void assertIsLeaderUntilBlockWithLeader() {
        JobRegistry.getInstance().registerRegistryCenter("test_job", regCenter);
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController);
        when(jobNodeStorage.isJobNodeExisted("leader/election/instance")).thenReturn(true);
        when(jobNodeStorage.getJobNodeData("leader/election/instance")).thenReturn("127.0.0.1@-@0");
        assertTrue(leaderService.isLeaderUntilBlock());
        verify(jobNodeStorage, times(0)).executeInLeader(eq("leader/election/latch"), ArgumentMatchers.<LeaderElectionExecutionCallback>any());
        JobRegistry.getInstance().shutdown("test_job");
    }
    
    @Test
    void assertIsLeaderUntilBlockWithoutLeaderAndAvailableServers() {
        when(jobNodeStorage.isJobNodeExisted("leader/election/instance")).thenReturn(false);
        assertFalse(leaderService.isLeaderUntilBlock());
        verify(jobNodeStorage, times(0)).executeInLeader(eq("leader/election/latch"), ArgumentMatchers.<LeaderElectionExecutionCallback>any());
    }
    
    @Test
    void assertIsLeaderUntilBlockWithoutLeaderWithAvailableServers() {
        when(jobNodeStorage.isJobNodeExisted("leader/election/instance")).thenReturn(false, true);
        assertFalse(leaderService.isLeaderUntilBlock());
        verify(jobNodeStorage, times(0)).executeInLeader(eq("leader/election/latch"), ArgumentMatchers.<LeaderElectionExecutionCallback>any());
    }
    
    @Test
    void assertIsLeaderUntilBlockWhenHasLeader() {
        JobRegistry.getInstance().registerRegistryCenter("test_job", regCenter);
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController);
        when(jobNodeStorage.isJobNodeExisted("leader/election/instance")).thenReturn(false, true);
        when(serverService.hasAvailableServers()).thenReturn(true);
        when(serverService.isAvailableServer("127.0.0.1")).thenReturn(true);
        when(jobNodeStorage.getJobNodeData("leader/election/instance")).thenReturn("127.0.0.1@-@0");
        assertTrue(leaderService.isLeaderUntilBlock());
        verify(jobNodeStorage).executeInLeader(eq("leader/election/latch"), ArgumentMatchers.<LeaderElectionExecutionCallback>any());
        JobRegistry.getInstance().shutdown("test_job");
    }
    
    @Test
    void assertIsLeader() {
        JobRegistry.getInstance().registerRegistryCenter("test_job", regCenter);
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController);
        when(jobNodeStorage.getJobNodeData("leader/election/instance")).thenReturn("127.0.0.1@-@0");
        assertTrue(leaderService.isLeader());
        JobRegistry.getInstance().shutdown("test_job");
    }
    
    @Test
    void assertHasLeader() {
        when(jobNodeStorage.isJobNodeExisted("leader/election/instance")).thenReturn(true);
        assertTrue(leaderService.hasLeader());
    }
    
    @Test
    void assertRemoveLeader() {
        leaderService.removeLeader();
        verify(jobNodeStorage).removeJobNodeIfExisted("leader/election/instance");
    }
    
    @Test
    void assertElectLeaderExecutionCallbackWithLeader() {
        when(jobNodeStorage.isJobNodeExisted("leader/election/instance")).thenReturn(true);
        leaderService.new LeaderElectionExecutionCallback().execute();
        verify(jobNodeStorage, times(0)).fillEphemeralJobNode("leader/election/instance", "127.0.0.1@-@0");
    }
    
    @Test
    void assertElectLeaderExecutionCallbackWithoutLeader() {
        leaderService.new LeaderElectionExecutionCallback().execute();
        verify(jobNodeStorage).fillEphemeralJobNode("leader/election/instance", "127.0.0.1@-@0");
    }
}
