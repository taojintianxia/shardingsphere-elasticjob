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

package org.apache.shardingsphere.elasticjob.lifecycle.internal.statistics;

import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.elasticjob.infra.handler.sharding.JobInstance;
import org.apache.shardingsphere.elasticjob.infra.yaml.YamlEngine;
import org.apache.shardingsphere.elasticjob.kernel.internal.storage.JobNodePath;
import org.apache.shardingsphere.elasticjob.lifecycle.api.ServerStatisticsAPI;
import org.apache.shardingsphere.elasticjob.lifecycle.domain.ServerBriefInfo;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server statistics API implementation class.
 */
@RequiredArgsConstructor
public final class ServerStatisticsAPIImpl implements ServerStatisticsAPI {
    
    private final CoordinatorRegistryCenter regCenter;
    
    @Override
    public int getServersTotalCount() {
        Set<String> servers = new HashSet<>();
        for (String jobName : regCenter.getChildrenKeys("/")) {
            JobNodePath jobNodePath = new JobNodePath(jobName);
            servers.addAll(regCenter.getChildrenKeys(jobNodePath.getServerNodePath()));
        }
        return servers.size();
    }
    
    @Override
    public Collection<ServerBriefInfo> getAllServersBriefInfo() {
        ConcurrentHashMap<String, ServerBriefInfo> servers = new ConcurrentHashMap<>();
        for (String jobName : regCenter.getChildrenKeys("/")) {
            JobNodePath jobNodePath = new JobNodePath(jobName);
            for (String each : regCenter.getChildrenKeys(jobNodePath.getServerNodePath())) {
                servers.putIfAbsent(each, new ServerBriefInfo(each));
                ServerBriefInfo serverInfo = servers.get(each);
                if ("DISABLED".equalsIgnoreCase(regCenter.get(jobNodePath.getServerNodePath(each)))) {
                    serverInfo.getDisabledJobsNum().incrementAndGet();
                }
                serverInfo.getJobNames().add(jobName);
                serverInfo.setJobsNum(serverInfo.getJobNames().size());
            }
            List<String> instances = regCenter.getChildrenKeys(jobNodePath.getInstancesNodePath());
            for (String each : instances) {
                JobInstance jobInstance = YamlEngine.unmarshal(regCenter.get(jobNodePath.getInstanceNodePath(each)), JobInstance.class);
                if (null != jobInstance) {
                    ServerBriefInfo serverInfo = servers.get(jobInstance.getServerIp());
                    if (null != serverInfo) {
                        serverInfo.getInstances().add(each);
                        serverInfo.setInstancesNum(serverInfo.getInstances().size());
                    }
                }
            }
        }
        List<ServerBriefInfo> result = new ArrayList<>(servers.values());
        Collections.sort(result);
        return result;
    }
}
