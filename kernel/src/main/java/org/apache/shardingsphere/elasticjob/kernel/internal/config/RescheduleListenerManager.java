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

package org.apache.shardingsphere.elasticjob.kernel.internal.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.infra.pojo.JobConfigurationPOJO;
import org.apache.shardingsphere.elasticjob.infra.yaml.YamlEngine;
import org.apache.shardingsphere.elasticjob.kernel.internal.listener.AbstractListenerManager;
import org.apache.shardingsphere.elasticjob.kernel.internal.schedule.JobRegistry;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.listener.DataChangedEvent;
import org.apache.shardingsphere.elasticjob.reg.listener.DataChangedEvent.Type;
import org.apache.shardingsphere.elasticjob.reg.listener.DataChangedEventListener;

/**
 * Reschedule listener manager.
 */
public final class RescheduleListenerManager extends AbstractListenerManager {
    
    private final ConfigurationNode configNode;
    
    private final String jobName;
    
    public RescheduleListenerManager(final CoordinatorRegistryCenter regCenter, final String jobName) {
        super(regCenter, jobName);
        this.jobName = jobName;
        configNode = new ConfigurationNode(jobName);
    }
    
    @Override
    public void start() {
        addDataListener(new CronSettingAndJobEventChangedJobListener());
    }
    
    class CronSettingAndJobEventChangedJobListener implements DataChangedEventListener {
        
        @Override
        public void onChange(final DataChangedEvent event) {
            if (configNode.isConfigPath(event.getKey()) && Type.UPDATED == event.getType() && !JobRegistry.getInstance().isShutdown(jobName)) {
                JobConfiguration jobConfiguration = YamlEngine.unmarshal(event.getValue(), JobConfigurationPOJO.class).toJobConfiguration();
                if (StringUtils.isEmpty(jobConfiguration.getCron())) {
                    JobRegistry.getInstance().getJobScheduleController(jobName).rescheduleJob();
                } else {
                    JobRegistry.getInstance().getJobScheduleController(jobName).rescheduleJob(jobConfiguration.getCron(), jobConfiguration.getTimeZone());
                }
            }
        }
    }
}
