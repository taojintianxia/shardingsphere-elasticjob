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

package org.apache.shardingsphere.elasticjob.kernel.internal.reconcile;

import com.google.common.collect.Lists;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.infra.handler.sharding.JobInstance;
import org.apache.shardingsphere.elasticjob.kernel.internal.config.ConfigurationService;
import org.apache.shardingsphere.elasticjob.kernel.internal.schedule.JobRegistry;
import org.apache.shardingsphere.elasticjob.kernel.internal.sharding.ShardingService;
import org.apache.shardingsphere.elasticjob.kernel.util.ReflectionUtils;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReconcileServiceTest {
    
    @Mock
    private ConfigurationService configService;
    
    @Mock
    private ShardingService shardingService;
    
    @Mock
    private CoordinatorRegistryCenter regCenter;
    
    private ReconcileService reconcileService;
    
    @BeforeEach
    void setup() {
        JobRegistry.getInstance().addJobInstance("test_job", new JobInstance("127.0.0.1@-@0"));
        reconcileService = new ReconcileService(regCenter, "test_job");
        ReflectionUtils.setFieldValue(reconcileService, "lastReconcileTime", 1L);
        ReflectionUtils.setFieldValue(reconcileService, "configService", configService);
        ReflectionUtils.setFieldValue(reconcileService, "shardingService", shardingService);
    }
    
    @Test
    void assertReconcile() {
        when(configService.load(true)).thenReturn(JobConfiguration.newBuilder("test_job", 3).cron("0/1 * * * * ?").reconcileIntervalMinutes(1).build());
        when(shardingService.isNeedSharding()).thenReturn(false);
        when(shardingService.hasShardingInfoInOfflineServers()).thenReturn(true);
        reconcileService.runOneIteration();
        verify(shardingService).isNeedSharding();
        verify(shardingService).hasShardingInfoInOfflineServers();
        verify(shardingService).setReshardingFlag();
    }
    
    @Test
    void assertReconcileWithStaticSharding() {
        when(configService.load(true)).thenReturn(JobConfiguration.newBuilder("test_job", 3).cron("0/1 * * * * ?").reconcileIntervalMinutes(1).staticSharding(true).build());
        when(shardingService.isNeedSharding()).thenReturn(false);
        when(shardingService.hasShardingInfoInOfflineServers()).thenReturn(true);
        when(regCenter.getChildrenKeys("/test_job/sharding")).thenReturn(Lists.newArrayList("0"));
        reconcileService.runOneIteration();
        verify(shardingService).isNeedSharding();
        verify(shardingService).hasShardingInfoInOfflineServers();
        verify(shardingService, times(0)).setReshardingFlag();
    }
}
